package framework.adapter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import framework.core.data.Chunk;

public class Pcap4jAdapter {
    private static final int SNAPLEN = 65536; // 최대 스냅샷 길이, 65536 바이트, 모든 패킷 캡처 가능
    private static final int TIMEOUT = 10; // 타임아웃 시간, 밀리초 단위
    
    private static final Pcap4jAdapter INSTANCE = new Pcap4jAdapter();
    private final List<PcapHandle> nicHandleList;
    private final ExecutorService threadPool;

    /**
     * 싱글톤 패턴을 사용하여 인스턴스를 생성합니다.
     * <br>
     * 각 NIC에 대한 {@link PcapHandle} 목록을 초기화하고,
     * NIC 수에 따라 고정된 크기의 스레드 풀을 생성합니다.
     */
    private Pcap4jAdapter() {
        try {
            this.nicHandleList = getNICHandleList();
            this.threadPool = Executors.newFixedThreadPool(nicHandleList.size());
        } catch (PcapNativeException e) {
            throw new RuntimeException("Failed to initialize NIC handles", e);
        }
    }


    /**
     * 싱글톤 {@link Pcap4jAdapter} 인스턴스를 반환합니다.
     * @return 초기화된 {@link Pcap4jAdapter} 인스턴스
     * @throws NullPointerException 인스턴스가 초기화되지 않은 경우
     */
    public static Pcap4jAdapter getInstance() {
        return Objects.requireNonNull(INSTANCE, "Pcap4jAdapter instance is not initialized");
    }


    /**
     * 지정된 {@link PcapHandle}에서 패킷을 블로킹 방식으로 수신합니다.
     * 
     * @param handle 패킷을 수신할 {@link PcapHandle}
     */
    private void receive(PcapHandle handle) {
        try{
            while(!Thread.currentThread().isInterrupted()) {
                Packet packet = handle.getNextPacketEx(); //blocking method
                if(packet != null) {
                    byte[] rawData = packet.getRawData();
                    //TODO: rawData 처리 로직 추가
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error receiving packets on handle: ", e);
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }


    /**
     * 각 NIC에 대한 패킷 수신을 시작합니다.
     */
    public void run() {
        for(PcapHandle handle : nicHandleList) {
            threadPool.submit(() -> receive(handle));
        }
    }


    public void send(Chunk chunk) {
        byte[] header = chunk.getHeader().getBytes();
        byte[] payload = chunk.getPayload().getBytes();

        byte[] packetData = new byte[header.length + payload.length];
        System.arraycopy(header, 0, packetData, 0, header.length);
        System.arraycopy(payload, 0, packetData, header.length, payload.length);

        //TODO: 패킷 전송 로직 추가
    }

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
    private List<PcapHandle> getNICHandleList() throws PcapNativeException {
        List<PcapNetworkInterface> nicList = Pcaps.findAllDevs();
        return nicList.stream().map(nic -> {
            try {
                return nic.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, TIMEOUT);
            } catch (Exception e) {
                throw new RuntimeException("Failed to open NIC handle for: " + nic.getName(), e);
            }
        }).toList();
    }
}
