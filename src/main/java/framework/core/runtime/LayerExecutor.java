package framework.core.runtime;

import framework.core.layer.Layer;

/**
 * Layer 실행을 제어하는 실행자(Executor) 계약
 *
 * - Layer의 처리 시작과 중단 책임을 정의한다
 * - 실행 모델(스레드, 큐, 동시성)은 구현체에 위임한다
 */
public interface LayerExecutor {

    /**
     * 주어진 Layer의 실행을 시작한다.
     *
     * - Layer의 실제 처리 방식은 구현체가 결정한다
     */
    void start(Layer layer);

    /**
     * 현재 실행 중인 Layer 처리를 중단한다.
     *
     * - 중단 시점과 방식은 구현체의 정책을 따른다
     */
    void stop();
}
