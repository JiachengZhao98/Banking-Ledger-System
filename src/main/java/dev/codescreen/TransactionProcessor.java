package dev.codescreen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Process transactions, maintaining a balance record and an event log
 * Use BigDecimal to precisely perform calculations.
 * Implement read-write locking to safely handle concurrent read and write operations
 */
public class TransactionProcessor {
    final List<TransactionEvent> eventLog = new ArrayList<>();
    private final Map<String, BigDecimal> userBalances = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Check user's bank account balance if exists.
     * Allow multiple threads to read a resource as long as no threads are writing to it.
     *
     * @param userId The user's ID. The user's bank account must exist if he wants to check balances.
     */

    public BigDecimal checkBalance(String messageId, String userId) {
        lock.readLock().lock();
        try {
            if (!userBalances.containsKey(userId)) {
                eventLog.add(new TransactionEvent(messageId, userId, "NULL", "CHECKING BALANCE", "USER NOT FOUND",
                        "0.00", "0.00"));
                return BigDecimal.valueOf(0.00);
            }
            BigDecimal balance = userBalances.get(userId);
            eventLog.add(new TransactionEvent(messageId, userId, "NULL", "CHECKING BALANCE", "APPROVED",
                    balance.toString(), balance.toString()));
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Loads funds into a user's account.
     * Only one thread can write to the resource while no threads are reading it.
     *
     * @param userId The user's ID. Allow user to deposit money if he does not have an account.
     * @param amount The amount to deposit. Amount must be greater than 0.
     * @return The new transaction response after the transaction.
     */

    public BigDecimal load(String messageId, String userId, BigDecimal amount) {
        lock.writeLock().lock();
        try {
            BigDecimal currentBalance = userBalances.getOrDefault(userId, BigDecimal.valueOf(0.00));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "LOADS", "DENIED",
                        currentBalance.toString(), currentBalance.toString()));
                throw new IllegalArgumentException("Amount must be greater than 0");
            }
            BigDecimal newBalance = currentBalance.add(amount);
            userBalances.put(userId, newBalance);
            eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "LOADS", "APPROVED",
                    currentBalance.toString(), newBalance.toString()));
            return newBalance;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Authorizes a withdrawal if sufficient funds are available.
     * Only one thread can write to the resource while no threads are reading it.
     *
     * @param userId The user's ID. The user must deposit money first if he wants to withdraw money from his account.
     * @param amount The amount to withdraw. Amount must be greater than 0.
     * @return The new transaction response if authorized, or throw exception if insufficient funds or no account found.
     */

    public BigDecimal authorization(String messageId, String userId, BigDecimal amount) {
        lock.writeLock().lock();
        try {
            if (!userBalances.containsKey(userId)) {
                eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "AUTHORIZATIONS", "DENIED",
                        "0.00", "0.00"));
                return BigDecimal.valueOf(0.00);
            }
            BigDecimal currentBalance = userBalances.get(userId);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "AUTHORIZATIONS", "DENIED",
                        currentBalance.toString(), currentBalance.toString()));
                throw new IllegalArgumentException("Amount must be greater than 0");
            }
            if (currentBalance.compareTo(amount) >= 0) {
                BigDecimal newBalance = currentBalance.subtract(amount);
                userBalances.put(userId, newBalance);
                eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "AUTHORIZATIONS", "APPROVED",
                        currentBalance.toString(), newBalance.toString()));
                return newBalance;
            } else {
                eventLog.add(new TransactionEvent(messageId, userId, amount.toString(), "AUTHORIZATIONS", "DENIED",
                        currentBalance.toString(), currentBalance.toString()));
                return currentBalance;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
