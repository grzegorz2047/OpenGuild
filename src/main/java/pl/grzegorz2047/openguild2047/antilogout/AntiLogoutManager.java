package pl.grzegorz2047.openguild2047.antilogout;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grzeg on 23.12.2015.
 */
public class AntiLogoutManager {
    HashMap<String, Fight> fightList = new HashMap<String, Fight>();

    public AntiLogoutManager() {

    }

    public HashMap<String, Fight> getFightList() {
        return fightList;
    }

    public void clearFightList() {
        fightList.clear();
    }

    public void checkExpiredFights() {
        List<String> toDelete = new ArrayList<String>();
        for (Map.Entry<String, Fight> entry : fightList.entrySet()) {
            // Bukkit.getLogger().log(Level.INFO, "checkFights_for_enter");
            if (hasFightExpired(entry.getValue().getEndCooldown())) {
                toDelete.add(entry.getKey());
                Player p = Bukkit.getPlayer(entry.getKey());
                sendCanLogoutMsg(p);
            }
        }
        removeExpiredFights(toDelete);
    }

    private void removeExpiredFights(List<String> toDelete) {
        for (String user : toDelete) {
            fightList.remove(user);
        }
    }

    private void sendCanLogoutMsg(Player p) {
        if (p != null) {
            String message = "§6Mozesz juz sie wylogowac!";
            sendActionBar(p, message);
        }
    }

    private boolean hasFightExpired(long cooldown) {
        return cooldown <= System.currentTimeMillis();
    }

    private void sendActionBar(Player p, String message) {
        try {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        } catch (Exception ex) {
            p.sendMessage(message);
        }
    }

    public boolean isPlayerDuringFight(String name) {
        return fightList.containsKey(name);
    }

    public String getPotentialKillerName(String victim) {
        return fightList.get(victim).getAttacker();
    }

    public void updatePlayerActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerDuringFight(player.getName())) {
                long secCalculate = (fightList.get(player.getName()).getEndCooldown() - System.currentTimeMillis()) / 1000;
                sendActionBar(player, generateActionBarAntilogout(secCalculate));
            }
        }
    }

    private String generateActionBarAntilogout(long time) {
        StringBuilder out = new StringBuilder();
        out.append("§6§lFairPlay Checker");
        if (time == 0) {
            out.append("§4▉▉▉▉▉▉▉▉▉▉");
            out.append("§e ").append(time).append(" §6sec.");
            return out.toString();
        }
        float perc = (((float) time / (float) Fight.COOLDOWN) * 100);
        //System.out.println("Perc: " + perc);
        out.append(makeHealthString(perc));
        out.append("§e ").append(time).append(" §6sec.");
        return out.toString();
    }

    private String makeHealthString(float perc) {
        if (perc >= 100) {
            return "§c▉▉▉▉▉▉▉▉▉▉";
        }
        if (perc >= 90 && perc < 100) {
            return "§c▉▉▉▉▉▉▉▉▉§4▉";
        }
        if (perc >= 80 && perc < 90) {
            return "§c▉▉▉▉▉▉▉▉§4▉▉";
        }
        if (perc >= 70 && perc < 80) {
            return "§c▉▉▉▉▉▉▉§4▉▉▉";
        }
        if (perc >= 60 && perc < 70) {
            return "§c▉▉▉▉▉▉§4▉▉▉▉";
        }
        if (perc >= 50 && perc < 60) {
            return "§c▉▉▉▉▉§4▉▉▉▉▉";
        }
        if (perc >= 40 && perc < 50) {
            return "§c▉▉▉▉§4▉▉▉▉▉▉";
        }
        if (perc >= 30 && perc < 40) {
            return "§c▉▉▉§4▉▉▉▉▉▉▉";
        }
        if (perc >= 20 && perc < 30) {
            return "§c▉▉§4▉▉▉▉▉▉▉▉";
        }
        if (perc >= 10 && perc < 20) {
            return "§c▉§4▉▉▉▉▉▉▉▉▉";
        }
        if (perc < 10) {
            return "§4▉▉▉▉▉▉▉▉▉▉";
        }
        return "§4▉▉▉▉▉▉▉▉▉▉";
    }

    public void updatePlayersFight(Player attacker, Player attacked) {
        Fight vf = new Fight(attacker.getName(), attacked.getName(), System.currentTimeMillis());
        Fight af = new Fight(attacker.getName(), attacked.getName(), System.currentTimeMillis());
        fightList.put(attacked.getName(), vf);
        fightList.put(attacker.getName(), af);
    }

    public void dispose() {
        this.fightList.clear();
    }

    public void removePlayerFromFight(String playerName) {
        this.fightList.remove(playerName);
    }
}