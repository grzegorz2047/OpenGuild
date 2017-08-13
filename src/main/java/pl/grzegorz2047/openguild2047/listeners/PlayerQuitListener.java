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

import com.github.grzegorz2047.openguild.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class PlayerQuitListener implements Listener {


    private final Guilds guilds;
    private final Cuboids cuboids;
    private final AntiLogoutManager logout;

    public PlayerQuitListener(Guilds guilds, Cuboids cuboids, AntiLogoutManager logout) {
        this.guilds = guilds;
        this.cuboids = cuboids;
        this.logout = logout;
    }

    @EventHandler
    public void handleEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        clearCuboidEnterNotification(player);
        if (isPlayerInGuild(player)) {
            Guild guild = guilds.getPlayerGuild(uuid);
            notifyGuildThatMemberLeft(player, guild);
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
    }

    private void notifyGuildThatMemberLeft(Player player, Guild guild) {
        for (UUID mem : guild.getMembers()) {
            OfflinePlayer om = Bukkit.getOfflinePlayer(mem);
            if (om.isOnline()) {
                om.getPlayer().sendMessage(MsgManager.get("guildmemberleft").replace("{PLAYER}", player.getDisplayName()));
            }
        }
    }

    private void clearCuboidEnterNotification(Player player) {
        cuboids.playersenteredcuboid.remove(player.getName());
    }

    private boolean isPlayerInGuild(Player player) {
        return guilds.hasGuild(player);
    }
}
