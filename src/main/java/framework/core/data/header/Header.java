package framework.core.data.header;

/**
 * TCP/IP 에서 사용되는 공통 헤더를 나타내는 추상 클래스입니다.
 * <p>
 * 이 클래스는 모든 헤더 클래스의 기본 클래스로 사용되며,
 * 헤더의 바이트 배열을 저장하고 반환하는 기능을 제공합니다.
 */
public abstract class Header {
    protected final byte[] bytes;

    protected Header() {
        this.bytes = new byte[0];
    }

    protected Header(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
