package dev.codescreen;

public class LoadResponse {
    private final String messageId;
    private final String userId;
    private final Balance balance;

    /**
     * Inner class to store the load response, according to the prescribed schema from service.yml
     */
    public LoadResponse(String messageId, String userId, String amount, String currency, String debitOrCredit) {
        this.messageId = messageId;
        this.userId = userId;
        this.balance = new Balance(amount, currency, debitOrCredit);
    }

    //getters
    public Balance getBalance() {
        return balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }

    private static class Balance {
        private final String amount;
        private final String currency;
        private final String debitOrCredit;

        Balance(String amount, String currency, String debitOrCredit) {
            this.amount = amount;
            this.currency = currency;
            this.debitOrCredit = debitOrCredit;
        }

        public String getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getDebitOrCredit() {
            return debitOrCredit;
        }
    }

}
