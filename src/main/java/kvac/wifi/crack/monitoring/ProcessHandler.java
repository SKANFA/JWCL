package kvac.wifi.crack.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    ArrayList<MonitoringProcess> monitoringProcesses = new ArrayList<>();

    public void startForIFname(String ifname) throws ProcessException, IOException {
        for (MonitoringProcess monitoringProcesse : monitoringProcesses) {
            if (monitoringProcesse.getIfname().equals(ifname)) {
                throw new ProcessException("process for" + ifname + " exist");
            }
        }//CHECK
        MonitoringProcess monitoringProcess = new MonitoringProcess();
        monitoringProcess.setIfname(ifname);
        monitoringProcess.init();
        monitoringProcess.start();
    }

}
