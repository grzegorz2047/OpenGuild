package pl.grzegorz2047.openguild2047.teleporters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.HashMap;

import static pl.grzegorz2047.openguild2047.configuration.GenConf.TPA_EXPIRE_TIME;

public class TpaRequester {

    private HashMap<String, TpaRequest> requests = new HashMap<>();
    private int expireTime = TPA_EXPIRE_TIME * 1000;

    public boolean addRequest(String source, String destination) {
        if(requests.containsKey(source)) {
            return false;
        }
        requests.put(source, new TpaRequest(source, destination, System.currentTimeMillis()));
        return true;
    }

    public void checkExpiredTpaRequests() {
        HashMap<String, TpaRequest> expiredPlayers = new HashMap<>();
        gatherExpiredRequests(expiredPlayers);
        notifyPlayersAboutRequestExpiration(expiredPlayers);
        clearExpiredRequestsFromList(expiredPlayers);
    }

    private void notifyPlayersAboutRequestExpiration(HashMap<String, TpaRequest> expiredPlayers) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (expiredPlayers.containsKey(p.getName())) {
                TpaRequest request = expiredPlayers.get(p.getName());
                if (request.getDestination().equalsIgnoreCase(p.getName())) {
                    p.sendMessage(MsgManager.get("tpadestinationexpired").replace("%REQUESTER%", request.getSource()));
                } else {
                    p.sendMessage(MsgManager.get("tpasourceexpired").replace("%REQUESTED%", request.getDestination()));
                }
            }
        }
    }

    private void gatherExpiredRequests(HashMap<String, TpaRequest> expiredPlayers) {
        for (TpaRequest request : requests.values()) {
            long timePassed = System.currentTimeMillis() - request.getRequestTime();
            if (timePassed > expireTime) {
                expiredPlayers.put(request.getSource(), request);
                expiredPlayers.put(request.getDestination(), request);
            }
        }
    }

    private void clearExpiredRequestsFromList(HashMap<String, TpaRequest> expiredPlayers) {
        for (String expiredPlayer : expiredPlayers.keySet()) {
            requests.remove(expiredPlayer);
        }
    }

    public void removeRequest(String player) {
        requests.remove(player);
    }

    public boolean isRequesting(String sourceName) {
        return requests.containsKey(sourceName);
    }
}
