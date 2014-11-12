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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class PlayerQuitListener implements Listener {
    
    private OpenGuild plugin;
    
    public PlayerQuitListener(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void handleEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        CuboidStuff.playersenteredcuboid.remove(player.getName());
        if(plugin.getGuildHelper().hasGuild(player)) {
            Guild guild = plugin.getGuildHelper().getPlayerGuild(uuid);
            for(UUID mem : guild.getMembers()) {
                OfflinePlayer om = plugin.getServer().getOfflinePlayer(mem);
                if(om.isOnline()) {
                    om.getPlayer().sendMessage(MsgManager.get("guildmemberleft").replace("{PLAYER}", player.getDisplayName()));
                }
            }
        }
    }
}
