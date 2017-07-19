package study.pmoreira.skillmanager.infrastructure;

import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;

public class OperationListener<T> {

    public void onSuccess(T result) {}

    public  void onValidationError(ValidateException e) {}

    public  void onError(BusinessException e) {}
}
