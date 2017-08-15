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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.Teleporter;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class PlayerQuitListener implements Listener {


    private final Guilds guilds;
    private final Cuboids cuboids;
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;

    public PlayerQuitListener(Guilds guilds, Cuboids cuboids, AntiLogoutManager logout, Teleporter teleporter) {
        this.guilds = guilds;
        this.cuboids = cuboids;
        this.logout = logout;
        this.teleporter = teleporter;
    }

    @EventHandler
    public void handleEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (isPlayerInGuild(player)) {
            Guild guild = guilds.getPlayerGuild(uuid);
            guild.notifyGuildThatMemberLeft(player);
        }
        String playerName = player.getName();
        if (logout.isPlayerDuringFight(playerName)) {
            Player potentialKiller = Bukkit.getPlayer(logout.getPotentialKillerName(playerName));
            player.damage(400, potentialKiller);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            logout.removePlayerFromFight(playerName);
            if (potentialKiller != null) {

            }
            Bukkit.broadcastMessage(MsgManager.get("playerlogoutduringfight").replace("%player", playerName));
        }
        cuboids.clearCuboidEnterNotification(player);
        teleporter.removeRequest(uuid);
    }

    private boolean isPlayerInGuild(Player player) {
        return guilds.hasGuild(player);
    }
}
