package dev.codescreen;

public class TransactionRequest {
    private final String userId;
    private final String messageId;
    private final TransactionAmount transactionAmount;

    /**
     * Inner class to store transaction requests for load and authorization, according to the prescribed schema from service.yml
     */

    public TransactionRequest(String messageId, String userId, String amount, String currency, String debitOrCredit) {
        this.messageId = messageId;
        this.userId = userId;
        this.transactionAmount = new TransactionAmount(amount, currency, debitOrCredit);

    }

    public TransactionAmount getTransactionAmount() {
        return transactionAmount;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAmount() {
        return transactionAmount.getAmount();
    }

    public String getCurrency() {
        return transactionAmount.getCurrency();
    }

    public String getDebitOrCredit() {
        return transactionAmount.getDebitOrCredit();
    }

    private static class TransactionAmount {
        private final String amount;
        private final String currency;
        private final String debitOrCredit;

        TransactionAmount(String amount, String currency, String debitOrCredit) {
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
