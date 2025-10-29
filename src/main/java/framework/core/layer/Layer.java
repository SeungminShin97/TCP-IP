package framework.core.layer;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import framework.core.data.Chunk;
import framework.core.data.header.EmptyHeader;

/**
 * TCP/IP 모델의 계층을 나타내는 추상 클래스입니다.
 * <br>
 * 각 계층은 상위 계층과 하위 계층에 대한 참조를 가지며,
 * 발신(outbound) 및 수신(inbound) 처리를 위한 스레드 풀{@code ExecutorService}과 큐{@code BlockingQueue<Chunk>}를 포함합니다.
 * <br>
 * 자식 클래스는 {@link #processOutbound(Chunk)}, {@link #processInbound(Chunk)}를 구현하여 처리 로직을 정의해야 합니다.
 */
public abstract class Layer {
    // 계층 유형을 나타내는 열거형
    private final LayerType LAYER_TYPE;

    private final ExecutorService inboundThreadPool;
    private final ExecutorService outboundThreadPool;
    protected final BlockingQueue<Chunk> inboundQueue;
    protected final BlockingQueue<Chunk> outboundQueue;
    protected Layer upperLayer;
    protected Layer lowerLayer;

    public Layer(LayerType layerType) {
        this(layerType, 1);
    }

    public Layer(LayerType layerType, int nThreads) {
        this.inboundThreadPool = Executors.newFixedThreadPool(nThreads);
        this.outboundThreadPool = Executors.newFixedThreadPool(nThreads);
        this.inboundQueue = new LinkedBlockingQueue<Chunk>();
        this.outboundQueue = new LinkedBlockingQueue<Chunk>();
        this.LAYER_TYPE = layerType;
    }

    /**
     * 상위 계층을 설정합니다.
     * @param upperLayer 설정할 상위 계층
     */
    public final void setUpperLayer(Layer upperLayer) { this.upperLayer = upperLayer; }

    /**
     * 하위 계층을 설정합니다.
     * @param lowerLayer 설정할 하위 계층
     */
    public final void setLowerLayer(Layer lowerLayer) { this.lowerLayer = lowerLayer; }

    /**
     * 계층의 발신 및 수신 처리를 시작합니다.
     * <br>
     * 각 큐에서 {@link Chunk}를 가져와 {@link #processInbound(Chunk)} 및 {@link #processOutbound(Chunk)} 메서드를 호출하는 작업을 스레드 풀에서 실행합니다.
     */
    public final void run() {
        inboundThreadPool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Chunk chunk = inboundQueue.take();
                    processInbound(chunk);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        outboundThreadPool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Chunk chunk = outboundQueue.take();
                    processOutbound(chunk);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * 계층의 스레드 풀을 종료합니다.
     */
    public final void close() {
        inboundThreadPool.shutdownNow();
        outboundThreadPool.shutdownNow();
    }

    /** 
     * 하위 계층으로 {@link Chunk}를 전송합니다.
     * @param chunk 전송할 {@link Chunk}
     */
    protected final void sendToLower(Chunk chunk) {
        //TODO: enqueue 실패 시 재시도 처리 로직
        if (lowerLayer == null) 
            throw new IllegalStateException("lower layer is not set");
        resetHeader(chunk);
        lowerLayer.enqueueOutbound(chunk);
    }

    /** 
     * 상위 계층으로 {@link Chunk}를 전송합니다.
     * @param chunk 전송할 {@link Chunk}
     */
    protected final void sendToUpper(Chunk chunk) {
        //TODO: enqueue 실패 시 재시도 처리 로직
        if (upperLayer == null) 
            throw new IllegalStateException("upper layer is not set");
        resetHeader(chunk);
        upperLayer.enqueueInbound(chunk);
    }

    /** 
     * {@link Chunk}를 수신 큐에 추가하는 내부 메서드 입니다.
     * @param chunk 수신할 {@link Chunk}
     */
    private final void enqueueInbound(Chunk chunk) {
        enqueue(inboundQueue, chunk);
    }

    /** 
     * {@link Chunk}를 발신 큐에 추가하는 내부 메서드 입니다.
     * @param chunk 발신할 {@link Chunk}
     */
    private final void enqueueOutbound(Chunk chunk) {
        enqueue(outboundQueue, chunk);
    }

    /** 
     * {@link Chunk}의 헤더를 EmptyHeader로 재설정합니다.
     * @param chunk 재설정할 {@link Chunk}
     */
    private void resetHeader(Chunk chunk) {
        chunk.setHeader(EmptyHeader.INSTANCE);
    }

    /** 
     * 지정된 큐에 {@link Chunk}를 추가합니다.
     * @param queue {@link Chunk}를 추가할 {@code BlockingQueue<Chunk>}
     * @param chunk 추가할 {@link Chunk}
     */
    private void enqueue(BlockingQueue<Chunk> queue, Chunk chunk) {
        Objects.requireNonNull(chunk, "chunk cannot be null");
        validateChunk(chunk);
        boolean offered = queue.offer(chunk);
        if (!offered) {
            throw new RejectedExecutionException(LAYER_TYPE + " queue is full");
        }
    }

    /** 
     * {@link Header}가 EmptyHeader인지 검사합니다.
     * - {@link Header}가 null인 경우 {@link NullPointerException}을 발생시킵니다.
     * - {@link Header}가 EmptyHeader가 아닌 경우 {@link IllegalArgumentException}을 발생시킵니다.
     * @param chunk 검사할 {@link Chunk}
     */
    private void validateChunk(Chunk chunk) {
        Objects.requireNonNull(chunk.getHeader(), "header cannot be null");
        if (!(chunk.getHeader() instanceof EmptyHeader)) 
            throw new IllegalArgumentException("header must be EmptyHeader"); 
    }

    /**
     * 하위 계층에서 수신된 {@link Chunk}를 처리하는 추상 메서드입니다.
     * @param chunk 수신된 {@link Chunk}
     */
    protected abstract void processInbound(Chunk chunk);
    /**
     * 상위 계층에서 발신된 {@link Chunk}를 처리하는 추상 메서드입니다.
     * @param chunk 발신된 {@link Chunk}
     */
    protected abstract void processOutbound(Chunk chunk);
}
