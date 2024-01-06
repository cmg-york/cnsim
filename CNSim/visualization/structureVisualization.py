import pandas as pd
import matplotlib.pyplot as plt
import networkx as nx
import pydot
from networkx.drawing.nx_pydot import graphviz_layout
import os

def is_blockchain_consistent(file_path):
    log_data = pd.read_csv(file_path)
    log_data.columns = log_data.columns.str.strip()

    grouped_data = log_data.groupby('NodeID')
    blockchain_structure = {}

    for name, group in grouped_data:
        blockchain_structure[name] = list(zip(group['BlockID'], group['ParentBlockID']))

    is_same_blockchain = all(value == list(blockchain_structure.values())[0] for value in blockchain_structure.values())
    
    return is_same_blockchain, blockchain_structure

def visualize_blockchain(blockchain_structure):
    G = nx.DiGraph()

    # Identify the genesis block ID
    genesis_block_id = None
    for _, blocks in blockchain_structure.items():
        for block_id, parent_id in blocks:
            if parent_id == -1:
                genesis_block_id = block_id
                break
        if genesis_block_id is not None:
            break

    # Adding nodes and edges
    for _, blocks in blockchain_structure.items():
        for block_id, parent_id in blocks:
            G.add_node(block_id)
            if parent_id != -1:
                G.add_edge(parent_id, block_id)

    # Use pydot to create a hierarchical layout
    pos = graphviz_layout(G, prog='dot')

    # Node colors
    node_colors = ['gold' if node == genesis_block_id else 'skyblue' for node in G]

    nx.draw(G, pos, with_labels=True, node_color=node_colors, node_size=2000, edge_color='black')
    plt.title("Blockchain Structure")
    plt.show()

# Example usage
current_directory = os.getcwd()
# Navigate to two directories above the current directory
two_levels_up = os.path.abspath(os.path.join(current_directory, '..', '..'))

latest_file_directory = os.path.join(two_levels_up, 'log/')
with open(latest_file_directory + 'LatestFileName.txt', 'r') as file:
    latest_filename = file.read().strip()
# Constructing the file path for the CSV file
file_path = f'{latest_file_directory}{latest_filename}/StructureLog - {latest_filename}.csv'

consistent, blockchain_structure = is_blockchain_consistent(file_path)

if consistent:
    print("Blockchain is consistent. Visualizing the blockchain...")
    visualize_blockchain(blockchain_structure)
else:
    print("Blockchain is NOT consistent across all nodes.")

