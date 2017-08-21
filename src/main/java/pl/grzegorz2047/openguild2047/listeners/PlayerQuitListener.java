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

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.Teleporter;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.TpaRequest;
import pl.grzegorz2047.openguild2047.TpaRequester;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.packets.ScoreboardPackets;

public class PlayerQuitListener implements Listener {


    private final Guilds guilds;
    private final Cuboids cuboids;
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private final String quitMsg;
    private final ScoreboardPackets scoreboardPackets = new ScoreboardPackets();

    public PlayerQuitListener(Guilds guilds, Cuboids cuboids, AntiLogoutManager logout, Teleporter teleporter, TpaRequester tpaRequester) {
        this.guilds = guilds;
        this.cuboids = cuboids;
        this.logout = logout;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;

        this.quitMsg = ChatColor.translateAlternateColorCodes('&', MsgManager.getIgnorePref("playerleftservermsg"));

    }

    @EventHandler
    public void handleEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        event.setQuitMessage(quitMsg.replace("%PLAYER%", player.getName()));
        if (guilds.isPlayerInGuild(player)) {
            Guild g = guilds.getPlayerGuild(player.getUniqueId());
            guilds.guildMemberLeftServer(player, uuid);
            for (Player p : Bukkit.getOnlinePlayers()) {
                scoreboardPackets.sendDeleteTeamTag(p, g.getName());
            }
        }
        String playerName = player.getName();
        logout.handleLogoutDuringFight(player, playerName);
        cuboids.clearCuboidEnterNotification(player);
        teleporter.removeRequest(uuid);
        tpaRequester.removeRequest(event.getPlayer().getName());
    }


}
