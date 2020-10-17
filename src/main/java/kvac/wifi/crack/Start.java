package kvac.wifi.crack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kvac.wifi.crack.monitoring.utils.ChannelChangerThread;
import kvac.wifi.crack.monitoring.utils.WirelessUtils;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        Start start = new Start();
        try {
            start.initConfigs();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    start.logger.info("EXIT HOOK");
                    WirelessUtils.stopMonitoring();
                    start.logger.info("EXIT HOOKED");
                } catch (IOException e) {
                    start.logger.error("", e);
                }
            }));
            WirelessUtils.stopMonitoring();
            start.initListeners();

        } catch (Exception e) {
            start.logger.error("", e);
        }
    }

    private void initConfigs() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        if (!HEADER.CONFIGFILE.exists()) {
            logger.info("config file not exist");
            HEADER.CONFIGFILE.getParentFile().mkdirs();
            HEADER.CONFIGFILE.createNewFile();

            logger.info("search for existing interfaces");
            CopyOnWriteArrayList<String> wlanList = WirelessUtils.searchWlanNames();
            wlanList.forEach(string -> logger.info("interface: " + string));

            HEADER.config.getWlanListForExclude().addAll(wlanList);
            mapper.writeValue(HEADER.CONFIGFILE, HEADER.config);
            logger.info("config writed");
            Runtime.getRuntime().halt(3);
        }
        HEADER.setConfig(mapper.readValue(HEADER.CONFIGFILE, WirelessConfig.class));
    }

    private void initListeners() throws PcapNativeException, NotOpenException, InterruptedException {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 50; // in milliseconds
        CopyOnWriteArrayList<String> devWlan = WirelessUtils.searchWlanNamesExclude();
        for (PcapNetworkInterface dev : allDevs) {
            if (devWlan.contains(dev.getName())) {
                logger.info(dev.getName());
                PcapHandle handle = dev.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
                PcapDumper dumper = handle.dumpOpen(dev.getName() + "_AUTH.pcap");
                String pattern = "88 8e 0[123] 0[0123]";
                Pattern r = Pattern.compile(pattern);
                PacketListener listener = (Packet packet) -> {
                    String packetString = packet.toString();

                    Matcher m = r.matcher(packetString);
                    if (m.find()) {
                        try {
                            logger.warn(dev.getName() + packetString);
                            dumper.dump(packet, handle.getTimestamp());
                        } catch (NotOpenException ex) {
                            logger.error("", ex);
                        }
                    } else {
                        //     logger.warn("BBBBBBBBBB:" + packetString);
                    }
                };
                new ChannelChangerThread(dev).start();
                handle.loop(-1, listener);
            }
        }
    }

}
