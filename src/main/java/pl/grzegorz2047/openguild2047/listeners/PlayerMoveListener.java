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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class PlayerMoveListener implements Listener {


    private final Cuboids cuboids;
    private final Guilds guilds;

    public PlayerMoveListener(Guilds guilds, Cuboids cuboids) {
        this.cuboids = cuboids;
        this.guilds = guilds;
    }

    @EventHandler
    public void handleEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Guild playerGuild = guilds.getPlayerGuild(player.getUniqueId());

        String guildscuboidtag = cuboids.getGuildTagInLocation(player.getLocation());
        Guild currentCuboidGuild = guilds.getGuild(guildscuboidtag);

        //Bukkit.broadcastMessage("Gracz "+player.getName()+" jest na "+guildscuboidtag);

        boolean wasOnCuboidBefore = cuboids.playersenteredcuboid.containsKey(player.getName());
        if (currentCuboidGuild == null && !wasOnCuboidBefore) {
            return;
        }
        if(wasOnCuboidBefore) {
            if(currentCuboidGuild == null) {
                processPlayerLeftCuboid(player);
                return;
            }
            if (cuboids.playersenteredcuboid.get(player.getName()).equals(currentCuboidGuild.getName())) {
                return;
            }
        }
        player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", guildscuboidtag.toUpperCase()));
        cuboids.playersenteredcuboid.put(player.getName(), guildscuboidtag);

        if (playerGuild != null && isAllyEntering(playerGuild, currentCuboidGuild)) {
            return;
        }
        if (GenConf.cubNotifyMem) {
            guilds.notifyMembersAboutSomeoneEnteringTheirCuboid(player, guildscuboidtag, playerGuild);
        }
        if (event.isCancelled() || GenConf.CANENTERAREA || player.hasPermission("openguild.cuboid.bypassenterflag")) {
            return;
        }
        if (!cuboids.canMove(playerGuild, event.getFrom(), event.getTo())) {
            event.setTo(event.getFrom());
        }

    }

    private boolean isAllyEntering(Guild playerGuild, Guild cuboidowner) {
        return cuboidowner.equals(playerGuild) || cuboidowner.isAlly(playerGuild);
    }


    private void processPlayerLeftCuboid(Player player) {
        String tagSaved = cuboids.playersenteredcuboid.get(player.getName());
        player.sendMessage(MsgManager.get("leavecubpl").replace("{GUILD}", tagSaved.toUpperCase()));
        cuboids.playersenteredcuboid.remove(player.getName());
    }

}
