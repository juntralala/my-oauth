package dev.juntralala.oauth.exception;

public class ValidationException extends Exception {

    private int statusCode;

    public ValidationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ValidationException(String message) {
        super(message);
        this.statusCode = 400;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
