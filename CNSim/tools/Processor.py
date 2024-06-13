import csv
from collections import defaultdict
import os

class BlockchainNode:
    def __init__(self, node_id):
        self.node_id = node_id
        self.blocks = []
        self.block_data = {}
        self.root_block_id = None  # New attribute to track the root block ID

    def add_block(self, block_id, parent_id, block_content):
        if self.root_block_id is None:
            self.root_block_id = block_id
        self.blocks.append(block_id)
        self.block_data[block_id] = (parent_id, block_content)

    def calculate_longest_chain(self):
        self.longest_chain = []
        if self.root_block_id:
            self._find_longest_chain_from(self.root_block_id, [self.root_block_id])

    def _find_longest_chain_from(self, current_block_id, current_chain):
        children = [block_id for block_id, (parent_id, _) in self.block_data.items() if parent_id == current_block_id]
        if not children:
            if len(current_chain) > len(self.longest_chain):
                self.longest_chain = current_chain[:]
            return
        for child_id in children:
            self._find_longest_chain_from(child_id, current_chain + [child_id])

    def get_longest_chain(self):
        return [(block_id, self.block_data[block_id][1]) for block_id in self.longest_chain]

    def contains_transaction(self, transaction):
        return any(transaction in content for _, content in self.get_longest_chain())


def main(fileName, startTime=None, endTime=None):
    # Read and sort CSV file data
    with open(fileName, 'r') as file:
        reader = csv.DictReader(file)
        sorted_data = sorted(reader, key=lambda row: int(row['SimTime']))

    # Filter and structure data
    append_rows = [row for row in sorted_data if 'Append' in row['EvtType']]
    filtered_data = [{k: row[k] for k in ['SimTime', 'NodeID', 'BlockID', 'ParentID', 'BlockContent']} for row in append_rows]

    # Determine startTime and endTime if not provided
    if startTime is None:
        startTime = int(filtered_data[0]['SimTime'])
    if endTime is None:
        endTime = int(filtered_data[-1]['SimTime'])

    # Initialize BlockchainNode objects
    nodes = defaultdict(lambda: BlockchainNode(None))

    # Calculate time slices
    time_slices = [(startTime + (endTime - startTime) * i / 30, startTime + (endTime - startTime) * (i + 1) / 30) for i in range(30)]
    current_slice_index = 0

    # Open CSV output file
    output_file_name = 'output.csv'

    write_header = not os.path.exists(output_file_name)
    with open(output_file_name, 'a', newline='') as output_file:
        csv_writer = csv.writer(output_file)
        if write_header:
            csv_writer.writerow(['SimID', 'SimTime', 'NodeID', 'Belief'])

        for row in filtered_data:
            sim_time = int(row['SimTime'])
            node_id = row['NodeID']
            if nodes[node_id] is None or nodes[node_id].node_id is None:
                nodes[node_id] = BlockchainNode(node_id)
            nodes[node_id].add_block(row['BlockID'], row['ParentID'], row['BlockContent'])

            # Check if the current row's SimTime exceeds the current slice
            while sim_time > time_slices[current_slice_index][1]:
                # Check if the longest chain contains the specific transaction
                for node_id, blockchain_node in nodes.items():
                    blockchain_node.calculate_longest_chain()
                    contains_transaction = blockchain_node.contains_transaction('100')
                    csv_writer.writerow([fileName, time_slices[current_slice_index][1], node_id, 1 if contains_transaction else 0])
                current_slice_index += 1
                if current_slice_index >= len(time_slices):
                    break
        
        # Write output for remaining time slices if there are no more rows in the CSV file
        last_sim_time = time_slices[-1][1]  # Last slice's end time
        for node_id, blockchain_node in nodes.items():
            blockchain_node.calculate_longest_chain()
            contains_transaction = blockchain_node.contains_transaction('100')
            csv_writer.writerow([fileName, last_sim_time, node_id, 1 if contains_transaction else 0])

if __name__ == '__main__':
    # Example of how to run with hardcoded values for demonstration
    main('BlockLog - 2024.04.08 21.31.39.csv', startTime=None, endTime=None)
