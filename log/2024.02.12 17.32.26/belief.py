import csv
from collections import defaultdict

def standardize_columns(row):
    return {key.replace(" ", "").lower(): value for key, value in row.items()}

def parse_log(filename):
    with open(filename, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        blocks = [standardize_columns(row) for row in reader]
    return blocks

def parse_block_content(block_content):
    # Assuming block content is a string of transaction IDs separated by semicolons
    return block_content.strip('{}').split(';')

def construct_blockchains(blocks):
    chains = defaultdict(lambda: defaultdict(list)) # chains[node_id][block_id] = block
    for block in blocks:
        node_id = block['nodeid']
        block_id = block['blockid']
        parent_id = block['parentid']
        # Parse transactions in the block
        block['transactions'] = parse_block_content(block['blockcontent'])
        if parent_id == '-1':  # Genesis block or starting point
            chains[node_id][block_id].append(block)
        else:
            # Copy the parent's chain and append the current block
            if parent_id in chains[node_id]:  # Check if the parent exists
                chains[node_id][block_id] = chains[node_id][parent_id] + [block]
            else:
                # Orphan block handling or initial block without known parent in the node
                chains[node_id][block_id].append(block)
    return chains

def find_longest_chain(chains):
    longest_chains = defaultdict(lambda: {'chain': [], 'simtime': None})
    for node_id, blocks in chains.items():
        for block_id, block_chain in blocks.items():
            if len(block_chain) > len(longest_chains[node_id]['chain']):
                longest_chains[node_id]['chain'] = block_chain
                longest_chains[node_id]['simtime'] = block_chain[-1]['simtime']
    return longest_chains

def check_belief(transaction_id, longest_chains):
    beliefs = defaultdict(list)
    for node_id, chain_info in longest_chains.items():
        chain = chain_info['chain']
        for block in chain:
            sim_time = block['simtime']
            belief = 1 if transaction_id in block['transactions'] else 0
            beliefs[node_id].append((sim_time, belief))
    return beliefs

def print_beliefs(beliefs):
    for node_id, times in sorted(beliefs.items()):
        for time, belief in sorted(times, key=lambda x: x[0]):
            print(f"{time} ms, Node {node_id}, Believes {belief}")

# Load and process the BlockLog
filename = 'BlockLog - 2024.02.12 17.32.26.csv'  # Update this path to your CSV file
transaction_id = input("Enter transaction ID: ").strip()
blocks = parse_log(filename)
chains = construct_blockchains(blocks)
longest_chains = find_longest_chain(chains)
beliefs = check_belief(transaction_id, longest_chains)
print_beliefs(beliefs)

