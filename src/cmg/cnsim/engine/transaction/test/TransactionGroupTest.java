package cmg.cnsim.engine.transaction.test;

import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        sortedGroup = pool.getTransactions().stream()
                .sorted(comparator)
                .toList();
    }

    @Test
    public void testTransactionGroupArgConstructor() {
        // Integer array used instead of float for compatibility with streams
        int[] sizes = {2753, 2839, 1291};
        int[] values = {732100, 173901, 238179};
        ArrayList<Transaction> newTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(0, 0, values[0], sizes[0]),
                new Transaction(1, 1, values[1], sizes[1]),
                new Transaction(2, 2, values[2], sizes[2])
        ));
        TransactionGroup newTransactionGroup = new TransactionGroup(newTransactions);

        assertArrayEquals(newTransactions.toArray(), newTransactionGroup.getTransactions().toArray());
        assertEquals(Arrays.stream(sizes).sum(), newTransactionGroup.getSize());
        assertEquals(Arrays.stream(values).sum(), newTransactionGroup.getValue());
    }

    @Test
    public void testTransactionGroupFileConstructor_validInput() throws IOException {
        String pathWithoutHeader = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_validInputWithoutHeader.txt";
        String pathWithHeader = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_validInputWithHeader.txt";

        TransactionGroup newPool1 = new TransactionGroup(pathWithoutHeader, false);
        TransactionGroup newPool2 = new TransactionGroup(pathWithHeader, true);
        TransactionGroup expectedPool = new TransactionGroup(new ArrayList<>(Arrays.asList(
                new Transaction(1, 40, 322, 1892, 51383),
                new Transaction(2, 42, 183, 9374, 66109),
                new Transaction(3, 51, 238, 4192, 43101),
                new Transaction(4, 59, 441, 5233, 21767),
                new Transaction(5, 64, 101, 2209, 77603)
        )));

        assertTrue(expectedPool.getTransactions().size() == newPool1.getTransactions().size() && expectedPool.getTransactions().size() == newPool2.getTransactions().size());
        assertTrue(expectedPool.getSize() == newPool1.getSize() && expectedPool.getSize() == newPool2.getSize());
        assertTrue(expectedPool.getValue() == newPool1.getValue() && expectedPool.getValue() == newPool2.getValue());
        for (int i = 0; i < expectedPool.getTransactions().size(); i++) {
            Transaction transaction0 = expectedPool.getTransactions().get(i);
            Transaction transaction1 = newPool1.getTransactions().get(i);
            Transaction transaction2 = newPool2.getTransactions().get(i);

            assertTrue(transaction0.getID() == transaction1.getID() && transaction0.getID() == transaction2.getID());
            assertTrue(transaction0.getCreationTime() == transaction1.getCreationTime() && transaction0.getCreationTime() == transaction2.getCreationTime());
            assertTrue(transaction0.getValue() == transaction1.getValue() && transaction0.getValue() == transaction2.getValue());
            assertTrue(transaction0.getSize() == transaction1.getSize() && transaction0.getSize() == transaction2.getSize());
            assertTrue(transaction0.getNodeID() == transaction1.getNodeID() && transaction0.getNodeID() == transaction2.getNodeID());
        }
    }

    @Test
    public void testTransactionGroupFileConstructor_checkTxIdStart() {
        String path = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_checkTxIdStart.txt";
        assertThrows(IllegalArgumentException.class, () -> new TransactionGroup(path, false));
    }

    @Test
    public void testTransactionGroupFileConstructor_checkTxIdIncrement() {
        String path = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_checkTxIdIncrement.txt";
        assertThrows(IllegalArgumentException.class, () -> new TransactionGroup(path, false));
    }

    @Test
    public void testTransactionGroupFileConstructor_checkTxIdIncreasing() {
        String path = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_checkTxIdIncreasing.txt";
        assertThrows(IllegalArgumentException.class, () -> new TransactionGroup(path, false));
    }

    @Test
    public void testTransactionGroupFileConstructor_checkTimePositive() {
        String path = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_checkTimePositive.txt";
        assertThrows(IllegalArgumentException.class, () -> new TransactionGroup(path, false));
    }

    @Test
    public void testTransactionGroupFileConstructor_checkTimeIncreasing() {
        String path = "src/cmg/cnsim/engine/transaction/test/text/TransactionGroup_checkTimeIncreasing.txt";
        assertThrows(IllegalArgumentException.class, () -> new TransactionGroup(path, false));
    }

    @Test
    public void testUpdateTransactionGroup_nonEmptyPool() {
        float value1 = 150;
        float value2 = 320;
        float size1 = 2000;
        float size2 = 4100;
        ArrayList<Transaction> newTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(55, 14, value1, size1),
                new Transaction(66, 23, value2, size2)
        ));

        pool.updateTransactionGroup(newTransactions);

        assertArrayEquals(newTransactions.toArray(), pool.getTransactions().toArray());
        assertEquals(size1 + size2, pool.getSize());
        assertEquals(value1 + value2, pool.getValue());
    }

    @Test
    public void testUpdateTransactionGroup_emptyPool() {
        assertEquals(0, emptyPool.getTransactions().size());
        assertEquals(0, emptyPool.getSize());
        assertEquals(0, emptyPool.getValue());
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
    public void testRemoveTransaction_existingTransaction() {
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        Transaction existingTransaction = pool.getTransactions().get(1);

        pool.removeTransaction(pool.getTransactions().get(1));

        assertFalse(pool.getTransactions().contains(existingTransaction));
        assertFalse(pool.contains(existingTransaction));
        assertEquals(sizeBefore - existingTransaction.getSize(), pool.getSize());
        assertEquals(valueBefore - existingTransaction.getValue(), pool.getValue());
    }

    @Test
    public void testRemoveTransaction_nonExistingTransaction() {
        List<Transaction> groupBefore = pool.getTransactions();
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();

        pool.removeTransaction(otherTransaction);

        assertFalse(pool.getTransactions().contains(otherTransaction));
        assertArrayEquals(groupBefore.toArray(), pool.getTransactions().toArray());
        assertEquals(sizeBefore, pool.getSize());
        assertEquals(valueBefore, pool.getValue());
    }

    @Test
    public void testRemoveTransaction_emptyPool() {
        emptyPool.removeTransaction(otherTransaction);

        assertTrue(emptyPool.getTransactions().isEmpty());
        assertEquals(0, emptyPool.getSize());
        assertEquals(0, emptyPool.getValue());
    }

    @Test
    public void testRemoveNextTx_nonEmptyPool() {
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getTransactions().size();
        Transaction nextTransaction = pool.getTransactions().getFirst();

        pool.removeNextTx();

        assertFalse(pool.getTransactions().contains(nextTransaction));
        assertEquals(groupSizeBefore - 1, pool.getTransactions().size());
        assertEquals(sizeBefore - nextTransaction.getSize(), pool.getSize());
        assertEquals(valueBefore - nextTransaction.getValue(), pool.getValue());
    }

    @Test
    public void testRemoveNextTx_emptyPool() {
        assertThrows(NoSuchElementException.class, emptyPool::removeNextTx);
    }

    @Test
    public void testExtractGroup_existingTransaction() {
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Arrays.asList(
                pool.getTransactions().get(0),
                pool.getTransactions().get(1)
        )));
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getTransactions().size();

        pool.extractGroup(targetGroup);

        for (Transaction targetTransaction : targetGroup.getTransactions()) {
            assertFalse(pool.getTransactions().contains(targetTransaction));
        }
        assertEquals(groupSizeBefore - targetGroup.getTransactions().size(), pool.getTransactions().size());
        assertEquals(sizeBefore - targetGroup.getSize(), pool.getSize());
        assertEquals(valueBefore - targetGroup.getValue(), pool.getValue());
    }

    @Test
    public void testExtractGroup_nonExistingTransaction() {
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Collections.singletonList(
                otherTransaction
        )));
        float sizeBefore = pool.getSize();
        float valueBefore = pool.getValue();
        int groupSizeBefore = pool.getTransactions().size();

        pool.extractGroup(targetGroup);

        for (Transaction targetTransaction : targetGroup.getTransactions()) {
            assertFalse(pool.getTransactions().contains(targetTransaction));
        }
        assertEquals(groupSizeBefore, pool.getTransactions().size());
        assertEquals(sizeBefore, pool.getSize());
        assertEquals(valueBefore, pool.getValue());
    }

    @Test
    public void testExtractGroup_emptyPool() {
        TransactionGroup targetGroup = new TransactionGroup(new ArrayList<>(Collections.singletonList(
                otherTransaction
        )));

        emptyPool.extractGroup(targetGroup);

        assertEquals(0, emptyPool.getTransactions().size());
        assertEquals(0, emptyPool.getSize());
        assertEquals(0, emptyPool.getValue());
    }

    @Test
    public void testContains_matchByReference() {
        assertTrue(pool.contains(pool.getTransactions().getFirst()));
        assertFalse(pool.contains(otherTransaction));
    }

    @Test
    public void testContains_matchById() {
        assertTrue(pool.contains(new Transaction(pool.getTransactions().getFirst().getID())));
        assertFalse(pool.contains(otherTransaction.getID()));
    }

    @Test
    public void testOverlapsWithObj_overlappingTransactions() {
        TransactionGroup group = new TransactionGroup(new ArrayList<>(Arrays.asList(
                new Transaction(),
                pool.getTransactions().getFirst()
        )));

        assertTrue(pool.overlapsWithByObj(group));
    }

    @Test
    public void testOverlapsWithObj_nonOverlappingTransactions() {
        otherTransaction.setID(pool.getTransactions().getFirst().getID());
        TransactionGroup group = new TransactionGroup(new ArrayList<>(Arrays.asList(
                otherTransaction,
                new Transaction()
        )));

        assertFalse(pool.overlapsWithByObj(group));
    }

    @Test
    public void testOverlapsWithObj_emptyPool() {
        assertFalse(pool.overlapsWithByObj(emptyPool));
        assertFalse(emptyPool.overlapsWithByObj(pool));
    }

    @Test
    public void testOverlapsWith_overlappingTransactions() {
        otherTransaction.setID(pool.getTransactions().getFirst().getID());
        ArrayList<Transaction> overlappingTransactions = new ArrayList<>(Arrays.asList(
                otherTransaction,
                new Transaction(123)
        ));

        TransactionGroup group = new TransactionGroup(overlappingTransactions);

        assertTrue(pool.overlapsWith(group));
    }

    @Test
    public void testOverlapsWith_nonOverlappingTransactions() {
        ArrayList<Transaction> nonOverlappingTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(9990),
                new Transaction(9991)
        ));

        TransactionGroup group = new TransactionGroup(nonOverlappingTransactions);

        assertFalse(pool.overlapsWith(group));
    }

    @Test
    public void testOverlapsWith_emptyPool() {
        assertFalse(pool.overlapsWith(emptyPool));
        assertFalse(emptyPool.overlapsWith(pool));
    }

    @Test
    public void testGetTopN_sizeLimitLessThanFirstTransaction() {
        float sizeLimit = sortedGroup.getLast().getSize() - 0.1f;

        TransactionGroup group = pool.getTopN(sizeLimit, comparator);

        assertTrue(group.getTransactions().isEmpty());
    }

    @Test
    public void testGetTopN_sizeLimitEqualToArbitraryTransaction() {
        int targetIndex = 2;
        float sizeLimit = sortedGroup.stream()
                .limit(targetIndex)
                .map(Transaction::getSize)
                .reduce(0.0f, Float::sum);

        TransactionGroup group = pool.getTopN(sizeLimit, comparator);

        assertEquals(targetIndex, group.getTransactions().size());
        assertArrayEquals(sortedGroup.stream().limit(targetIndex).toArray(), group.getTransactions().toArray());
    }

    @Test
    public void testGetTopN_invalidSizeLimit() {
        assertThrows(IllegalArgumentException.class, () -> pool.getTopN(-1, comparator));
    }
}
