package kvac.wifi.crack.monitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import kvac.wifi.crack.HEADER;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringProcess {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    @Setter
    private String ifname;

    @Getter
    private final ProcessBuilder processBuilder = new ProcessBuilder();

    @Getter
    @Setter
    private Process process;

    void init() {
        logger.info("init");
        ArrayList<String> commands = new ArrayList<>();
        commands.add(HEADER.getConfig().getSudo().getAbsolutePath());
        commands.add(HEADER.getConfig().getBesside().getAbsolutePath());
        commands.add(ifname);
        processBuilder.command(commands);

        File dirForSaveDataIFNAME = new File(HEADER.ROOT_DIR, "data");
        dirForSaveDataIFNAME = new File(dirForSaveDataIFNAME, ifname);
        if (!dirForSaveDataIFNAME.exists()) {
            dirForSaveDataIFNAME.mkdirs();
        }
        processBuilder.directory(dirForSaveDataIFNAME);
        logger.info("init-ok");
    }

    public void start() throws IOException {
        logger.info("start");
        setProcess(processBuilder.start());
        HEADER.processHandler.monitoringProcesses.add(this);
        logger.info("start-ok");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.toLowerCase().contains("Scanning chan".toLowerCase())
                                || line.toLowerCase().contains("Attacking".toLowerCase()) //
                                ) {
                            continue;
                        }
                        logger.info("IF:" + ifname + ": " + line);
                    }
                } catch (Exception e) {
                    logger.error(ifname, e);
                }
            }
        }, ifname).start();

    }

}
