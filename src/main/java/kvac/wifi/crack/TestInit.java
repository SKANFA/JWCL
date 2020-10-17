package kvac.wifi.crack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestInit {

    final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws PcapNativeException {
        TestInit testInit = new TestInit();
        try {
            testInit.pro();
        } catch (Exception e) {
            testInit.logger.error("", e);
        }
    }

    private void pro() throws PcapNativeException, NotOpenException, InterruptedException {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 50; // in milliseconds

        for (PcapNetworkInterface dev : allDevs) {
            if (dev.getName().equals("wlp8s0")) {
                logger.info(dev.getName());
                PcapHandle handle = dev.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout);
                PcapDumper dumper = handle.dumpOpen("dump.pcap");
                String pattern = "88 8e 0[123] 0[0123]";
                Pattern r = Pattern.compile(pattern);
                PacketListener listener = (Packet packet) -> {
                    String packetString = packet.toString();

                    Matcher m = r.matcher(packetString);
                    if (m.find()) {
                        try {
                            dumper.dump(packet, handle.getTimestamp());
                            logger.warn("AAAAAAAAAA:" + packetString);
                        } catch (NotOpenException ex) {
                            logger.error("", ex);
                        }
                    } else {
                        //     logger.warn("BBBBBBBBBB:" + packetString);
                    }
                };
                handle.loop(-1, listener);
            }

        }
    }

}
