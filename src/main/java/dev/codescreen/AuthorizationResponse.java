package dev.codescreen;

public class AuthorizationResponse {
    private final String userId;
    private final String messageId;
    private final String responseCode;
    private final Balance balance;

    /**
     * Inner class to store the authorization response, according to the prescribed schema from service.yml
     */
    public AuthorizationResponse(String messageId, String userId, String responseCode, String amount, String currency, String debitOrCredit) {
        this.messageId = messageId;
        this.userId = userId;
        this.responseCode = responseCode;
        this.balance = new Balance(amount, currency, debitOrCredit);
    }

    // getters
    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public Balance getBalance() {
        return balance;
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
