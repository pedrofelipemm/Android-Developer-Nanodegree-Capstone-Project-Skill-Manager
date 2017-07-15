package study.pmoreira.skillmanager.infrastructure;

public class BusinessException extends Exception {

    private static final int INVALOD_CODE = -1;

    private final int code;

    public BusinessException(String message) {
        super(message);
        code = INVALOD_CODE;
    }

    public BusinessException(String message, Exception e) {
        super(message, e);
        code = INVALOD_CODE;
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
