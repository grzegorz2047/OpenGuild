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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.tntguildblocker.TntGuildBlocker;

public class TNTExplode implements Listener {

    private final DropFromBlocks drop;
    private final TntGuildBlocker tntGuildBlocker;
    private final Guilds guilds;
    private int defaultBlockTimeForGuildWhereTNTExploded = 30;
    public static boolean TNT_BLOCK_ENABLED = false;

    public TNTExplode(Guilds guilds, DropFromBlocks drop, TntGuildBlocker tntGuildBlocker, FileConfiguration config) {
        this.guilds = guilds;
        this.drop = drop;
        this.tntGuildBlocker = tntGuildBlocker;
        TNT_BLOCK_ENABLED = true;
        defaultBlockTimeForGuildWhereTNTExploded = config.getInt("listener.tnt-block-time", 30);

    }

    public void handle(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }

        Guild guild = guilds.getGuild(event.getBlock().getLocation());
        if (guild != null) {
            if (tntGuildBlocker.isGuildBlocked(guild.getName())) {
                player.sendMessage(MsgManager.get("tntex").replace("{SEC}", tntGuildBlocker.getTimeRemainingForBlockedGuild(guild.getName())));
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (SpawnChecker.isSpawn(event.getLocation())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntityType().equals(EntityType.PRIMED_TNT)) {
            Location location = event.getLocation();
            final Guild guild = guilds.getGuild(location);

            if (guild != null) {
                tntGuildBlocker.addGuildAsBlocked(guild.getName(), defaultBlockTimeForGuildWhereTNTExploded);
            }
        }
        if (GenConf.DROP_ENABLED) {
            List<Block> blocksToExplode = event.blockList();
            for (Block blockToExplode : blocksToExplode) {
                if (!drop.isNotUsedInDropFromBlocks(blockToExplode.getType())) {
                    blockToExplode.setType(Material.AIR);
                }
            }
        }
    }


}
