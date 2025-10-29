package framework.util;

import java.util.List;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

/**
 * 네트워크 인터페이스 카드(NIC) 관련 유틸리티 클래스입니다.
 * <br>
 * NIC 검색 및 {@link PcapHandle} 생성 기능을 제공합니다.
 */
public final class NICUtils {
    private static final int SNAPLEN = 65536; // 최대 스냅샷 길이, 65536 바이트, 모든 패킷 캡처 가능
    private static final int TIMEOUT = 10; // 타임아웃 시간, 밀리초 단위

    // Utility class, prevent instantiation
    private NICUtils() { }

    /**
     * 모든 네트워크 인터페이스 카드(NIC)를 검색하고
     * 각 NIC에 대한 {@link PcapHandle} 목록을 반환합니다.
     * <br>
     * 각 NIC 설정 
     * <ul>
     *  <li>snaplen : 패킷의 최대 캡처 길이 (바이트 기준)</li>
     *  <li>PromiscuousMode.PROMISCUOUS : 프로미스큐어스 모드, 자신의 MAC 주소가 아닌 패킷도 모두 수신</li>
     *  </li>timeout : 최대 대기시간 (밀리초)</li>
     * </ul>
     * 
     * @return 각 NIC에 대한 {@link PcapHandle} 목록
     * @throws PcapNativeException NIC 검색 또는 핸들 생성 실패 시
     */
    public static List<PcapHandle> createHandles() throws PcapNativeException {
        try {
            List<PcapNetworkInterface> nicList = Pcaps.findAllDevs();
            return nicList.stream().map(nic -> {
                try {
                    return nic.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to open NIC handle for: " + nic.getName(), e);
                }
            }).toList();
        } catch (PcapNativeException e) {
        throw new PcapNativeException("Failed to create NIC handles", e);
        }
    }
}
