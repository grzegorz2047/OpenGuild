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

package pl.grzegorz2047.openguild2047.listeners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;

public class PlayerChatListener implements Listener {

    private final Guilds guilds;


    public PlayerChatListener(Guilds guilds) {
        this.guilds = guilds;
    }

    @EventHandler
    public void handleEvent(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage().replace("%", "");
        if (!isInGuild(player)) {

            if (GenConf.guildprefixinchat) {
                event.setFormat(ChatColor.translateAlternateColorCodes('&',
                        GenConf.chatFormat
                                .replace("{PLAYER}", player.getName())
                                .replace("{GUILD}", "")
                                .replace("{MESSAGE}", message)));
            }
            return;
        }
        processChatForPlayerWithGuild(event, player, uuid, message);
    }

    private void processChatForPlayerWithGuild(AsyncPlayerChatEvent event, Player player, UUID uuid, String message) {
        Guild guild = guilds.getPlayerGuild(uuid);
        String tag = guild.getName().toUpperCase();
        String format;
        if (isInGuildChatMode(message, GenConf.channelOnlyGuildKey)) {
            message = message.substring(1);
            format = prepareMessageFormat(player, message);
            guild.notifyGuild(format);
            event.setCancelled(true);
            return;
        }
        if (isInGuildChatMode(message, GenConf.channelAllyAndGuildKey)) {
            message = message.substring(1);
            format = prepareMessageFormat(player, message);
            guild.notifyGuild(format);
            event.setCancelled(true);
            sendMessageToAllAllies(player, message, guild);
            return;
        }
        String msgFormat = event.getFormat();

        if (!GenConf.guildprefixinchat) {
            if (msgFormat.contains("%OPENGUILD_TAG%")) {
                event.setFormat(msgFormat.replace("%OPENGUILD_TAG%", tag));
                return;
            }
        }
        event.setFormat(ChatColor.translateAlternateColorCodes('&',
                GenConf.chatFormat
                        .replace("{PLAYER}", player.getName())
                        .replace("{GUILD}", guild.getName())
                        .replace("{MESSAGE}", message)));
    }

    private void sendMessageToAllAllies(Player player, String message, Guild guild) {
        for (Relation r : guild.getAlliances()) {
            sendMessageToAlly(player, message, guild, r);
        }
    }

    private void sendMessageToAlly(Player player, String message, Guild guild, Relation r) {
        Guild ally;
        String whoGuildTag = r.getWho();
        Guild whoGuild = guilds.getGuild(whoGuildTag);
        ally = getAllyGuildFromRelation(guild, r, whoGuild);
        if (ally != null) {
            String format = GenConf.allyChatFormat
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

    private String getFirstLetterOfMsg(String message) {
        return message.substring(1);
    }

    private String prepareMessageFormat(Player player, String message) {
        return GenConf.guildChatFormat
                .replace("{PLAYER}", player.getName())
                .replace("{MESSAGE}", message);
    }

    private boolean isInGuildChatMode(String message, String allyChatKey) {
        return message.startsWith(allyChatKey) && message.length() >= 2;
    }

    private boolean isInGuild(Player player) {
        return guilds.hasGuild(player);
    }
}
