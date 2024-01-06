import pandas as pd
import matplotlib.pyplot as plt
import os


# Path to the CSV file
#csv_file_path = '/home/amir/Projects/CNSim/cnsim/log/2024.01.05 14.04.52/Input - 2024.01.05 14.04.52.csv'
current_directory = os.getcwd()
# Navigate to two directories above the current directory
two_levels_up = os.path.abspath(os.path.join(current_directory, '..', '..'))

latest_file_directory = os.path.join(two_levels_up, 'log/')

with open(latest_file_directory + 'LatestFileName.txt', 'r') as file:
    latest_filename = file.read().strip()

# Constructing the file path for the CSV file
csv_file_path = f'{latest_file_directory}{latest_filename}/Input - {latest_filename}.csv'

# Read the CSV file
data = pd.read_csv(csv_file_path)

# Remove any extra spaces from column names
data.columns = data.columns.str.strip()

# Sorting the data by Arrival Time
data_sorted = data.sort_values(by='ArrivalTime (ms)', ascending=True)  # Ensure sorting is in ascending order

# Fixed bar width for visibility
bar_width = 30  # Set a fixed width that is visually appropriate

# Plotting the chart
plt.figure(figsize=(15, 7))
bars = plt.bar(data_sorted['ArrivalTime (ms)'], data_sorted['Value (coins)'], width=bar_width, align='center')

# Adding TxID labels to each bar
for bar, txid in zip(bars, data_sorted['TxID']):
    yval = bar.get_height()
    plt.text(bar.get_x(), yval + (0.01 * max(data_sorted['Value (coins)'])), str(txid), ha='center', va='bottom')

# Setting chart title and labels
plt.title('Transaction Value vs Arrival Time with TxID Labels')
plt.xlabel('Arrival Time (ms)')
plt.ylabel('Value (coins)')
plt.grid(True)

# Adjust x-axis limits to ensure no bars are cut off
plt.xlim(min(data_sorted['ArrivalTime (ms)']) - bar_width, max(data_sorted['ArrivalTime (ms)']) + bar_width)

# Show the plot
plt.tight_layout()  # Adjust layout to fit all labels
plt.show()

