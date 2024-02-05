# Python script to generate a CSV file with 100 transactions

import csv
import random

def generate_transactions(num_transactions):
    transactions = []

    for _ in range(num_transactions):
        transaction_arrival_interval = round(random.uniform(1.0, 500.0), 2)  # Random float between 1.0 and 3.0
        mining_interval = random.randint(900, 1100)  # Random integer between 900 and 1100
        transaction_fee_value = round(random.uniform(50.0, 60.0), 2)  # Random float between 0.01 and 0.05
        transaction_size = random.randint(200, 500)  # Random integer between 200 and 500
        transactions.append([transaction_arrival_interval, mining_interval, transaction_fee_value, transaction_size])
    
    return transactions

def write_to_csv(filename, data):
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        for row in data:
            writer.writerow(row)

# Generate 100 transactions
transactions = generate_transactions(100)

# Write the transactions to a CSV file
csv_filename = 'transactions.csv'
write_to_csv(csv_filename, transactions)

print(f"{csv_filename} with 100 transactions has been created.")


