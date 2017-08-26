package pl.grzegorz2047.openguild2047.tntguildblocker;

import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.*;

/**
 * Created by grzeg on 26.08.2017.
 */
public class TntGuildBlocker {
    private final Map<String, Long> blockedGuilds = new HashMap<String, Long>();


    public boolean isGuildBlocked(String guildName) {
        return blockedGuilds.containsKey(guildName);
    }

    public void addGuildAsBlocked(String guildTag, int time) {
        blockedGuilds.put(guildTag, System.currentTimeMillis() + (time * 1000));
    }


    public String getTimeRemainingForBlockedGuild(String guildName) {
        long milis = blockedGuilds.get(guildName);
        long left = milis - System.currentTimeMillis();
        long secondsLeft = left / 1000;
        return String.valueOf(secondsLeft);
    }

    public void checkTimesForBlockedGuilds() {
        List<String> toDelete = new ArrayList<String>();
        Set<Map.Entry<String, Long>> entries = blockedGuilds.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            long left = entry.getValue() - System.currentTimeMillis();
            if (left < 0) {
                toDelete.add(entry.getKey());
            }
        }
        for (String guildName : toDelete) {
            blockedGuilds.remove(guildName);
        }
        toDelete.clear();
    }
}
