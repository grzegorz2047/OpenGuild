package pl.grzegorz2047.openguild2047;

import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;

/**
 * Created by grzeg on 13.08.2017.
 */
public class Watcher implements Runnable {
    private final AntiLogoutManager logout;
    private int seconds;

    public Watcher(AntiLogoutManager logout) {
        this.logout = logout;
    }

    @Override
    public void run() {
        seconds++;
        if (seconds % 60 == 0) {
            seconds = 0;
        }
        if (GenConf.ANTI_LOGOUT) {
            logout.updatePlayerActionBar();
        }
    }

}
