package pl.grzegorz2047.openguild2047.guilds;

/**
 * Created by grzeg on 23.08.2017.
 */
public class GuildInvitation {
    private final long inviteTime;
    private final String inviteePlayer;
    private final String hostGuild;
    private int invitationExpireTime = 20;

    public GuildInvitation(String hostGuild, String inviteePlayer, long inviteTime) {
        this.hostGuild = hostGuild;
        this.inviteePlayer = inviteePlayer;
        this.inviteTime = inviteTime;
    }

    public boolean isExpired(long currentTime) {
        return inviteTime + (invitationExpireTime * 1000) > currentTime;
    }

    public boolean isInvited(String playerName) {
        return inviteePlayer.equals(playerName);
    }

    public String getInviteePlayer() {
        return inviteePlayer;
    }

    public CharSequence getHostGuild() {
        return hostGuild;
    }
}
