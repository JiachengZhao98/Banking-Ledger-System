package dev.codescreen;

public class ErrorResponse {
    private String message;
    private String code;

    /**
     * Inner class to store the error response, according to the prescribed schema from service.yml
     */
    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

    //getters
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
