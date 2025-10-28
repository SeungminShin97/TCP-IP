package framework.core.data.payload;

/**
 * TCP/IP 에서 사용되는 페이로드를 나타내는 클래스입니다.
 * <p>
 * 이 클래스는 페이로드의 바이트 배열을 저장하고 반환하는 기능을 제공합니다.
 */
public final class Payload {
    private byte[] bytes;

    public Payload(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }
}
