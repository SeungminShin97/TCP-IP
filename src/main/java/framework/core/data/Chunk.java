package framework.core.data;

import framework.core.data.header.Header;
import framework.core.data.payload.Payload;

/**
 * TCP/IP 에서 사용되는 청크를 나타내는 클래스입니다.
 * <p>
 * {@code Header} 타입의 헤더와 {@code Payload} 타입의 페이로드를 포함합니다. 
 * {@code Header}는 타입 매개변수 {@code T}로 지정됩니다.
 * 
 * @param <T> 헤더의 타입을 나타내는 제네릭 타입 매개변수입니다. {@code Header} 클래스를 상속받아야 합니다.
 */
public final class Chunk <T extends Header> {
    private T header;
    private Payload payload;

    public void setHeader(T header) {
        this.header = header;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public T getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }
    
}
