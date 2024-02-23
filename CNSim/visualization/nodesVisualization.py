import pandas as pd
import matplotlib.pyplot as plt
import networkx as nx
import os

# Reading the latest file name from 'LatestFileName.txt'
current_directory = os.getcwd()
# Navigate to two directories above the current directory
two_levels_up = os.path.abspath(os.path.join(current_directory, '..', '..'))

latest_file_directory = os.path.join(two_levels_up, 'log/')
with open(latest_file_directory + 'LatestFileName.txt', 'r') as file:
    latest_filename = file.read().strip()

# Constructing the file path for the CSV file
file_path = f'{latest_file_directory}{latest_filename}/Nodes - {latest_filename}.csv'

nodes_df = pd.read_csv(file_path)

# Correcting column names by stripping any leading/trailing spaces
nodes_df.columns = nodes_df.columns.str.strip()

# Create a graph
G = nx.Graph()

# Add nodes to the graph with hash power attribute
for index, row in nodes_df.iterrows():
    G.add_node(row['NodeID'], hash_power=row['HashPower (GH/s)'])

# Define node sizes directly proportional to hash power
min_size = 100  # Minimum size for the smallest node
hash_powers = [row['HashPower (GH/s)'] for index, row in nodes_df.iterrows()]
min_hash_power = min(hash_powers)
node_sizes = [min_size * (hash_power / min_hash_power) for hash_power in hash_powers]

# Define node colors - red for node with ID=1, others green
node_colors = ['red' if node == 1 else 'green' for node in G.nodes]

# Define positions using Kamada-Kawai layout for centralized placement
pos = nx.kamada_kawai_layout(G)

# Create a table with electricity power and cost
table_data = nodes_df[['NodeID', 'ElectricPower (W)', 'ElectricityCost (USD/kWh)']]
table_data.set_index('NodeID', inplace=True)

# Adjusting the figure layout
fig, ax = plt.subplots(1, 2, figsize=(30, 15), gridspec_kw={'width_ratios': [3, 1]})

# Draw the graph in the first subplot
nx.draw_networkx_nodes(G, pos, ax=ax[0], node_size=node_sizes, node_color=node_colors)
nx.draw_networkx_edges(G, pos, ax=ax[0])
nx.draw_networkx_labels(G, pos, ax=ax[0], labels={node: f'ID = {node}' for node in G.nodes}, font_size=8)
for node, (x, y) in pos.items():
    ax[0].text(x, y-0.02, f'HashPower = {G.nodes[node]["hash_power"]:.0f}', ha='center', va='top', fontsize=8)
ax[0].set_title("Visualization of Nodes")
ax[0].axis('on')  # Turn on axis for manual adjustment

# Manually setting the axes limits to center the nodes
x_values, y_values = zip(*pos.values())
ax[0].set_xlim(min(x_values) - 0.1, max(x_values) + 0.1)
ax[0].set_ylim(min(y_values) - 0.1, max(y_values) + 0.1)

# Display the table in the second subplot
ax[1].axis('off')  # Turn off axis for the table
ax[1].set_title("Electricity Power and Cost per Node")
table = ax[1].table(cellText=table_data.values, colLabels=table_data.columns, loc='center')
table.auto_set_font_size(False)
table.set_fontsize(9)
table.scale(0.8, 0.8)  # Scale for the table

plt.tight_layout()
plt.show()

