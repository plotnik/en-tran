package io.plotnik.entran;

public class TranException extends RuntimeException {

    public String reason;

    public TranException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
