package dev.codescreen;

import java.time.ZonedDateTime;
import java.io.Serializable;


public class TransactionEvent implements Serializable {
    private final String messageId;
    private final String userId;
    private final ZonedDateTime timestamp;
    private final String amount;
    private final String type;
    private final String responseCode;
    private final String balanceBefore;
    private final String balanceAfter;

    /**
     * Inner class to store transaction events
     *
     * @param type         loads or authorizations
     * @param responseCode if this transaction is approved, denied, or otherwise
     */

    TransactionEvent(String messageId, String userId, String amount, String type, String responseCode, String balanceBefore, String balanceAfter) {
        this.messageId = messageId;
        this.userId = userId;
        this.timestamp = ZonedDateTime.now();
        this.amount = amount;
        this.type = type;
        this.responseCode = responseCode;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getUserId() {
        return userId;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getBalanceBefore() {
        return balanceBefore;
    }

    public String getBalanceAfter() {
        return balanceAfter;
    }

    public String printEvent() {
        // Method to return a string representation of the event, potentially JSON
        return String.format("TransactionEvent{messageId='%s', userId='%s', timestamp='%s', amount='%s', type='%s', responseCode='%s', balanceBefore='%s', balanceAfter='%s'}",
                messageId, userId, timestamp, amount, type, responseCode, balanceBefore, balanceAfter);
    }
}