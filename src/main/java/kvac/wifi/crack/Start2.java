package kvac.wifi.crack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import kvac.wifi.crack.monitoring.ProcessException;
import kvac.wifi.crack.monitoring.utils.WirelessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start2 {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        Start2 start2 = new Start2();
        try {
            start2.initConfigs();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    start2.logger.info("EXIT HOOK");
                    WirelessUtils.stopMonitoring();
                    start2.logger.info("EXIT HOOKED");
                } catch (IOException e) {
                    start2.logger.error("", e);
                }
            }));
            start2.initProcesses();
        } catch (Exception e) {
            start2.logger.error("", e);
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

    private void initProcesses() throws ProcessException {
        for (String ifname : WirelessUtils.searchWlanNamesExclude()) {
            try {
                logger.info(ifname);
                HEADER.processHandler.startForIFname(ifname);
            } catch (IOException | ProcessException e) {
                logger.error(ifname, e);
            }
        }
        while (true) {

        }
    }
}
