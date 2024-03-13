import csv
import os
import csv

def read_and_filter_csv(input_csv_file_path):
    with open(input_csv_file_path, mode='r') as infile:
        reader = csv.DictReader(infile)
        filtered_sorted_rows = sorted(
            [row for row in reader if "Appended" in row[" EvtType"]],
            key=lambda x: int(x["SimTime"])
        )
    return filtered_sorted_rows
    
def find_first_append_time_for_transaction(rows, transaction_id):
    # Iterate through each row to find the transaction ID in the block's content
    for row in rows:
        # Remove curly braces and split the string into a list of transaction IDs
        transactions = row[" BlockContent"].strip('{}').split(';')
        if transaction_id in transactions:
            return row["SimTime"]  # Return the SimTime when the transaction was first appended
    return "Transaction ID not found in the blockchain."

def get_longest_chains_by_time(rows, sim_time):
    # Filter blocks up to the specified SimTime
    time_filtered_rows = [row for row in rows if int(row["SimTime"]) <= sim_time]
    
    # Track blocks for each node
    nodes_blocks = {}
    for row in time_filtered_rows:
        node_id = row[" NodeID"]
        block = {" BlockID": row[" BlockID"], " ParentID": row[" ParentID"], " BlockContent": row[" BlockContent"]}
        if node_id not in nodes_blocks:
            nodes_blocks[node_id] = []
        nodes_blocks[node_id].append(block)

    def find_longest_chain(node_id, blocks):
        chains = []
        for block in blocks:
            chain = [block]
            while block[" ParentID"] != "-1":
                parent_block = next((b for b in blocks if b[" BlockID"] == block[" ParentID"]), None)
                if parent_block:
                    chain.append(parent_block)
                    block = parent_block
                else:
                    break
            chains.append(chain)
        return max(chains, key=len, default=[])

    # Find the longest chain for each node with the time constraint
    longest_chains = {node_id: find_longest_chain(node_id, blocks) for node_id, blocks in nodes_blocks.items()}

    return longest_chains

def is_transaction_in_longest_chain_by_time(rows, transaction_id, sim_time):
    # Filter blocks up to the specified SimTime
    time_filtered_rows = [row for row in rows if int(row["SimTime"]) <= sim_time]
    
    # Get the longest chains for each node at the specified SimTime
    longest_chains_by_time = get_longest_chains_by_time(time_filtered_rows, sim_time)
    
    # Check if the transaction is in the longest chain of each node
    transaction_availability = {}
    for node_id, chain in longest_chains_by_time.items():
        # Split block contents and check for transaction ID
        transaction_found = any(transaction_id in block[" BlockContent"].strip("{}").split(";") for block in chain)
        transaction_availability[node_id] = transaction_found
    
    return transaction_availability
    
def find_first_and_last_sim_times_for_transaction(rows, transaction_id):
    first_sim_time = None
    last_sim_time = rows[-1]["SimTime"]  # Assume the last row has the latest SimTime
    for row in rows:
        transactions = row[" BlockContent"].strip('{}').split(';')
        if transaction_id in transactions:
            first_sim_time = row["SimTime"]
            break
    return first_sim_time, last_sim_time

def divide_time_range_into_parts(first_time, last_time, parts=20):
    first_time = int(first_time)
    last_time = int(last_time)
    step = (last_time - first_time) // parts
    return [first_time + step * i for i in range(parts + 1)]

def print_longest_chains_and_check_transaction_for_time_segments(rows, transaction_id, time_segments):
    for i in range(len(time_segments)-1):
        start_time = time_segments[i]
        end_time = time_segments[i+1]
        print(f"Time Segment {i+1}: {start_time} to {end_time}")
        longest_chains = get_longest_chains_by_time(rows, end_time)
        
        for node_id, chain in longest_chains.items():
            print(f"  Node {node_id}: Longest chain length = {len(chain)}")
            transaction_found = False
            for block in chain:
                block_id = block[" BlockID"]
                parent_id = block[" ParentID"]
                transactions = block[" BlockContent"].strip("{}").split(";")
                if transaction_id in transactions:
                    transaction_found = True
                #print(f"    BlockID: {block_id}, ParentID: {parent_id}, Transactions: {transactions}")
            print(f"    Transaction {transaction_id} is {'present' if transaction_found else 'not present'} in the longest chain.")
            print()  # Newline for better readability between nodes' chains
        print("\n" + "-"*50 + "\n")  # Separator between time segments
        
        
def write_beliefs_to_csv(output_csv_file_path, belief_data, is_header=False):
    # Use 'a' mode for appending, 'w' for the first write to include headers
    mode = 'w' if is_header else 'a'
    with open(output_csv_file_path, mode=mode, newline='') as file:
        writer = csv.writer(file)
        if is_header:
            writer.writerow(["Name of the File", "SimTime (ms)", "Node ID", "Believes"])
        writer.writerows(belief_data)

def collect_and_write_beliefs(rows, transaction_id, time_segments, output_csv_file_path, file_name):
    belief_data = []
    for i in range(len(time_segments)-1):
        start_time = time_segments[i]
        end_time = time_segments[i+1]
        longest_chains = get_longest_chains_by_time(rows, end_time)
        
        for node_id, chain in longest_chains.items():
            transaction_found = any(transaction_id in block[" BlockContent"].strip("{}").split(";") for block in chain)
            belief_data.append([file_name, end_time, node_id, int(transaction_found)])
    
    write_beliefs_to_csv(output_csv_file_path, belief_data)
    print(f"Beliefs have been written to {output_csv_file_path}.")
    
    
# Function to process each BlockLog file
def process_blocklog_file(file_path, output_file_path, transaction_id, file_name):
    filtered_sorted_rows = read_and_filter_csv(file_path)
    first_sim_time, last_sim_time = find_first_and_last_sim_times_for_transaction(filtered_sorted_rows, transaction_id)

    if first_sim_time:
        print(f"Transaction {transaction_id} first appeared at SimTime {first_sim_time} in file {file_path}.")
        time_segments = divide_time_range_into_parts(first_sim_time, last_sim_time)
        # Pass file_name to collect_and_write_beliefs
        collect_and_write_beliefs(filtered_sorted_rows, transaction_id, time_segments, output_file_path, file_name)
    else:
        print(f"Transaction ID not found in the blockchain for file {file_path}.")

# Main script
directory_path = "/home/amir/Projects/CNSim/cnsim/log"
output_file_path = 'output.csv'  # Specify your desired output file path

transaction_id = input("Enter trx ID: ")


# Initialize output file with headers
write_beliefs_to_csv(output_file_path, [], is_header=True)

for root, dirs, files in os.walk(directory_path):
    for file in files:
        if "BlockLog -" in file:
            print(f"Processing {file}...")
            file_path = os.path.join(root, file)
            # Pass file as an additional parameter to process_blocklog_file
            process_blocklog_file(file_path, output_file_path, transaction_id, file)

print("Processing completed. Outputs have been written to the output file.")