package kvac.wifi.crack.monitoring.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import kvac.wifi.crack.HEADER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WirelessUtils {

    static Logger logger = LoggerFactory.getLogger(WirelessUtils.class);

    private WirelessUtils() {
    }

    public enum monitoringAction {
        START, STOP
    }

    public static CopyOnWriteArrayList<String> searchWlanNames() {
        CopyOnWriteArrayList<String> wirelessNames = new CopyOnWriteArrayList<>();
        File rootDirForSearch = new File("/", "sys");
        rootDirForSearch = new File(rootDirForSearch, "class");
        rootDirForSearch = new File(rootDirForSearch, "ieee80211");
        File[] phyS = rootDirForSearch.listFiles();
        for (File file : phyS) {
            File device = new File(file, "device");
            File net = new File(device, "net");
            File[] wlanFileS = net.listFiles();
            for (File wlan : wlanFileS) {
                wirelessNames.add(wlan.getName());
            }
        }
        return wirelessNames;
    }

    public static void reloadInterfaces() throws IOException {
        stopMonitoring();
        startMonitoring();
    }

    public static void startMonitoring() throws IOException {
        initMonitoring(monitoringAction.START);
    }

    public static void stopMonitoring() throws IOException {
        initMonitoring(monitoringAction.STOP);
    }

    public static void initMonitoring(monitoringAction action) throws IOException {
        File program = HEADER.config.getAirmon();
        File sudo = HEADER.config.getSudo();
        ProcessBuilder airmonProcessBuilder = new ProcessBuilder();

        CopyOnWriteArrayList<String> withOutExcludeList = searchWlanNamesExclude();

        for (String wlanName : withOutExcludeList) {

            ArrayList<String> commands = new ArrayList<>();
            commands.add(sudo.getAbsolutePath());
            commands.add(program.getAbsolutePath());
            commands.add(action.toString().toLowerCase());
            commands.add(wlanName);
            logger.info(Arrays.toString(commands.toArray()));

            airmonProcessBuilder.command(commands);
            airmonProcessBuilder.redirectErrorStream(true);

            Process airmonProcess = airmonProcessBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(airmonProcess.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        }
    }

    public static CopyOnWriteArrayList<String> searchWlanNamesExclude() {
        boolean isexclude = HEADER.config.getWlanListForExclude() != null;
        CopyOnWriteArrayList<String> list = WirelessUtils.searchWlanNames();
        for (String string : list) {
            if (isexclude) {
                for (String exclude : HEADER.config.getWlanListForExclude()) {
                    if (string.equals(exclude)) {
                        list.remove(string);
                    }
                }
            }
        }
        return list;
    }

}
