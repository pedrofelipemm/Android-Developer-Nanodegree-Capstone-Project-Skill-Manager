package study.pmoreira.skillmanager.infrastructure.exception;

public class ValidateException extends BusinessException {

    private final int resId;

    public ValidateException(int resId, int code) {
        super(code);
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
