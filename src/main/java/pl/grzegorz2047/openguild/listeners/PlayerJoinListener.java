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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.database.SQLRecord;
import pl.grzegorz2047.openguild.database.TempPlayerData;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.updater.Updater;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final TagManager tagManager;
    private final Guilds guilds;
    private final SQLHandler sqlHandler;
    private final TempPlayerData tempPlayerData;
    private final Updater updater;

    private final String joinMsg;

    private final int defaultEloPoints = 1000;

    public PlayerJoinListener(Guilds guilds, TagManager tagManager, SQLHandler sqlHandler, TempPlayerData tempPlayerData, Updater updater) {
        this.guilds = guilds;
        this.tagManager = tagManager;
        this.tempPlayerData = tempPlayerData;
        this.sqlHandler = sqlHandler;
        this.joinMsg = ChatColor.translateAlternateColorCodes('&', MsgManager.getIgnorePref("playerjoinedservermsg"));
        this.updater = updater;
    }

    @EventHandler
    public void handleEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        event.setJoinMessage(joinMsg.replace("%PLAYER%", player.getName()));
        //System.out.print("Wykonuje playerJoinEvent!");


        //Pobierz dane gracza
        SQLRecord playerRecord = this.tempPlayerData.getPlayerRecord(uuid);

        processPlayerRecord(player, uuid, playerRecord);
        tagManager.prepareScoreboardTagForPlayerOnJoin(player);

        updater.notifyOpAboutUpdate(player);
    }

    private void processPlayerRecord(Player player, UUID uuid, SQLRecord playerRecord) {
        if (playerRecord != null) {
            //System.out.println("PlRek1 " + playerRecord.toString());
            prepareCurrentPlayerData(player, uuid, playerRecord);
        } else {
            //System.out.println("PlRek ;< ");

            if (!player.hasPlayedBefore()) {
                //System.out.println("PlRek ;< never ");
                sqlHandler.insertPlayer(uuid);
                createDefaultPlayerMetaData(uuid);
            } else {
                //System.out.println("PlRek ;< retry");
                this.sqlHandler.getPlayerData(player.getUniqueId(), tempPlayerData);
                playerRecord = this.tempPlayerData.getPlayerRecord(uuid);
                if (playerRecord == null) {
                    sqlHandler.insertPlayer(uuid);
                    createDefaultPlayerMetaData(uuid);
                   // System.out.println("PlRek2 emergency");
                    return;
                }
                //System.out.println("PlRek2 " + playerRecord.toString());
                prepareCurrentPlayerData(player, uuid, playerRecord);
            }

        }
    }

    private void createDefaultPlayerMetaData(UUID uuid) {
        guilds.updatePlayerMeta(uuid, "", defaultEloPoints, 0, 0);
    }

    private void prepareCurrentPlayerData(Player player, UUID uuid, SQLRecord playerRecord) {
        String guildName = playerRecord.getGuild();
        int eloPoints = playerRecord.getElo();
        int playerKills = playerRecord.getKills();
        int playerDeaths = playerRecord.getDeaths();

        guilds.updatePlayerMeta(uuid, guildName, eloPoints, playerKills, playerDeaths);
        this.tempPlayerData.removePlayer(uuid);
        if (guilds.hasGuild(player)) {
            Guild guild = guilds.getPlayerGuild(uuid);
            tagManager.refreshScoreboardTagsForAllPlayersOnServerApartFromJoiner(player, guild);

            guilds.addOnlineGuild(guild.getName());
            guilds.notifyMembersJoinedGame(player, guild);
        }
    }


}
