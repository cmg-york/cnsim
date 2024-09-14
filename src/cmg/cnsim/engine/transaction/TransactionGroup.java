package cmg.cnsim.engine.transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A list containing various transactions. Can be used as a block or other needed grouping (e.g. pool).
 *
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 */
public class TransactionGroup implements ITxContainer {

    private List<Transaction> group;
    protected int groupID;
    protected float totalValue;
    protected float totalSize;

    ////////// Constructors //////////

    /**
     * Plain constructor, simply initializes the internal data structure.
     */
    public TransactionGroup() {
        group = new ArrayList<>();
    }

    /**
     * Accepts an already created ArrayList of transactions. Calculates the total value and size in the group.
     *
     * @param initial An already created ArrayList of transactions
     */
    public TransactionGroup(List<Transaction> initial) {
        group = initial;
        for (Transaction t : initial) {
            totalValue += t.getValue();
            totalSize += t.getSize();
        }
    }

    /**
     * Loads a transaction group from a text file. Each line in the file is a separate transaction.
     * Each transaction is a comma separated string containing the following information:
     * Transaction ID, Time Created, Total Value, Total Size, First Arrival NodeID.
     * Transaction ID must run from <tt>1</tt> to <tt>n</tt> strictly increasing by 1 at each step  (error otherwise). Time must not decrease as transactions IDs increase.
     * Time Created: a long integer representing the number of milliseconds (ms) from a fixed time 0.
     * Total Value: in user defined tokens depending on network.
     * TODO: Is this bytes?
     * Total Size: in bytes
     * First Arrival NodeID: the node at which the transaction first arrives
     *
     * @param fileName  A name to the text file containing the transactions.
     * @param hasHeader Whether the file has a header.
     * @throws IOException Error finding or reading the file.
     */
    public TransactionGroup(String fileName, boolean hasHeader) throws IOException {
        this();

        String l;
        String delimiter = ",";

        int tCount = 1;
        long lastTime = 0;
        int id;
        long time;
        float value;
        float size;
        int nodeID;

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((l = br.readLine()) != null) {
            if (hasHeader) {
                hasHeader = false;
            } else {
                String[] t = l.split(delimiter);
                id = Integer.parseInt(t[0]);
                if (id != tCount)
                    throw new IllegalArgumentException("Error in workload file: transaction IDs must start from 1 and strictly increase by 1.");
                tCount++;
                time = Long.parseLong(t[1]);
                if (time < lastTime)
                    throw new IllegalArgumentException("Error in workload file: time must not decrease as transaction IDs increase.");
                lastTime = time;
                value = Float.parseFloat(t[2]);
                size = Float.parseFloat(t[3]);
                nodeID = Integer.parseInt(t[4]);

                this.addTransaction(new Transaction(id, time, value, size, nodeID));
            }
        }
        br.close();
    }

    ////////// Modifiers //////////

    /**
     * Replace transaction group with a new one.
     *
     * @param initial An array list of <tt>Transaction</tt> objects, to replace the existing one.
     */
    public void updateTransactionGroup(List<Transaction> initial) {
        totalValue = 0;
        totalSize = 0;
        group = initial;
        for (Transaction t : initial) {
            totalValue += t.getValue();
            totalSize += t.getSize();
        }
    }

    /**
     * See {@linkplain ITxContainer#addTransaction(Transaction)}.
     */
    @Override
    public void addTransaction(Transaction t) {
        group.add(t);
        totalSize += t.getSize();
        totalValue += t.getValue();
    }

    /**
     * See {@linkplain ITxContainer#removeTransaction(Transaction)}.
     */
    @Override
    public void removeTransaction(Transaction t) {
        if (!group.contains(t)) return;
        group.remove(t);
        totalSize -= t.getSize();
        totalValue -= t.getValue();
    }


    /**
     * Like removeTransaction(Transaction) but with ID as an argument. 
     * See {@linkplain ITxContainer#removeTransaction(Transaction)}.
     * @param txID
     */
    public void removeTransaction(int txID) {
        Transaction t = getTransactionById((int) txID);
        if (!group.contains(t)) return;
        group.remove(t);
        totalSize -= t.getSize();
        totalValue -= t.getValue();
    }
    
    
    
    /**
     * See {@linkplain ITxContainer#removeNextTx()}.
     */
    @Override
    public Transaction removeNextTx() {
        Transaction t = group.removeFirst();
        totalSize -= t.getSize();
        totalValue -= t.getValue();
        return t;
    }

    /**
     * See {@linkplain ITxContainer#extractGroup(TransactionGroup)}.
     */
    @Override
    public void extractGroup(TransactionGroup g) {
        for (Transaction t : g.getTransactions()) {
            this.removeTransaction(t);
        }
    }

    ////////// Examine Content //////////

    /**
     * See {@linkplain ITxContainer#contains(Transaction)}.
     */
    @Override
    public boolean contains(Transaction t) {
        for (Transaction r : group) {
            if (r.getID() == t.getID()) {
                return true;
            }
        }
        return false;
    }

    /**
     * See {@linkplain ITxContainer#contains(long)}.
     */
    @Override
    public boolean contains(long txID) {
        for (Transaction r : group) {
            if (r.getID() == txID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the group overlaps with another transaction group, i.e.,
     * there is a transaction in {@code p} that also exists in the current group.
     *
     * @param p The <tt>TransactionGroup</tt> in question.
     * @return <tt>true</tt> of there is at least one transaction in <tt>p</tt> that is contained in the group, <tt>false</tt>, otherwise.
     */
    public boolean overlapsWithByObj(TransactionGroup p) {
        boolean result = false;
        for (Transaction t : p.getTransactions()) {
            if (group.contains(t)) {
                result = true;
                break;
            }
        }
        return (result);
    }

    /**
     * As {@link TransactionGroup#overlapsWithByObj(TransactionGroup)} but criterion that is used is
     * transaction ID.
     *
     * @param g The {@link TransactionGroup} object in question.
     * @return <tt>true</tt> of there is at least one transaction in <tt>g</tt> that is contained in the group, <tt>false</tt>, otherwise.
     */
    public boolean overlapsWith(TransactionGroup g) {
        for (Transaction r : group) {
            for (Transaction t : g.getTransactions()) {
                if (t.getID() == r.getID()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves a TransactionGroup containing the top N transactions based on
     * a given size limit and comparator.
     *
     * @param sizeLimit The maximum cumulative size (in bytes) of transactions allowed in the result.
     * @param comp      The comparator used to sort the transactions.
     * @return A {@link TransactionGroup} object containing the top N transactions that do not
     * exceed sizeLimit based on given comparator.
     */
    public TransactionGroup getTopN(float sizeLimit, Comparator<Transaction> comp) {
        if (sizeLimit < 0) {
            throw new IllegalArgumentException(String.format("Size limit (%f) must be a positive integer", sizeLimit));
        }

        ArrayList<Transaction> result = new ArrayList<>();
        List<Transaction> sortedGroup = group.stream().sorted(comp).toList();

        int i = 0;
        float sum = 0;
        while ((sum <= sizeLimit) && (i < sortedGroup.size())) {
            sum += sortedGroup.get(i).getSize();
            result.add(sortedGroup.get(i));
            i++;
        }
        if (sum > sizeLimit) { //Last one was exceeding the limit.
            result.remove(i - 1);
        }
        return (new TransactionGroup(result));
    }

    ////////// Accessors //////////

    /**
     * See {@linkplain ITxContainer#getID()}.
     */
    @Override
    public int getID() {
        return groupID;
    }

    /**
     * See {@linkplain ITxContainer#getCount()}.
     */
    @Override
    public int getCount() {
        return (group.size());
    }

    /**
     * See {@linkplain ITxContainer#getSize()}.
     */
    @Override
    public float getSize() {
        return totalSize;
    }

    /**
     * See {@linkplain ITxContainer#getValue()}.
     */
    @Override
    public float getValue() {
        return totalValue;
    }

    /**
     * Return the ArrayList of transactions in the group
     *
     * @return An <tt>ArrayList</tt> of <tt>Transaction</tt> objects representing the transactions in the group.
     */
    @Override
    public List<Transaction> getTransactions() {
        return group;
    }

    /**
     * Get the transaction of the group at index <tt>index</tt>. Does not check if index exists.
     *
     * @param index The index from <tt>0</tt> to <tt>n-1</tt>
     * @return A reference to the <tt>Transaction</tt> object.
     */
    public Transaction getTransaction(int index) {
        return group.get(index);
    }

    public Transaction getTransactionById(int txID) {
        for (Transaction r : group) {
            if (r.getID() == txID) {
            	return (r);
            }
        }
    	return null;
    }
    
    
    ////////// Print Group //////////

    /**
     * See {@linkplain ITxContainer#printIDs(String)}.
     */
    @Override
    public String printIDs(String sep) {
        StringBuilder s = new StringBuilder("{");
        for (Transaction t : group) {
            s.append(t.getID()).append(sep);
        }
        if (s.length() > 1)
            s = new StringBuilder(s.substring(0, s.length() - 1) + "}");
        else
            s.append("}");
        return (s.toString());
    }

    /**
     * Generates a debug printout of the Transaction IDs in the pool.
     *
     * @return A string containing the IDs of the Transactions in the pool, separated by commas.
     */
    @SuppressWarnings("unused")
    public String debugPrintPoolTx() {
        StringBuilder s = new StringBuilder();
        for (Transaction t : group) {
            s.append(t.getID()).append(", ");
        }
        return (s.toString());
    }

}
