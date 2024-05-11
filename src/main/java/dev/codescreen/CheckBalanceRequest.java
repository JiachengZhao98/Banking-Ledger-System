package dev.codescreen;

public class CheckBalanceRequest {
    private final String messageId;
    private final String userId;

    /**
     * Inner class to store the check balance request
     */
    public CheckBalanceRequest(String messageId, String userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    // getters

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }
}