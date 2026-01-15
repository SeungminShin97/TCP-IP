package framework.core.exception;

/**
 * 프레임워크 계층(Layer) 내부에서 발생하는 예외의 공통 추상 타입
 *
 * - 예외 자체와 함께 로그 정책과 이후 처리 방향을 계약으로 제공한다
 * - 계층 제어 로직이 예외를 일관되게 해석할 수 있도록 한다
 */
public abstract class LayerException extends RuntimeException {

    protected LayerException(String message) {
        super(message);
    }

    protected LayerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 해당 예외가 기록되어야 할 로그 수준을 반환한다.
     */
    public abstract LogLevel logLevel();

    /**
     * 예외 발생 이후 프레임워크가 취해야 할 처리 방향을 반환한다.
     */
    public abstract ExceptionAction action();
}
