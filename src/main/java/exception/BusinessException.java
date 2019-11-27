package exception;


public class BusinessException extends Exception {
    public BusinessException() {

    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
