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

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.UUID;

public class PlayerMoveListener implements Listener {


    private final Cuboids cuboids;
    private final Guilds guilds;
    private final boolean NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID;
    private final boolean CANENTERAREA;
    private boolean notifyCuboidEnter;

    public PlayerMoveListener(Guilds guilds, Cuboids cuboids, FileConfiguration config) {
        this.cuboids = cuboids;
        this.guilds = guilds;
        notifyCuboidEnter = config.getBoolean("cuboid.notify-enter", true);
        NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID = config.getBoolean("cuboid.notify-enter-members", false);
        CANENTERAREA = config.getBoolean("cuboid.canenterarea", true);

    }

    @EventHandler
    public void handleEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();
        Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);

        Location playerLocation = player.getLocation();
        String guildscuboidtag = cuboids.getGuildTagInLocation(playerLocation);

        Guild currentCuboidGuild = guilds.getGuild(guildscuboidtag);
        Cuboid currentCuboid = cuboids.getCuboidByGuildName(guildscuboidtag);
        if (currentCuboid != null) {

            OpenGuild.getOGLogger().debug(String.valueOf(currentCuboid.getCuboidSize()));
        }//Bukkit.broadcastMessage("Gracz "+player.getName()+" jest na "+guildscuboidtag);

        String playerName = player.getName();
        boolean wasOnCuboidBefore = cuboids.playersenteredcuboid.containsKey(playerName);
        if (currentCuboidGuild == null && !wasOnCuboidBefore) {
            return;
        }
        if (wasOnCuboidBefore) {
            if (currentCuboidGuild == null) {
                processPlayerLeftCuboid(player);
                return;
            }
            String currentCuboidGuildName = currentCuboidGuild.getName();
            if (cuboids.playersenteredcuboid.get(playerName).equals(currentCuboidGuildName)) {
                return;
            }
        }
        if (notifyCuboidEnter) {
            player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", guildscuboidtag.toUpperCase()));

        }
        cuboids.playersenteredcuboid.put(playerName, guildscuboidtag);

        if (playerGuild != null && isAllyEntering(playerGuild, currentCuboidGuild)) {
            return;
        }
        if (NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID) {
            guilds.notifyMembersAboutSomeoneEnteringTheirCuboid(player, guildscuboidtag, playerGuild);
        }
        if (event.isCancelled() || CANENTERAREA || player.hasPermission("openguild.cuboid.bypassenterflag")) {
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
