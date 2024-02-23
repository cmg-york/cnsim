import csv
from collections import defaultdict

class BlockChainTracker:
    def __init__(self):
        self.node_chains = defaultdict(list)  # Stores the chains of blocks for each node
        self.block_details = {}  # Stores block details for backtracking

    def add_block(self, node_id, block_id, parent_id, height, sim_time, evt_type, block_content):
        # Only add the block if it was appended to the chain
        if evt_type in ['Appended On Chain (parentless)', 'Appended On Chain (w/ parent)'] or parent_id == -1:
            # Store block details
            self.block_details[block_id] = {'parent_id': parent_id, 'height': height, 'sim_time': sim_time, 'block_content': block_content}
            # Append block to the node's chain, assuming linear progression for simplicity
            self.node_chains[node_id].append(block_id)

    def get_longest_chain(self, node_id, upto_sim_time):
        # Find the block with the maximum height up to the given sim_time
        max_height = -1
        max_height_block_id = None
        for block_id in self.node_chains[node_id]:
            block = self.block_details[block_id]
            if block['sim_time'] <= upto_sim_time and block['height'] > max_height:
                max_height = block['height']
                max_height_block_id = block_id

        # Backtrack from the block with the maximum height to reconstruct the chain
        longest_chain = []
        current_block_id = max_height_block_id
        while current_block_id is not None and current_block_id in self.block_details:
            block = self.block_details[current_block_id]
            if block['sim_time'] > upto_sim_time:
                break  # Ensure we don't include blocks beyond the specified sim_time
            longest_chain.insert(0, (current_block_id, block['parent_id'], block['height'], block['sim_time'], block['block_content']))
            current_block_id = block['parent_id'] if block['parent_id'] != -1 else None

        return longest_chain

def process_csv_and_display_longest_chain(csv_filename):
    tracker = BlockChainTracker()

    with open(csv_filename, mode='r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        sorted_rows = sorted(csv_reader, key=lambda row: int(row['SimTime']))

        # Initialize variables to find the start and end times
        start_time = float('inf')
        end_time = 0

        for row in sorted_rows:
            sim_time = int(row['SimTime'])
            tracker.add_block(
                node_id=int(row[' NodeID']),
                block_id=int(row[' BlockID']),
                parent_id=int(row[' ParentID']),
                height=int(row[' Height']),
                sim_time=sim_time,
                evt_type=row[' EvtType'],
                block_content=row[' BlockContent']
            )
            # Update the end time to the latest sim_time
            end_time = max(end_time, sim_time)

        item_to_check = int(input("Please enter the integer to check in the block content: "))
    n_slices = int(input("Please enter the number of slices (N): "))
    
    # Loop to find the start time when the target transaction was first added
    for block_id, details in tracker.block_details.items():
        if str(item_to_check) in details['block_content'].strip('{}').split(';'):
            start_time = min(start_time, details['sim_time'])

    # Handle case where the item is not found in any block
    if start_time == float('inf'):
        print(f"The item {item_to_check} was not found in any block.")
        return

    # Calculate slice duration
    slice_duration = (end_time - start_time) / n_slices

    for i in range(n_slices):
        slice_end_time = start_time + slice_duration * (i + 1)
        print(f"Checking for presence of item {item_to_check} up to SimTime {slice_end_time}:")

        for node_id in tracker.node_chains:
            longest_chain = tracker.get_longest_chain(node_id, slice_end_time)
            # Update the check to properly access block content in the tuple
            item_found = any(str(item_to_check) in block[4].strip('{}').split(';') for block in longest_chain)
            if item_found:
                print(f" - Node {node_id}: Item is in the longest chain.")
            else:
                print(f" - Node {node_id}: Item is not in the longest chain.")

# Example usage
csv_filename = 'BlockLog - 2024.02.12 17.32.26.csv'  # Replace with the path to your actual CSV file
process_csv_and_display_longest_chain(csv_filename)