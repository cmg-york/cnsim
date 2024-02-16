import csv
import os
import glob

def generate_belief_table_entry(writer, simulation_id, node_id, sim_time_diff, sys_time_diff, believes):
    writer.writerow({
        'Simulation ID': simulation_id, 
        'SimTime (ms)': sim_time_diff, 
        'SysTime': sys_time_diff, 
        'Node ID': node_id, 
        'Believes': believes
    })

def get_latest_file_name(file_path):
    with open(file_path, 'r') as file:
        return file.read().strip()

def read_structure_log(file_path):
    with open(file_path, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        data = [row for row in reader]
    return data
    
def read_block_log(file_path):
    with open(file_path, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        rows = [row for row in reader]
    
    if rows:
        first_row = rows[0]
        last_row = rows[-1]
        sim_time_diff = int(last_row['SimTime']) - int(first_row['SimTime'])
        sys_time_diff = int(last_row[' SysTime']) - int(first_row[' SysTime'])
    else:
        sim_time_diff = 0
        sys_time_diff = 0
    
    return sim_time_diff, sys_time_diff

def find_longest_chain(data):
    chains = {}  # {node_id: {block_id: (parent_id, height, content)}}
    longest_chains = {}  # {node_id: (height, last_block_id)}

    for row in data:
        node_id = int(row[' NodeID'])  # Removed extra spaces around keys
        block_id = int(row[' BlockID'])
        parent_id = int(row[' ParentBlockID'])
        height = int(row[' Height'])
        content = row[' Concent']

        if node_id not in chains:
            chains[node_id] = {}
        chains[node_id][block_id] = (parent_id, height, content)

        if node_id not in longest_chains or longest_chains[node_id][0] < height:
            longest_chains[node_id] = (height, block_id)

    # Reconstruct the longest chain for each node
    longest_chains_contents = {}
    for node_id, (_, last_block_id) in longest_chains.items():
        current_block_id = last_block_id
        chain_content = []

        while current_block_id != -1:
            parent_id, height, content = chains[node_id][current_block_id]
            chain_content.append(content)
            current_block_id = parent_id

        longest_chains_contents[node_id] = chain_content[::-1]  # Reverse to get the correct order

    return longest_chains_contents

def check_number_in_node_chains(longest_chains, number):
    results = {}  # Store results for each node
    for node_id, chain_contents in longest_chains.items():
        in_chain = any(str(number) in content.replace('{', '').replace('}', '').split(';') for content in chain_contents)
        results[node_id] = in_chain
    return results
    
def process_logs(structure_log_file_path, block_log_file_path, number, writer, simulation_id):
    sim_time_diff, sys_time_diff = read_block_log(block_log_file_path)
    data = read_structure_log(structure_log_file_path)
    longest_chains = find_longest_chain(data)
    results = check_number_in_node_chains(longest_chains, number)
    
    for node_id, is_in_chain in results.items():
        believes = 1 if is_in_chain else 0
        generate_belief_table_entry(writer, simulation_id, node_id, sim_time_diff, sys_time_diff, believes)

# Main script to iterate through all directories and generate a single belief table
def main():
    output_file_path = 'BeliefTable.csv'
    number = int(input("Enter a number to check in the longest chain: "))
    simulation_id = 1  # Initialize simulation ID
    
    with open(output_file_path, 'w', newline='') as csvfile:
        fieldnames = ['Simulation ID', 'SimTime (ms)', 'SysTime', 'Node ID', 'Believes']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        # Iterate through all directories in the current path
        for directory in os.listdir('.'):
            if os.path.isdir(directory):
                structure_log_files = glob.glob(f'{directory}/StructureLog*.csv')
                block_log_files = glob.glob(f'{directory}/BlockLog*.csv')

                for structure_log_file_path, block_log_file_path in zip(structure_log_files, block_log_files):
                    process_logs(structure_log_file_path, block_log_file_path, number, writer, simulation_id)
                    simulation_id += 1  # Increment simulation ID for each processed pair of files

    print(f"Belief table generated at {output_file_path}.")

if __name__ == "__main__":
    main()
