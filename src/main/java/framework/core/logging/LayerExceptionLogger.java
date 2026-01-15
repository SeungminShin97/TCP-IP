package framework.core.logging;

import framework.core.exception.LayerException;
import framework.core.exception.LogDomain;
import framework.core.layer.Layer;
import org.slf4j.Logger;

/**
 * {@link LayerException}에 대한 로그 출력을 담당하는 전용 로거.
 *
 * <p>
 * 예외가 제공하는 {@link framework.core.exception.LogLevel} 메타 정보를 기반으로
 * 실제 로그 레벨과 출력 형식을 결정한다.
 *
 * <p>
 * 이 클래스는 로그 정책과 포맷을 중앙에서 통제하기 위한 목적을 가지며,
 * 실행 제어나 예외 처리 흐름에는 관여하지 않는다.
 */
public final class LayerExceptionLogger {

    private LayerExceptionLogger() {}

    /**
     * 주어진 {@link LayerException}을 로그 정책에 따라 기록한다.
     *
     * <p>
     * 로그 레벨과 출력 형식은 예외가 제공하는 메타 정보에 의해 결정되며,
     * 실제 처리 흐름(DROP, STOP 등)은 이 메서드의 호출자에 의해 제어된다.
     *
     * @param e     발생한 계층 예외
     * @param layer 예외가 발생한 계층
     * @param log   로그 출력을 담당하는 SLF4J {@link Logger}
     */
    public static void log(LayerException e, Layer layer, Logger log) {

        switch (e.logLevel()) {

            /**
             * 디버깅 또는 개발 단계에서만 의미가 있는 예외 상황.
             * <p>
             * 상세한 내부 상태 추적을 위한 용도로 사용된다.
             */
            case DEBUG -> log.debug(
                    LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                    LogDomain.LAYER,
                    layer.getType(),
                    e.getMessage()
            );

            /**
             * 시스템의 정상적인 흐름 내에서 발생한 정보성 예외 상황.
             * <p>
             * 상태 변화나 실행 맥락을 기록하기 위한 용도로 사용된다.
             */
            case INFO -> log.info(
                    LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                    LogDomain.LAYER,
                    layer.getType(),
                    e.getMessage()
            );

            /**
             * 관찰 또는 추적이 필요한 비치명적 예외 상황.
             */
            case WARN -> log.warn(
                    LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                    LogDomain.LAYER,
                    layer.getType(),
                    e.getMessage()
            );

            /**
             * 계층 실행의 안정성에 영향을 줄 수 있는 치명적 예외 상황.
             */
            case ERROR -> log.error(
                    LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                    LogDomain.LAYER,
                    layer.getType(),
                    e.getMessage(),
                    e
            );

            /**
             * 정의되지 않았거나 예상하지 못한 로그 수준의 예외.
             * <p>
             * 프레임워크 내부 오류로 간주하고 ERROR 레벨로 기록한다.
             */
            default -> log.error(
                    LogTemplate.DOMAIN_TYPE_MESSAGE.pattern(),
                    LogDomain.UNKNOWN,
                    e.getMessage(),
                    e
            );
        }
    }
}
