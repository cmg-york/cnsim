package cmg.cnsim.engine.transaction.test;

import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionGroupTest {

    private TransactionGroup pool;
    private TransactionGroup emptyPool;
    private Transaction otherTransaction;
    private Comparator<Transaction> comparator;
    private List<Transaction> sortedGroup;

    @BeforeEach
    public void setup() {
        ArrayList<Transaction> transactions = new ArrayList<>(Arrays.asList(
                new Transaction(100, 1000, 322, 1892),
                new Transaction(101, 1001, 183, 9374),
                new Transaction(102, 1002, 238, 4192)
        ));

        pool = new TransactionGroup(new ArrayList<>(transactions));
        emptyPool = new TransactionGroup();
        otherTransaction = new Transaction(4, 13, 982, 1202);

        // Sorts in descending order
        comparator = (Transaction t1, Transaction t2) -> Float.compare(t2.getSize(), t1.getSize());
        sortedGroup = pool.getGroup().stream()
                .sorted(comparator)
                .toList();
    }

    @Test
    public void testTransactionGroupArgConstructor() {
        // Array type is integer instead of float in order for Arrays.stream().sum() to work properly
        int[] sizes = {2753, 2839, 1291};
        int[] values = {732100, 173901, 238179};
        ArrayList<Transaction> newTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(0, 0, values[0], sizes[0]),
                new Transaction(1, 1, values[1], sizes[1]),
                new Transaction(2, 2, values[2], sizes[2])
        ));
        TransactionGroup newTransactionGroup = new TransactionGroup(newTransactions);

        assertArrayEquals(newTransactions.toArray(), newTransactionGroup.getGroup().toArray());
        assertEquals(Arrays.stream(sizes).sum(), newTransactionGroup.getSize());
        assertEquals(Arrays.stream(values).sum(), newTransactionGroup.getValue());
    }

    @Test
    public void testTransactionGroupFileConstructor() {
        // TODO: To be implemented...
    }

    @Test
    public void testUpdateTransactionGroup1() {
        // Non-empty input
        float value1 = 150;
        float value2 = 320;
        float size1 = 2000;
        float size2 = 4100;
        ArrayList<Transaction> newTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(55, 14, value1, size1),
                new Transaction(66, 23, value2, size2)
        ));

        pool.updateTransactionGroup(newTransactions);

        assertArrayEquals(newTransactions.toArray(), pool.getGroup().toArray());
        assertEquals(size1 + size2, pool.getSize());
        assertEquals(value1 + value2, pool.getValue());
    }

    @Test
    public void testUpdateTransactionGroup2() {
        // Empty input
        pool.updateTransactionGroup(new ArrayList<>());

        assertEquals(0, pool.getGroup().size());
        assertEquals(0, pool.getSize());
        assertEquals(0, pool.getValue());
    }

    @Test
    public void testAddTransaction() {
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();

        pool.addTransaction(otherTransaction);

        assertEquals(sizeBefore + otherTransaction.getSize(), pool.getSize());
        assertEquals(valueBefore + otherTransaction.getValue(), pool.getValue());
    }

    @Test
    public void testRemoveTransaction1() {
        // Remove existing transaction
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        Transaction existingTransaction = pool.getGroup().get(1);

        pool.removeTransaction(pool.getGroup().get(1));

        assertFalse(pool.getGroup().contains(existingTransaction));
        assertFalse(pool.contains(existingTransaction));
        assertEquals(sizeBefore - existingTransaction.getSize(), pool.getSize());
        assertEquals(valueBefore - existingTransaction.getValue(), pool.getValue());
    }

    @Test
    public void testRemoveTransaction2() {
        // Remove non-existing transaction from non-empty container
        ArrayList<Transaction> groupBefore = pool.getGroup();
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();

        pool.removeTransaction(otherTransaction);

        assertFalse(pool.getGroup().contains(otherTransaction));
        assertArrayEquals(groupBefore.toArray(), pool.getGroup().toArray());
        assertEquals(sizeBefore, pool.getSize());
        assertEquals(valueBefore, pool.getValue());
    }

    @Test
    public void testRemoveTransaction3() {
        // Remove transaction from empty container
        emptyPool.removeTransaction(otherTransaction);

        assertTrue(emptyPool.getGroup().isEmpty());
        assertEquals(0, emptyPool.getSize());
        assertEquals(0, emptyPool.getValue());
    }

    private float[] calculateSizeAndValue(TransactionGroup pool) {
        float newSize = 0;
        float newValue = 0;

        for (Transaction transaction : pool.getGroup()) {
            newSize += transaction.getSize();
            newValue += transaction.getValue();
        }

        return new float[]{newSize, newValue};
    }

    @Test
    public void testRemoveNextTx1() {
        // Remove next transaction from non-empty container
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getGroup().size();
        Transaction nextTransaction = pool.getGroup().getFirst();

        pool.removeNextTx();

        assertFalse(pool.getGroup().contains(nextTransaction));
        assertEquals(groupSizeBefore - 1, pool.getGroup().size());
        assertEquals(sizeBefore - nextTransaction.getSize(), pool.getSize());
        assertEquals(valueBefore - nextTransaction.getValue(), pool.getValue());
    }

    @Test
    public void testRemoveNextTx2() {
        // Remove next transaction from empty container
        assertThrows(NoSuchElementException.class, emptyPool::removeNextTx);
    }

    @Test
    public void testExtractGroup1() {
        // Extract existing transaction(s) from non-empty container
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Arrays.asList(
                pool.getGroup().get(0),
                pool.getGroup().get(1)
        )));
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getGroup().size();

        pool.extractGroup(targetGroup);

        for (Transaction targetTransaction : targetGroup.getGroup()) {
            assertFalse(pool.getGroup().contains(targetTransaction));
        }
        assertEquals(groupSizeBefore - targetGroup.getGroup().size(), pool.getGroup().size());
        assertEquals(sizeBefore - targetGroup.getSize(), pool.getSize());
        assertEquals(valueBefore - targetGroup.getValue(), pool.getValue());
    }

    @Test
    public void testExtractGroup2() {
        // Extract non-existing transactions from non-empty container
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Collections.singletonList(
                otherTransaction
        )));
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getGroup().size();

        pool.extractGroup(targetGroup);

        for (Transaction targetTransaction : targetGroup.getGroup()) {
            assertFalse(pool.getGroup().contains(targetTransaction));
        }
        assertEquals(groupSizeBefore, pool.getGroup().size());
        assertEquals(sizeBefore, pool.getSize());
        assertEquals(valueBefore, pool.getValue());
    }

    @Test
    public void testExtractGroup3() {
        // Extract transactions from empty container
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Collections.singletonList(
                otherTransaction
        )));

        emptyPool.extractGroup(targetGroup);

        assertEquals(0, emptyPool.getGroup().size());
        assertEquals(0, emptyPool.getSize());
        assertEquals(0, emptyPool.getValue());
    }

    @Test
    public void testContainsObj() {
        assertTrue(pool.contains(pool.getGroup().getFirst()));
        assertFalse(pool.contains(otherTransaction));
    }

    @Test
    public void testContainsID() {
        assertTrue(pool.contains(new Transaction(pool.getGroup().getFirst().getID())));
        assertFalse(pool.contains(otherTransaction.getID()));
    }

    @Test
    public void testOverlapsWithObj1() {
        // Non-empty container with overlapping transactions
        TransactionGroup group = new TransactionGroup(new ArrayList<>(Arrays.asList(
                new Transaction(),
                pool.getGroup().getFirst()
        )));

        assertTrue(pool.overlapsWithbyObj(group));
    }

    @Test
    public void testOverlapsWithObj2() {
        // Non-empty container without overlapping transactions
        otherTransaction.setID(pool.getGroup().getFirst().getID());
        TransactionGroup group = new TransactionGroup(new ArrayList<>(Arrays.asList(
                otherTransaction,
                new Transaction()
        )));

        assertFalse(pool.overlapsWithbyObj(group));
    }

    @Test
    public void testOverlapsWithObj3() {
        // Empty container
        assertFalse(pool.overlapsWithbyObj(emptyPool));
        assertFalse(emptyPool.overlapsWithbyObj(pool));
    }

    @Test
    public void testOverlapsWith1() {
        // Non-empty container with overlapping transactions
        otherTransaction.setID(pool.getGroup().getFirst().getID());
        ArrayList<Transaction> overlappingTransactions = new ArrayList<>(Arrays.asList(
                otherTransaction,
                new Transaction(123)
        ));

        TransactionGroup group = new TransactionGroup(overlappingTransactions);

        assertTrue(pool.overlapsWith(group));
    }

    @Test
    public void testOverlapsWith2() {
        // Non-empty container without overlapping transactions
        ArrayList<Transaction> nonOverlappingTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(9990),
                new Transaction(9991)
        ));

        TransactionGroup group = new TransactionGroup(nonOverlappingTransactions);

        assertFalse(pool.overlapsWith(group));
    }

    @Test
    public void testOverlapsWith3() {
        assertFalse(pool.overlapsWith(emptyPool));
        assertFalse(emptyPool.overlapsWith(pool));
    }

    @Test
    public void testGetTopN1() {
        // Size limit < the smallest transaction by size
        float sizeLimit = sortedGroup.getLast().getSize() - 0.1f;

        TransactionGroup group = pool.getTopN(sizeLimit, comparator);

        assertTrue(group.getGroup().isEmpty());
    }

    @Test
    public void testGetTopN2() {
        // Size limit = arbitrary transaction
        int targetIndex = 2;
        float sizeLimit = sortedGroup.stream()
                .limit(targetIndex)
                .map(Transaction::getSize)
                .reduce(0.0f, Float::sum);

        TransactionGroup group = pool.getTopN(sizeLimit, comparator);

        assertEquals(targetIndex, group.getGroup().size());
        assertArrayEquals(sortedGroup.stream().limit(targetIndex).toArray(), group.getGroup().toArray());
    }

    @Test
    public void testGetTopN3() {
        // Invalid size limit
        assertThrows(IllegalArgumentException.class, () -> pool.getTopN(-1, comparator));
    }
}
