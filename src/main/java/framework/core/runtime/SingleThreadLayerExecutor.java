package framework.core.runtime;

import framework.core.data.Chunk;
import framework.core.exception.ExceptionAction;
import framework.core.exception.LayerException;
import framework.core.exception.LogDomain;
import framework.core.layer.Layer;
import framework.core.logging.LayerExceptionLogger;
import framework.core.logging.LogTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LayerExecutor} 계약을 구현한 단일 스레드 기반 실행자
 *
 * {@link Layer} 하나에 대해 inbound / outbound 처리를 각각
 * 전용 {@link Thread} 하나로 수행하며,
 * {@code BlockingQueue} 기반의 블로킹 처리 모델을 전제로 한다.
 */
public class SingleThreadLayerExecutor implements LayerExecutor {

    private static final Logger log = LoggerFactory.getLogger(SingleThreadLayerExecutor.class);

    /**
     * Layer 실행 상태를 제어하는 플래그
     *
     * - start/stop 호출 간의 가시성을 보장하기 위해 volatile로 선언된다
     */
    private volatile boolean running = false;

    /**
     * Layer inbound 처리를 담당하는 실행 스레드
     */
    private Thread inboundThread;

    /**
     * Layer outbound 처리를 담당하는 실행 스레드
     */
    private Thread outboundThread;

    /**
     * 주어진 Layer에 대한 실행을 시작한다.
     *
     * - inbound / outbound 처리용 스레드를 각각 생성하여 실행한다
     * - 이미 실행 중인 경우 중복 시작하지 않는다
     */
    @Override
    public synchronized void start(Layer layer) {
        if (running) return;
        running = true;

        inboundThread =
                new Thread(() -> consumeInbound(layer), layer.getType() + "-inbound");
        outboundThread =
                new Thread(() -> consumeOutbound(layer), layer.getType() + "-outbound");

        inboundThread.start();
        outboundThread.start();
    }

    /**
     * 현재 실행 중인 Layer 처리를 중단한다.
     *
     * - 실행 플래그를 해제하고 관련 스레드에 인터럽트를 전달한다
     */
    @Override
    public void stop() {
        running = false;
        if (inboundThread != null) inboundThread.interrupt();
        if (outboundThread != null) outboundThread.interrupt();
    }

    /**
     * Layer의 inbound 큐를 소비하며 처리한다.
     *
     * - 큐에서 Chunk를 순차적으로 가져와 Layer에 위임한다
     * - LayerException 발생 시 정의된 ExceptionAction에 따라 흐름을 제어한다
     */
    private void consumeInbound(Layer layer) {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Chunk chunk = layer.getInboundQueue().take();
                layer.executeInbound(chunk);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (LayerException e) {
                LayerExceptionLogger.log(e, layer, log);
                if (e.action() == ExceptionAction.STOP) stop();
            } catch (Exception e) {
                log.error(
                        LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                        LogDomain.RUNTIME,
                        layer.getType(),
                        e.getMessage()
                );
                stop();
            }
        }
    }

    /**
     * Layer의 outbound 큐를 소비하며 처리한다.
     *
     * - outbound 흐름은 inbound와 동일한 실행 모델을 따른다
     */
    private void consumeOutbound(Layer layer) {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Chunk chunk = layer.getOutboundQueue().take();
                layer.executeOutbound(chunk);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (LayerException e) {
                LayerExceptionLogger.log(e, layer, log);
                if (e.action() == ExceptionAction.STOP) stop();
            } catch (Exception e) {
                log.error(
                        LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                        LogDomain.RUNTIME,
                        layer.getType(),
                        e.getMessage()
                );
                stop();
            }
        }
    }

    /**
     * 현재 Executor가 실행 중인지 여부를 반환한다.
     */
    public boolean isRunning() {
        return running;
    }
}
