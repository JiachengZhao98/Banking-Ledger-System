package dev.codescreen;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class TransactionProcessorTest {

    /**
     * Test all four functions in TransactionProcessor, including the concurrent access and transaction
     */

    //@Disabled
    @Test
    void testCheckBalance() {
        TransactionProcessor processor = new TransactionProcessor();
        int messageId = 0;
        processor.load(Integer.toString(++messageId), "user1", BigDecimal.valueOf(100.00));
        assertEquals(BigDecimal.valueOf(100.00), processor.checkBalance(Integer.toString(++messageId), "user1"));
        assertEquals(BigDecimal.valueOf(0.00), processor.checkBalance(Integer.toString(++messageId), "user2")); // no load beforehand

    }

    //@Disabled
    @Test
    void testLoad() {
        TransactionProcessor processor = new TransactionProcessor();
        int messageId = 0;
        assertEquals(BigDecimal.valueOf(100.00), processor.load(Integer.toString(++messageId), "user1", BigDecimal.valueOf(100.0)));
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                processor.load(Integer.toString(-1), "user2", BigDecimal.valueOf(-100.00)));
        assertEquals("Amount must be greater than 0", exception.getMessage()); // illegal amount
    }

    //@Disabled
    @Test
    void testAuthorization() {
        TransactionProcessor processor = new TransactionProcessor();
        int messageId = 0;
        processor.load(Integer.toString(++messageId), "user1", BigDecimal.valueOf(200.00));
        assertEquals(BigDecimal.valueOf(100.00), processor.authorization(Integer.toString(++messageId), "user1", BigDecimal.valueOf(100.0)));
        assertEquals(BigDecimal.valueOf(100.00), processor.authorization(Integer.toString(++messageId), "user1", BigDecimal.valueOf(101.0))); // Insufficient funds
        Exception exception1 = assertThrows(IllegalArgumentException.class, () ->
                processor.authorization(Integer.toString(-1), "user1", BigDecimal.valueOf(-100.00)));
        assertEquals("Amount must be greater than 0", exception1.getMessage()); // illegal amount
        assertEquals(BigDecimal.valueOf(0.00), processor.authorization(Integer.toString(++messageId), "user2", BigDecimal.valueOf(101.0)));
    }

    //@Disabled
    @Test
    void testConcurrentAccess() throws InterruptedException {
        final TransactionProcessor processor = new TransactionProcessor();
        String userId = "user123";
        String loadMessageId = "loadMsg";
        String authMessageId = "authMsg";
        BigDecimal loadAmount = new BigDecimal("100.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");
        int numberOfThreads = 100; // Number of threads for loading and withdrawing

        // Create an ExecutorService with a fixed number of threads
        ExecutorService service = Executors.newFixedThreadPool(2 * numberOfThreads);

        // Run load operations
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> processor.load(loadMessageId + index, userId, loadAmount));
        }

        // Run authorization (withdrawal) operations
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> processor.authorization(authMessageId + index, userId, withdrawAmount));
        }

        // Shutdown the ExecutorService and wait for all threads to finish
        service.shutdown();
        assertTrue(service.awaitTermination(1, TimeUnit.MINUTES));

        // Check final balance
        BigDecimal expectedBalance = loadAmount.multiply(BigDecimal.valueOf(numberOfThreads))
                .subtract(withdrawAmount.multiply(BigDecimal.valueOf(numberOfThreads)));
        BigDecimal actualBalance = processor.checkBalance("checkMsg", userId);

        assertEquals(expectedBalance, actualBalance, "The expected balance does not match the actual balance.");
    }

}
