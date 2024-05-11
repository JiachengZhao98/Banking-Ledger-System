package dev.codescreen;

public class SampleTestTransaction {
    String action;
    String msgId;
    String userId;
    String debitOrCredit;
    String amount;
    String responseCode;
    String balance;

    /**
     * Inner class to store the information parsed from sample_tests file
     */

    public SampleTestTransaction(String action, String msgId, String userId, String debitOrCredit,
                                 String amount, String responseCode, String balance) {
        this.action = action;
        this.msgId = msgId;
        this.userId = userId;
        this.debitOrCredit = debitOrCredit;
        this.amount = amount;
        this.responseCode = responseCode;
        this.balance = balance;
    }

    //getters
    public String getAction() {
        return action;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

    public String getAmount() {
        return amount;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getBalance() {
        return balance;
    }
}
