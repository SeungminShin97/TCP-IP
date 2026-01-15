package framework.core.logging;

/**
 * 로그 출력 시 사용되는 메시지 템플릿을 정의하는 enum
 *
 * - 로그 포맷을 상수로 고정하여 일관성을 보장한다
 * - 로깅 호출부에서 문자열 조합 로직을 제거하기 위함이다
 */
public enum LogTemplate {

    /**
     * 도메인과 타입, 메시지를 함께 표현하는 기본 로그 형식
     */
    DOMAIN_TYPE_MESSAGE("[{}:{}] {}"),

    /**
     * 도메인과 타입만 표현하는 로그 형식
     */
    DOMAIN_TYPE("[{}:{}]"),

    /**
     * 타입과 메시지를 표현하는 로그 형식
     */
    TYPE_MESSAGE("[{}] {}");

    private final String pattern;

    LogTemplate(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 로그 출력에 사용될 포맷 문자열을 반환한다.
     */
    public String pattern() {
        return pattern;
    }
}
