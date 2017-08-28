package pl.grzegorz2047.openguild.antilogout;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grzegorz2047 on 23.12.2015.
 */
public class AntiLogoutManager {
    HashMap<String, Fight> fightList = new HashMap<String, Fight>();

    public AntiLogoutManager() {

    }

    public HashMap<String, Fight> getFightList() {
        return fightList;
    }

    public void handleLogoutDuringFight(Player player, String playerName) {
        if (isPlayerDuringFight(playerName)) {
            Player potentialKiller = Bukkit.getPlayer(getPotentialKillerName(playerName));
            player.damage(400, potentialKiller);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            removePlayerFromFight(playerName);
            /*if (potentialKiller != null) {

            }*/
            Bukkit.broadcastMessage(MsgManager.get("playerlogoutduringfight").replace("%PLAYER%", playerName));
        }
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

    private boolean isPlayerDuringFight(String name) {
        return fightList.containsKey(name);
    }

    private String getPotentialKillerName(String victim) {
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
        String splitColor = "§4";
        String firstColor = "§c";
        String defaultAntiLogoutBar = "▉▉▉▉▉▉▉▉▉▉";
        if (perc <= 10) {
            return splitColor + defaultAntiLogoutBar;
        }
        int splitPosition = ((int) perc / 10) % 10;
        int splitIndexFixed = splitPosition - 1;
        String partBar = defaultAntiLogoutBar.substring(0, splitIndexFixed) + splitColor;
        String finalBar = partBar + defaultAntiLogoutBar.substring(splitIndexFixed + 1);
        finalBar = firstColor + finalBar;
        return finalBar;
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