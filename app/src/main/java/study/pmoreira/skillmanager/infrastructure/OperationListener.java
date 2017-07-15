package study.pmoreira.skillmanager.infrastructure;

public interface OperationListener<T> {

    void onSuccess(T result);

    void onError(BusinessException e);
}
