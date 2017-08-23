/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.grzegorz2047.openguild2047.guilds;

import java.util.*;

import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class Guilds {

    private final SQLHandler sqlHandler;
    private final GuildInvitations guildInvitations;
    private Map<String, Guild> guilds = new HashMap<String, Guild>();
    private Map<UUID, Guild> mappedPlayersToGuilds = new HashMap<UUID, Guild>();
    private List<String> onlineGuilds = new ArrayList<>();


    public Guilds(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
        this.guildInvitations = new GuildInvitations(sqlHandler, this);
    }


    public void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, Guild g, String tag, boolean foundCuboid) {
        for (UUID mem : g.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(mem);
            if (op.isOnline()) {
                notifySomeoneEnteredCuboid(player, tag, op, foundCuboid);
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (GenConf.cubNotifySound) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(Player player, String tag, OfflinePlayer op, boolean foundCuboid) {
        if (GenConf.cubNotifyMem) {
            if (!foundCuboid) {

                op.getPlayer().sendMessage(MsgManager.get("entercubmemsnoguild").
                        replace("{PLAYER}", player.getName()));
            } else {
                op.getPlayer().sendMessage(MsgManager.get("entercubmems").
                        replace("{PLAYER}", player.getName()).
                        replace("{GUILD}", tag.toUpperCase()));
            }

        }
    }

    public List<Guild> getAllyGuilds(Guild g) {
        List<Guild> allies = new ArrayList<>();
        for (Relation r : g.getAlliances()) {
            String alliedGuildTag = r.getAlliedGuildTag();
            if (r.getAlliedGuildTag().equals(g.getName())) {
                alliedGuildTag = r.getBaseGuildTag();
            }
            Guild allyGuild = this.getGuild(alliedGuildTag);
            allies.add(allyGuild);
        }
        return allies;
    }

    public void guildMemberLeftServer(Player player, UUID uuid) {
        Guild guild = getPlayerGuild(uuid);
        if (guild == null) {
            return;
        }
        notifyGuildThatMemberLeft(player, guild);
        verifyOnlineGuild(player, guild);
    }

    public void notifyMembersJoinedGame(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberjoined");
        guild.notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    public void notifyGuildThatMemberLeft(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberleft");
        guild.notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    public boolean isPlayerInGuild(Player player) {
        return hasGuild(player);
    }


    public void verifyOnlineGuild(Player player, Guild guild) {
        List<String> onlineMembers = guild.getOnlineMembers();
        if (onlineMembers.size() == 0) {
            removeOnlineGuild(guild.getName());
        } else if (onlineMembers.size() == 1) {
            if (onlineMembers.contains(player.getName())) {
                removeOnlineGuild(guild.getName());
            }
        }
    }

    public Guild getPlayerGuild(UUID uuid) {
        if (this.hasGuild(uuid)) {
            return mappedPlayersToGuilds.get(uuid);
        }
        return null;
    }

    public Guild getGuild(String guildTag) {
        return guilds.get(guildTag);
    }

    public Map<String, Guild> getGuilds() {
        return guilds;
    }

    /**
     * @param uuid UUID of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(UUID uuid) {
        return mappedPlayersToGuilds.containsKey(uuid) && mappedPlayersToGuilds.get(uuid) != null;
    }

    /**
     * @param player Player class instance, of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(Player player) {
        return hasGuild(player.getUniqueId());
    }

    /**
     * @param tag tag of guild, for which existance should map be searched.
     * @return boolean
     */
    public boolean doesGuildExists(String tag) {
        return !tag.isEmpty() && guilds.containsKey(tag);
    }

    public void setGuilds(Map<String, Guild> guilds) {
        this.guilds = guilds;
    }


    public void setMappedPlayersToGuilds(Map<UUID, Guild> mappedPlayersToGuilds) {
        this.mappedPlayersToGuilds = mappedPlayersToGuilds;
    }

    /**
     * @return map which contains all players, who are members of guilds.
     */
    public Map<UUID, Guild> getMappedPlayersToGuilds() {
        return mappedPlayersToGuilds;
    }

    public Guild addGuild(OpenGuild plugin, Location home, UUID owner, String tag, String description) {
        Guild guild =
                new Guild(
                        tag,
                        description,
                        home,
                        owner,
                        Bukkit.getScoreboardManager().getNewScoreboard());
        guild.addMember(owner);
        guilds.put(tag, guild);
        return guild;
    }

    public void invitePlayer(final Player player, Player who, Guild guild) {
        final UUID uuid = player.getUniqueId();
        String guildName = guild.getName();

        guildInvitations.addGuildInvitation(player, who, guild, guildName);
    }


    public boolean addOnlineGuild(String guild) {
        if (onlineGuilds.contains(guild)) {
            return false;
        }
        return this.onlineGuilds.add(guild);
    }

    public boolean removeOnlineGuild(String guild) {
        if (!onlineGuilds.contains(guild)) {
            return false;
        }
        return this.onlineGuilds.remove(guild);
    }

    public boolean isGuildOnline(String guild) {
        return onlineGuilds.contains(guild);
    }


    public void updatePlayerGuild(UUID uniqueId, Guild guild) {
        mappedPlayersToGuilds.put(uniqueId, guild);
    }

    public int getNumberOfGuilds() {
        return guilds.size();
    }

    public void addPlayer(UUID uuid, Guild playersGuild) {
        mappedPlayersToGuilds.put(uuid, playersGuild);
    }

    public List<String> getOnlineGuilds() {
        return onlineGuilds;
    }

    public void checkPlayerInvitations() {
        guildInvitations.checkPlayerInvitations();
    }

    public GuildInvitation getGuildInvitation(String playerName, String guildName) {
        return guildInvitations.getPlayerInvitation(playerName, guildName);
    }

    public void acceptInvitation(Player player, Guild guild) {
        this.guildInvitations.acceptGuildInvitation(player,guild);
    }
}
