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

package pl.grzegorz2047.openguild.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;
import pl.grzegorz2047.openguild.relations.Relation;

import java.util.UUID;

public class PlayerChatListener implements Listener {

    private final Guilds guilds;
    public static String CHAT_FORMAT;
    public static String GUILD_CHAT_FORMAT;
    private final boolean GUILD_PREFIX_IN_CHAT_ENABLED;
    private final String GUILD_AND_ALLY_ONLY_CHAT_FORMAT;
    private final String GUILD_ONLY_CHAT_MSG_KEY;
    private final String GUILD_ONLY_CHAT_FORMAT;
    private final String GUILD_AND_ALLY_ONLY_CHAT_KEY;
    private final String CUSTOM_CHAT_TAG_CONFIG_LABEL = "{OPENGUILD_TAG}";

    public PlayerChatListener(Guilds guilds, FileConfiguration config) {
        this.guilds = guilds;
        GUILD_CHAT_FORMAT = config.getString("chat.guildChatFormat", "{GUILD} &7{PLAYER}&7: &f{MESSAGE}");
        CHAT_FORMAT = config.getString("chat.noGuildChatFormat", "&7{PLAYER}&7: &f{MESSAGE}");
        GUILD_PREFIX_IN_CHAT_ENABLED = config.getBoolean("chat.guildprefixinchat", true);
        GUILD_AND_ALLY_ONLY_CHAT_FORMAT = config.getString("chat.ally-format", "&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        GUILD_ONLY_CHAT_MSG_KEY = config.getString("chat.guild-key", "guild:");
        GUILD_ONLY_CHAT_FORMAT = config.getString("chat.guild-format", "&8[&aGuild&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        GUILD_AND_ALLY_ONLY_CHAT_KEY = config.getString("chat.ally-key", "allies:");
    }

    @EventHandler
    public void handleEvent(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();

        String message = event.getMessage().replace("%", "");
        if (guilds.hasGuild(player)) {
            processChatForPlayerWithGuild(event, player, message);
            return;
        } else {
            event.setFormat(event.getFormat().replace(CUSTOM_CHAT_TAG_CONFIG_LABEL, ""));
        }
        int elo = getPlayerElo(player);
        if (GUILD_PREFIX_IN_CHAT_ENABLED) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&',
                    CHAT_FORMAT
                            .replace("{ELO}", String.valueOf(elo))
                            .replace("{PLAYER}", player.getName())
                            .replace("{MESSAGE}", message)));
        }
    }

    private int getPlayerElo(Player player) {
        return player.getMetadata(PlayerMetadataController.PlayerMetaDataColumn.ELO.name()).get(0).asInt();
    }

    private void processChatForPlayerWithGuild(AsyncPlayerChatEvent event, Player player,  String message) {
        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        String guildName = guild.getName();
        String tag = guildName.toUpperCase();
        String format;
        if (isInGuildChatMode(message, GUILD_ONLY_CHAT_MSG_KEY)) {
            message = message.substring(1);
            format = prepareMessageFormat(player, message);
            guild.notifyGuild(format);
            event.setCancelled(true);
            return;
        }
        if (isInGuildChatMode(message, GUILD_AND_ALLY_ONLY_CHAT_KEY)) {
            message = message.substring(1);
            format = prepareMessageFormat(player, message);
            guild.notifyGuild(format);
            event.setCancelled(true);
            sendMessageToAllAllies(player, message, guild);
            return;
        }
        String msgFormat = event.getFormat();
        if (!GUILD_PREFIX_IN_CHAT_ENABLED && msgFormat.contains(CUSTOM_CHAT_TAG_CONFIG_LABEL)) {
            event.setFormat(msgFormat.replace(CUSTOM_CHAT_TAG_CONFIG_LABEL, tag));
            return;
        }
        int elo = getPlayerElo(player);
        event.setFormat(ChatColor.translateAlternateColorCodes('&',
                GUILD_CHAT_FORMAT
                        .replace("{ELO}", String.valueOf(elo))
                        .replace("{PLAYER}", player.getName())
                        .replace("{GUILD}", tag)
                        .replace("{MESSAGE}", message)));
    }

    private void sendMessageToAllAllies(Player player, String message, Guild guild) {
        for (Relation r : guild.getAlliances()) {
            sendMessageToAlly(player, message, guild, r);
        }
    }

    private void sendMessageToAlly(Player player, String message, Guild guild, Relation r) {
        Guild ally;
        String whoGuildTag = r.getBaseGuildTag();
        Guild whoGuild = guilds.getGuild(whoGuildTag);
        ally = getAllyGuildFromRelation(guild, r, whoGuild);
        int elo = getPlayerElo(player);
        if (ally != null) {
            String format = GUILD_AND_ALLY_ONLY_CHAT_FORMAT
                    .replace("{ELO}", String.valueOf(elo))
                    .replace("{GUILD}", guild.getName())
                    .replace("{PLAYER}", player.getName())
                    .replace("{MESSAGE}", message);
            ally.notifyGuild(format);
        }
    }

    private Guild getAllyGuildFromRelation(Guild guild, Relation r, Guild whoGuild) {
        Guild ally;
        if (guild.equals(whoGuild)) {
            String withWhoGuildTag = r.getAlliedGuildTag();
            ally = guilds.getGuild(withWhoGuildTag);
        } else {
            ally = whoGuild;
        }
        return ally;
    }

    private String prepareMessageFormat(Player player, String message) {
        int elo = getPlayerElo(player);
        return GUILD_ONLY_CHAT_FORMAT
                .replace("{ELO}", String.valueOf(elo))
                .replace("{PLAYER}", player.getName())
                .replace("{MESSAGE}", message);
    }

    private boolean isInGuildChatMode(String message, String allyChatKey) {
        return message.startsWith(allyChatKey) && message.length() >= 2;
    }

}
