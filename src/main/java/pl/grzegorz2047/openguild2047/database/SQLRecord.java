package pl.grzegorz2047.openguild2047.database;

/**
 * Created by grzeg on 25.08.2017.
 */
public class SQLRecord {

    private final String guild;
    private final String lastseenName;
    private final int elo;
    private final int kills;
    private final int deaths;

    public SQLRecord(String guild, String lastSeenName, int elo, int kills, int deaths) {
        this.guild = guild;
        this.lastseenName = lastSeenName;
        this.elo = elo;
        this.kills = kills;
        this.deaths = deaths;
    }

    public int getElo() {
        return elo;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public String getLastSeenName() {
        return lastseenName;
    }

    public String getGuild() {
        return guild;
    }

}
