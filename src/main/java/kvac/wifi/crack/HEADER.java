/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kvac.wifi.crack;

import kvac.wifi.crack.monitoring.ProcessHandler;
import java.io.File;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jdcs_dev
 */
public class HEADER {

    private HEADER() {
    }

    public static final File ROOT_DIR = new File("WCrackByKVACSkanfa");
    public static final File CONFIGFILE = new File(ROOT_DIR, "WCrackConfig.yml");
    @Getter
    @Setter
    public static WirelessConfig config = new WirelessConfig();
    public static final ProcessHandler processHandler = new ProcessHandler();

}
