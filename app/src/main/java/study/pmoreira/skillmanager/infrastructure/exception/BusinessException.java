package study.pmoreira.skillmanager.infrastructure.exception;

public class BusinessException extends Exception {

    private static final int INVALID_CODE = -1;

    private final int code;

    BusinessException(int code) {
        this.code = code;
    }

    public BusinessException(String message, Exception e) {
        super(message, e);
        code = INVALID_CODE;
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
