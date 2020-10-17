package kvac.wifi.crack;

import java.io.File;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class WirelessConfig {

    @Getter
    @Setter
    private int autoAddToListRate = 3000;

    @Getter
    @Setter
    private ArrayList<String> wlanListForExclude = new ArrayList<>();

    @Getter
    @Setter
    private File aircrack = new File(findProgrammFile("aircrack-ng", "path/to/aircrack-ng"));
    @Getter
    @Setter
    private File besside = new File(findProgrammFile("besside-ng", "path/to/besside-ng"));
    @Getter
    @Setter
    private File airmon = new File(findProgrammFile("airmon-ng", "path/to/airmon-ng"));
    @Getter
    @Setter
    private File sudo = new File(findProgrammFile("sudo", "path/to/sudo"));

    public WirelessConfig() {
        wlanListForExclude.add("wlan1");
    }

    private String findProgrammFile(String programName, String defaultPath) {
        String path = System.getenv("PATH");
        if (path.length() == 0) {
            return defaultPath;
        }
        String[] paths = path.split(":");
        File programFile = null;
        for (String pathTest : paths) {
            programFile = new File(pathTest, programName);
            if (programFile.exists()) {
                return programFile.getAbsolutePath();
            }
        }
        if (programFile == null) {
            programFile = new File(defaultPath);
        }
        return programFile.getAbsolutePath();
    }
}
