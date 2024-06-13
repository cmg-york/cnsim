import os
import pandas as pd
import glob
from Processor import main

# Initialize variables to track the global minimum and maximum of SimTime
global_min_time = float('inf')
global_max_time = float('-inf')

# Initialize a list to store the file paths that will be processed
processed_files = []

# Navigate through each directory in the current directory
for directory in next(os.walk('.'))[1]:
    # Construct the path pattern to match "BlockLog*" files within the directory
    path_pattern = os.path.join(directory, "BlockLog*")
    
    # Use glob to find all files matching the pattern
    for file_path in glob.glob(path_pattern):
        try:
            # Read the file into a pandas DataFrame
            df = pd.read_csv(file_path)
            
            # Filter rows where EvtType contains "Append"
            filtered_df = df[df['EvtType'].str.contains("Append", na=False)]
            
            # Check if there are any rows left after filtering
            if not filtered_df.empty:
                # Calculate the minimum and maximum of the SimTime column
                min_time = filtered_df['SimTime'].min()
                max_time = filtered_df['SimTime'].max()
                
                # Update the global minimum and maximum times
                global_min_time = min(min_time, global_min_time)
                global_max_time = max(max_time, global_max_time)
                
                # Add the file path to the list of processed files
                processed_files.append((file_path, min_time, max_time))
                
        except Exception as e:
            print(f"Error processing file {file_path}: {e}")

# Check if the initial values were updated, indicating we found valid data
if global_min_time != float('inf') and global_max_time != float('-inf'):
    print(f"Global minimum SimTime: {global_min_time}")
    print(f"Global maximum SimTime: {global_max_time}")
else:
    print("No matching data found.")

# Call the main function for each file with its specific start and end times
for file_info in processed_files:
    file_path, start_time, end_time = file_info
    main(file_path, global_min_time, global_max_time)

