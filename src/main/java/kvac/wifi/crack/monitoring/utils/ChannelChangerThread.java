/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kvac.wifi.crack.monitoring.utils;

import org.pcap4j.core.PcapNetworkInterface;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jdcs_dev
 */
public class ChannelChangerThread extends Thread implements Runnable {

    final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private final PcapNetworkInterface dev;

    public ChannelChangerThread(PcapNetworkInterface dev) {
        this.dev = dev;
    }

    @Override
    public void run() {
        do {
            try {

                Thread.sleep(500);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        } while (true);
    }

}
