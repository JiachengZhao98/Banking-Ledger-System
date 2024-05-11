package dev.codescreen;

public class CheckBalanceResponse {
    private final String messageId;
    private final String userId;
    private final String balance;

    /**
     * Inner class to store the check balance response
     */
    public CheckBalanceResponse(String messageId, String userId, String balance) {
        this.messageId = messageId;
        this.userId = userId;
        this.balance = balance;
    }

    //getters
    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBalance() {
        return balance;
    }
}