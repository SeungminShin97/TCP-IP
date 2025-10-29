package framework.adapter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;

import framework.core.data.Chunk;
import framework.util.NICUtils;

public class Pcap4jAdapter {
    private final List<PcapHandle> nicHandleList;
    private final ExecutorService threadPool;

    /**
     * 프록시 생성자를 통해 외부에서 NIC 핸들 목록을 주입받아 인스턴스를 생성합니다.
     * <br>
     * 각 NIC에 대한 {@link PcapHandle} null값 검증 후 목록을 초기화하고,
     * NIC 수에 따라 고정된 크기의 스레드 풀을 생성합니다.
     * @param handles NIC 핸들 목록
     * @throws PcapNativeException 
     */
    private Pcap4jAdapter(List<PcapHandle> handles) {
        this.nicHandleList = Objects.requireNonNull(handles, "NIC handles cannot be null");
        this.threadPool = Executors.newFixedThreadPool(nicHandleList.size());
    }

    /**
     * 싱글톤 {@link Pcap4jAdapter} 인스턴스를 반환하는 정적 내부 클래스.
     * 
     * @throws ExceptionInInitializerError 인스턴스 생성 실패 시
     */
    public static class Holder {
        private static final Pcap4jAdapter INSTANCE;
        static {
            try {
                INSTANCE = new Pcap4jAdapter(NICUtils.createHandles());
            } catch (PcapNativeException e) {
                System.err.println("Failed to create Pcap4jAdapter instance: " + e.getMessage());
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * 싱글톤 {@link Pcap4jAdapter} 인스턴스를 반환합니다.
     * @return 초기화된 {@link Pcap4jAdapter} 인스턴스
     */
    public static Pcap4jAdapter getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 팩토리 메서드를 통해 외부에서 NIC 핸들 목록을 주입받아 인스턴스를 생성합니다.
     * 테스트를 위한 메서드입니다.
     * @param handles NIC 핸들 목록
     * @return 초기화된 {@link Pcap4jAdapter} 인스턴스
     */
    public static Pcap4jAdapter of(List<PcapHandle> handles) {
        return new Pcap4jAdapter(handles);
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

    /**
     * 패킷 수신을 중지하고 리소스를 정리합니다.
     * TODO: shutdown hook 등록 해야됨
     */
    public void stop() {
        threadPool.shutdownNow();
        for (PcapHandle handle : nicHandleList) {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
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
}
