package cmg.cnsim.bitcoin.test;

import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link Block}.
 */
public class BlockTest {

    private ArrayList<Transaction> transactions;
    private Block block;

    @BeforeEach
    public void setup() {
        transactions = new ArrayList<>(Arrays.asList(
                new Transaction(100, 1000, 322, 1892),
                new Transaction(101, 1001, 183, 9374),
                new Transaction(102, 1002, 238, 4192)
        ));
        block = new Block(transactions);
    }

    /**
     * Tests {@link Block#Block()}.
     */
    @Test
    public void testBlockNoArgConstructor() {
        Block block1 = new Block();
        Block block2 = new Block();
        Block block3 = new Block();

        assertEquals(block1.getID() + 1, block2.getID());
        assertEquals(block2.getID() + 1, block3.getID());
    }

    /**
     * Tests {@link Block#Block(ArrayList)}.
     */
    @Test
    public void testBlockArgConstructor_id() {
        Block block1 = new Block(new ArrayList<>());
        Block block2 = new Block(new ArrayList<>(List.of(new Transaction())));
        Block block3 = new Block(new ArrayList<>(Arrays.asList(new Transaction(), new Transaction())));

        assertEquals(block1.getID() + 1, block2.getID());
        assertEquals(block2.getID() + 1, block3.getID());
    }

    /**
     * Tests {@link Block#Block(ArrayList)}.
     */
    @Test
    public void testBlockArgConstructor_transactionList() {
        assertEquals(transactions, block.getGroup());
    }

    /**
     * Tests {@link Block#validateBlock}.
     */
    @Test
    public void testValidateBlock() {
        ArrayList<Transaction> newTransactions = new ArrayList<>(Arrays.asList(
                new Transaction(1, 40, 322, 1892, 51383),
                new Transaction(2, 42, 183, 9374, 66109)
        ));
        long simTime = 12;
        long sysTime = 24;
        int nodeID = 31;
        String eventType = "Test";
        double difficulty = 3.6;
        double cycles = 4.7;

        Block.Context previousContext = block.getContext();
        block.validateBlock(new TransactionGroup(newTransactions), simTime, sysTime, nodeID, eventType, difficulty, cycles);
        Block.Context newContext = block.getContext();

        assertEquals(newTransactions, block.getGroup());
        assertEquals(simTime, block.getSimTime_validation());
        assertEquals(sysTime, block.getSysTime_validation());
        assertEquals(nodeID, block.getCurrentNodeID());
        assertEquals(difficulty, block.getValidationDifficulty());
        assertEquals(cycles, block.getValidationCycles());

        assertNotEquals(previousContext, newContext);
        assertEquals(simTime, newContext.simTime);
        assertEquals(sysTime, newContext.sysTime);
        assertEquals(nodeID, newContext.nodeID);
        assertEquals(eventType, newContext.blockEvt);
        assertEquals(difficulty, newContext.difficulty);
        assertEquals(cycles, newContext.cycles);
    }

    /**
     * Tests {@code Block#clone}
     */
    @Test
    public void testClone_emptyTransactions() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Block newBlock = new Block();

        Method cloneMethod = Block.class.getDeclaredMethod("clone");
        cloneMethod.setAccessible(true);
        Block clonedBlock = (Block) cloneMethod.invoke(newBlock);

        assertNotSame(newBlock, clonedBlock);
        assertEquals(newBlock, clonedBlock);
    }

    /**
     * Tests {@code Block#clone}
     */
    @Test
    public void testClone_nonEmptyTransactions() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method cloneMethod = Block.class.getDeclaredMethod("clone");
        cloneMethod.setAccessible(true);
        Block clonedBlock = (Block) cloneMethod.invoke(block);

        assertNotSame(block, clonedBlock);
        assertEquals(block, clonedBlock);
    }

}
