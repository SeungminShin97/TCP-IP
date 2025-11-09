package local.framework.adapter;

import org.junit.jupiter.api.Test;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;

import framework.adapter.Pcap4jAdapter;

import java.io.EOFException;
import java.util.List;

import static org.mockito.Mockito.*;

public class Pcap4jAdapterTest {
    
    @Test
    void testRun_withMockHandles() throws Exception {
        // given
        PcapHandle mockHandle = mock(PcapHandle.class);
        Packet mockPacket = mock(Packet.class);

        when(mockHandle.getNextPacketEx())
            .thenReturn(mockPacket)
            .thenThrow(new NotOpenException());

        when(mockPacket.getRawData()).thenReturn("mock".getBytes());

        Pcap4jAdapter adapter = Pcap4jAdapter.of(List.of(mockHandle));

        //when
        adapter.run();

        //then
        verify(mockHandle, atLeastOnce()).getNextPacketEx();
        verify(mockPacket).getRawData();

        adapter.stop();
    }
}
