/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kvac.wifi.crack.monitoring;

/**
 *
 * @author jdcs_dev
 */
public class ProcessException extends Exception {

    public ProcessException() {
    }

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }

    public ProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
