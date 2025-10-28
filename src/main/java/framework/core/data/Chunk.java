package framework.core.data;

import framework.core.data.header.Header;
import framework.core.data.payload.Payload;

/**
 * TCP/IP 에서 사용되는 청크를 나타내는 클래스입니다.
 * <p>
 * {@code Header} 타입의 헤더와 {@code Payload} 타입의 페이로드를 포함합니다.
 * 
 */
public final class Chunk {
    private Header header;
    private Payload payload;

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }
    
}
