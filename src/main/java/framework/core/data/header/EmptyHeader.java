package framework.core.data.header;

/**
 * 빈 헤더를 나타내는 클래스입니다.
 * {@code null}을 사용하지 않고 빈 헤더를 표현하기 위해 사용됩니다.
 */
public final class EmptyHeader extends Header {
    public static final EmptyHeader INSTANCE = new EmptyHeader();
    private EmptyHeader() {}
}
