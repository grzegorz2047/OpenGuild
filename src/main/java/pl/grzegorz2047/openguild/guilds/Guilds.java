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
package pl.grzegorz2047.openguild.guilds;

import java.util.*;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.relations.Relation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;

public class Guilds {

    private final GuildInvitations guildInvitations;
    private final Plugin plugin;
    private final Cuboids cuboids;
    private Map<String, Guild> guilds = new HashMap<>();
    private List<String> onlineGuilds = new ArrayList<>();


    public Guilds(SQLHandler sqlHandler, Plugin plugin, Cuboids cuboids) {
        this.cuboids = cuboids;
        this.plugin = plugin;
        this.guildInvitations = new GuildInvitations(sqlHandler, this);
    }


    public void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, String guildscuboidtag, Guild enemy) {
        Guild guild = getGuild(guildscuboidtag);
        for (UUID mem : guild.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(mem);
            if (op.isOnline()) {
                if (enemy != null) {
                    notifySomeoneEnteredCuboid(op, player, enemy);
                } else {
                    notifySomeoneEnteredCuboid(op, player);
                }
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (GenConf.cubNotifySound) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player) {
        op.getPlayer().sendMessage(
                MsgManager.get("entercubmemsnoguild")
                        .replace("{PLAYER}", player.getName()));


    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player, Guild enemy) {

        op.getPlayer().sendMessage(
                MsgManager.get("entercubmems")
                        .replace("{PLAYER}", player.getName())
                        .replace("{GUILD}", enemy.getName().toUpperCase()));

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

    private void notifyGuildThatMemberLeft(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberleft");
        guild.notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    public boolean isPlayerInGuild(Player player) {
        return hasGuild(player);
    }


    private void verifyOnlineGuild(Player player, Guild guild) {
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
        List<MetadataValue> metadata = Bukkit.getPlayer(uuid).getMetadata("guild");
        String guildTag = metadata.get(0).asString();
        return guilds.get(guildTag);
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
        List<MetadataValue> metadata = Bukkit.getPlayer(uuid).getMetadata("guild");
        if (metadata.size() == 0) {
            return false;
        }
        String guildTag = metadata.get(0).asString();
        return !Objects.equals(guildTag, "");
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


    /**
     * @return map which contains all players, who are members of guilds.
     */

    public Guild addGuild(Location home, UUID owner, String tag, String description) {
        Guild guild =
                new Guild(
                        tag,
                        description,
                        home,
                        owner
                );
        guild.addMember(owner);
        guilds.put(tag, guild);
        return guild;
    }

    public void invitePlayer(final Player player, Player who, Guild guild) {
        String guildName = guild.getName();

        guildInvitations.addGuildInvitation(player, who, guild, guildName);
    }


    public void addOnlineGuild(String guild) {
        if (onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.add(guild);
    }

    public void removeOnlineGuild(String guild) {
        if (!onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.remove(guild);
    }

    public boolean isGuildOnline(String guild) {
        return onlineGuilds.contains(guild);
    }


    public void updatePlayerMetadata(UUID uniqueId, String column, Object value) {
        Bukkit.getPlayer(uniqueId).setMetadata(column, new FixedMetadataValue(plugin, value));
    }

    public int getNumberOfGuilds() {
        return guilds.size();
    }

    public void addPlayer(UUID uuid, Guild playersGuild) {
        updatePlayerMetadata(uuid, "guild", playersGuild.getName());
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
        this.guildInvitations.acceptGuildInvitation(player, guild);
    }

    public Guild getGuild(Location location) {
        return guilds.get(cuboids.getGuildTagInLocation(location));
    }
}
