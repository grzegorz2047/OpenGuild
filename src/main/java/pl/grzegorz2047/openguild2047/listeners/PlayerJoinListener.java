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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.BagOfEverything;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.database.SQLRecord;
import pl.grzegorz2047.openguild2047.database.TempPlayerData;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

public class PlayerJoinListener implements Listener {

    private final TagManager tagManager;
    private final Guilds guilds;
    private final SQLHandler sqlHandler;
    private final TempPlayerData tempPlayerData;
    private final Plugin p;
    private String joinMsg;

    public PlayerJoinListener(Guilds guilds, TagManager tagManager, SQLHandler sqlHandler, TempPlayerData tempPlayerData, Plugin p) {
        this.guilds = guilds;
        this.p = p;
        this.tagManager = tagManager;
        this.tempPlayerData = tempPlayerData;
        this.sqlHandler = sqlHandler;
        this.joinMsg = ChatColor.translateAlternateColorCodes('&', MsgManager.getIgnorePref("playerjoinedservermsg"));
    }

    @EventHandler
    public void handleEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
         event.setJoinMessage(joinMsg.replace("%PLAYER%", player.getName()));
        //System.out.print("Wykonuje playerJoinEvent!");


        //Pobierz dane gracza
        SQLRecord playerRecord = this.tempPlayerData.getPlayerRecord(uuid);

        if (playerRecord != null) {
            preparePlayerData(player, uuid, playerRecord);
        } else {
            if (!player.hasPlayedBefore()) {
                sqlHandler.insertPlayer(uuid);
                guilds.updatePlayerMetadata(uuid, "guild", "");
                guilds.updatePlayerMetadata(uuid, "elo", 1000);
                guilds.updatePlayerMetadata(uuid, "kills", 0);
                guilds.updatePlayerMetadata(uuid, "deaths", 0);
            } else {
                this.sqlHandler.getPlayerData(player.getUniqueId(), tempPlayerData);
                playerRecord = this.tempPlayerData.getPlayerRecord(uuid);
                preparePlayerData(player, uuid, playerRecord);
            }

        }

        tagManager.prepareScoreboardTagForPlayerOnJoin(player);

        notifyOpAboutUpdate(player);
    }

    private void preparePlayerData(Player player, UUID uuid, SQLRecord playerRecord) {
        guilds.updatePlayerMetadata(uuid, "guild", playerRecord.getGuild());
        guilds.updatePlayerMetadata(uuid, "elo", playerRecord.getElo());
        guilds.updatePlayerMetadata(uuid, "kills", playerRecord.getKills());
        guilds.updatePlayerMetadata(uuid, "deaths", playerRecord.getDeaths());
        this.tempPlayerData.removePlayer(uuid);
        if (guilds.hasGuild(player)) {
            Guild guild = guilds.getPlayerGuild(uuid);
            //tagManager.refreshScoreboardTagsForAllPlayersOnServerApartFromJoiner(player, guild);
            tagManager.refreshScoreboardTagsForAllPlayersOnServerApartFromJoiner(player, guild);

            guilds.addOnlineGuild(guild.getName());
            guilds.notifyMembersJoinedGame(player, guild);
        }
    }

    private void notifyOpAboutUpdate(Player player) {
        if (player.isOp() && BagOfEverything.getUpdater().isEnabled() && BagOfEverything.getUpdater().isAvailable()) {
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
            if (GenConf.lang.equalsIgnoreCase("PL")) {
                player.sendMessage(ChatColor.YELLOW + "Znaleziono aktualizacje! Prosze zaktualizowac Twój plugin do najnowszej wersji!");
                player.sendMessage(ChatColor.YELLOW + "Pobierz go z https://github.com/grzegorz2047/OpenGuild2047/releases");
            } else if (GenConf.lang.equalsIgnoreCase("SV")) {
                player.sendMessage(ChatColor.YELLOW + "Uppdatering hittas! Uppdatera ditt plugin till den senaste version!");
                player.sendMessage(ChatColor.YELLOW + "Ladda ner det från https://github.com/grzegorz2047/OpenGuild2047/releases");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Update found! Please update your plugin to the newest version!");
                player.sendMessage(ChatColor.YELLOW + "Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
            }
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
        }
    }


}
