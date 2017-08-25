package pl.grzegorz2047.openguild2047.ranking;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by grzeg on 20.12.2015.
 */
public class EloRanking {

    private final Plugin plugin;
    private final SQLHandler sqlHandler;
    private LinkedHashMap<String, Integer> userRank = new LinkedHashMap<String, Integer>();
    private LinkedHashMap<String, Integer> guildRank = new LinkedHashMap<String, Integer>();


    public EloRanking(Plugin plugin, SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
        this.plugin = plugin;
    }

    public LinkedHashMap<String, Integer> getGuildRank() {
        return guildRank;
    }

    public void setGuildRank(LinkedHashMap<String, Integer> guildRank) {
        this.guildRank = guildRank;
    }


    public void checkUserpoints() {
    }

    public void checkGuildpoints(Guild guild) {
        /*if (guildRank.get(guildTag) != null) {
          //  guildRank.put(guildTag, guild.getGuildPoints());
        } else {
            if (guildRank.size() < 15) {
                guildRank.put(guildTag, guild.getGuildPoints());
            } else {
                int maxVal = 0;
                String topGuildTag = "";
                for (Map.Entry<String, Integer> entry : guildRank.entrySet()) {
                    if (guild.getGuildPoints() > entry.getValue()) {
                        maxVal = entry.getValue();
                        topGuildTag = entry.getKey();
                    }
                }
                if (!topGuildTag.isEmpty()) {
                    guildRank.remove(topGuildTag);
                    guildRank.put(guildTag, guild.getGuildPoints());
                }
            }

        }*/
    }


    public void addUserPoints(String username, int points) {
        userRank.put(username, points);

    }

    public void addGuildPoints(String guildTag, int points) {
        guildRank.put(guildTag, points);
    }

    public void printUserRanking(Player p) {
        int index = 1;
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.translateAlternateColorCodes('&', ("&7Topka graczy:")));
        for (Map.Entry<String, Integer> entry : userRank.entrySet()) {
            //System.out.println("Ustawiam wartosc "+entry.getValue()+" dla osoby "+entry.getKey());
            sb.append(ChatColor.translateAlternateColorCodes('&', (index++ + ". " + entry.getKey() + ": " + entry.getValue())));
        }
        p.sendMessage(sb.toString());
    }

    public void reCountGuildPoints(Guild guild) {
        /*int guildPoints = 0;
        int guildSize = guild.getMembers().size();
        for (String member : guild.getMembers()) {
            User u = new User(member);
            plugin.getManager().getMysqlManager().getUserQuery().getPlayer(u);
            guildPoints += u.getPoints();
        }
        guildPoints = guildPoints / guildSize;
        guild.setGuildPoints(guildPoints);
        plugin.getManager().getMysqlManager().getGuildQuery().updateGuild(guild);*/
    }

    /*
    https://metinmediamath.wordpress.com/2013/11/27/how-to-calculate-the-elo-rating-including-example/



     */
    public void recountEloFight(Player winner, Player lost) {
        int wOldPoints = winner.getMetadata("elo").get(0).asInt();
        int wKFactor = getKFactor(wOldPoints);

        int lOldPoints = lost.getMetadata("elo").get(0).asInt();
        int lKFactor = getKFactor(lOldPoints);


        double w = Math.pow(10, wOldPoints / 400);//R(1)
        double l = Math.pow(10, lOldPoints / 400);//R(2)

        double ew = w / (w + l);//E(1) Oblicz oczekiwany wynik
        double el = l / (w + l);//E(2) Suma ew i el = 1

        int ws = 1;//S(1) Ustaw kto wygral
        int ls = 0;//S(2) a kto przegral

        double wNewPoints = wOldPoints + wKFactor * (ws - ew); //Oblicz nowe puntky
        double lNewPoints = lOldPoints + wKFactor * (ls - el);

        //Bukkit.broadcastMessage("Nowe punkty z " + wOldPoints + " na " + wNewPoints);
        //Bukkit.broadcastMessage("Nowe punkty z " + lOldPoints + " na " + lNewPoints);

        winner.setMetadata("elo", new FixedMetadataValue(plugin, (int) wNewPoints));
        lost.setMetadata("elo", new FixedMetadataValue(plugin, (int) lNewPoints));

        int wDiff = (int) (wOldPoints - wNewPoints);
        int lDiff = (int) (lOldPoints - lNewPoints);

        Bukkit.broadcastMessage(MsgManager.get("killerkilledvictim").
                replace("{VICTIM}", lost.getName()).
                replace("{KILLER}", winner.getName()).
                replace("{VICTIMLOSE}", -lDiff + "").
                replace("{KILLEREARN}", -wDiff + ""));

        sqlHandler.updatePlayersElo(winner.getUniqueId(), (int) wNewPoints, lost.getUniqueId(), (int) lNewPoints);
    }


    public int getKFactor(int wOldPoints) {
        int wKFactor = 30;
        if (wOldPoints < 2000) {
            wKFactor = 30;
        } else if (wOldPoints >= 2000 && wOldPoints <= 2400) {
            wKFactor = 130 - ((wOldPoints) / 20);
        } else if (wOldPoints > 2400) {
            wKFactor = 10;
        }
        return wKFactor;
    }


    public LinkedHashMap<String, Integer> getUserRank() {
        return userRank;
    }
}