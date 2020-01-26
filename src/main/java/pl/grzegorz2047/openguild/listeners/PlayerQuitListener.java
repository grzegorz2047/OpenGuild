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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.grzegorz2047.openguild.database.TempPlayerData;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.teleporters.TpaRequester;
import pl.grzegorz2047.openguild.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.packets.ScoreboardPackets;

public class PlayerQuitListener implements Listener {


    private final Guilds guilds;
    private final Cuboids cuboids;
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private final String quitMsg;
    private final ScoreboardPackets scoreboardPackets = new ScoreboardPackets();
    private final TempPlayerData tempPlayerData;

    public PlayerQuitListener(Guilds guilds, Cuboids cuboids, AntiLogoutManager logout, Teleporter teleporter, TpaRequester tpaRequester, TempPlayerData tempPlayerData) {
        this.guilds = guilds;
        this.cuboids = cuboids;
        this.logout = logout;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;
        this.tempPlayerData = tempPlayerData;
        this.quitMsg = ChatColor.translateAlternateColorCodes('&', MsgManager.getIgnorePref("playerleftservermsg"));

    }

    @EventHandler
    public void handleEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        String playerName = player.getName();
        event.setQuitMessage(quitMsg.replace("%PLAYER%", playerName));
        if (guilds.isPlayerInGuild(player)) {
            Guild g = guilds.getPlayerGuild(uniqueId);
            guilds.guildMemberLeftServer(player, uniqueId);
            String guildName = g.getName();
            if (!guilds.isGuildOnline(guildName)) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    scoreboardPackets.sendDeleteTeamTag(p, guildName);
                }
            }

        }
        tempPlayerData.removePlayer(uniqueId);
        logout.handleLogoutDuringFight(player, playerName);
        cuboids.clearCuboidEnterNotification(player);
        teleporter.removeRequest(uniqueId);
        tpaRequester.removeRequest(playerName);
    }


}
