package local.framework.util;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

import framework.util.NICUtils;

import static org.junit.jupiter.api.Assertions.*;

public class NICUtilsTest {
    
    @Test
    void testCreateHandles() throws PcapNativeException {
        //given 
        List<PcapHandle> handles = NICUtils.createHandles();
        
        //then
        assertNotNull(handles);
        assertFalse(handles.isEmpty(), "NIC Handle list should not be empty.");

        for (PcapHandle handle : handles) {
            assertNotNull(handle, "Each NIC Handle should not be null.");
            assertTrue(handle.isOpen(), "Each NIC Handle should be open.");
            handle.close();
        }
    }
}
