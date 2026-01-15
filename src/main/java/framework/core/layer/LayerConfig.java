package framework.core.layer;

/**
 * {@link framework.core.layer.Layer} 실행에 필요한 큐 관련 설정을 표현하는 설정 객체 <br>
 *
 * Layer 생성 시 불변 설정 값으로 사용된다.
 * <p>기본 설정 값은 다음과 같다.</p>
 *
 * <table border="1">
 *   <tr>
 *     <th>항목</th>
 *     <th>기본값</th>
 *     <th>설명</th>
 *   </tr>
 *   <tr>
 *     <td>{@code inboundQueueCapacity}</td>
 *     <td>{@code 1024}</td>
 *     <td>inbound 처리용 큐의 최대 용량</td>
 *   </tr>
 *   <tr>
 *     <td>{@code outboundQueueCapacity}</td>
 *     <td>{@code 1024}</td>
 *     <td>outbound 처리용 큐의 최대 용량</td>
 *   </tr>
 * </table>
 */
public final class LayerConfig {

    private final int inboundQueueCapacity;
    private final int outboundQueueCapacity;

    private LayerConfig(Builder builder) {
        this.inboundQueueCapacity = builder.inboundQueueCapacity;
        this.outboundQueueCapacity = builder.outboundQueueCapacity;
    }

    public LayerConfig(int inboundQueueCapacity, int outboundQueueCapacity) {
        this.inboundQueueCapacity = inboundQueueCapacity;
        this.outboundQueueCapacity = outboundQueueCapacity;
    }

    public static class Builder {
        private int inboundQueueCapacity = 1024;
        private int outboundQueueCapacity = 1024;

        public Builder inboundQueueCapacity(int inboundQueueCapacity) {
            this.inboundQueueCapacity = inboundQueueCapacity;
            return this;
        }

        public Builder outboundQueueCapacity(int outboundQueueCapacity) {
            this.outboundQueueCapacity = outboundQueueCapacity;
            return this;
        }

        public LayerConfig build() {
            return new LayerConfig(this);
        }

        private void validate() {
            if (inboundQueueCapacity <= 0)
                throw new IllegalArgumentException("inboundQueueCapacity must be positive");
            if (outboundQueueCapacity <= 0)
                throw new IllegalArgumentException("outboundQueueCapacity must be positive");
        }
    }

    public int inboundQueueCapacity() { return inboundQueueCapacity; }
    public int outboundQueueCapacity() { return outboundQueueCapacity; }
}
