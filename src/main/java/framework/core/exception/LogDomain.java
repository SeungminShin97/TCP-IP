package framework.core.exception;

/**
 * 로그 출력 시 사용되는 도메인 식별자.
 * <p>
 * 로그 분류 및 prefix 표현 용도로만 사용되며,
 * 제어 흐름이나 정책 판단에는 관여하지 않는다.
 */
public enum LogDomain {
    LAYER,
    RUNTIME,
    UNKNOWN
}
