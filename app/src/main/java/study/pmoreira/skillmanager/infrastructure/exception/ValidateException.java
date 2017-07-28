package study.pmoreira.skillmanager.infrastructure.exception;

public class ValidateException extends BusinessException {

    //TODO

//    private final int resId;
//
//    public ValidateException(int resId, int code) {
//        super(code);
//        this.resId = resId;
//    }
//
//    public int getResId() {
//        return resId;
//    }

    public ValidateException(int code) {
        super(code);
    }
}
