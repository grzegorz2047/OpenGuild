package pl.grzegorz2047.openguild2047;

import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;

/**
 * Created by grzeg on 13.08.2017.
 */
public class Watcher implements Runnable {
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private int seconds;

    public Watcher(AntiLogoutManager logout, Teleporter teleporter, TpaRequester tpaRequester) {
        this.logout = logout;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;
    }

    @Override
    public void run() {
        seconds++;
        if (seconds % 60 == 0) {
            seconds = 0;
        }

        if (GenConf.ANTI_LOGOUT) {
            logout.updatePlayerActionBar();
            logout.checkExpiredFights();
        }

        if(GenConf.TELEPORT_COOLDOWN > 0) {
            teleporter.checkHomeRequests();
        }

        if(GenConf.TPA_ENABLED) {
            tpaRequester.checkExpiredTpaRequests();
        }
    }

}
