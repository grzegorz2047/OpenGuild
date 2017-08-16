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

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.OpenGuildPlugin;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.EnhancedRunnable;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.modules.spawn.SpawnChecker;

public class TNTExplode implements Listener {

    private final OpenGuildPlugin plugin;
    private final Map<String, Integer> blockedGuilds = new HashMap<String, Integer>();
    private final DropFromBlocks drop;

    public TNTExplode(OpenGuildPlugin plugin, DropFromBlocks drop) {
        this.plugin = plugin;
        this.drop = drop;
    }

    public void handle(BlockPlaceEvent event) {
        if (GenConf.enableTNTExplodeListener) {
            Player player = event.getPlayer();

            if (player.hasPermission("openguild.cuboid.bypassplace")) {
                return;
            }

            Guild guild = plugin.getGuild(event.getBlock().getLocation());
            if (guild != null) {
                if (blockedGuilds.containsKey(guild.getTag())) {
                    player.sendMessage(MsgManager.get("tntex").replace("{SEC}", String.valueOf(blockedGuilds.get(guild.getTag()))));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (SpawnChecker.isSpawn(event.getLocation())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntityType().equals(EntityType.PRIMED_TNT) && GenConf.enableTNTExplodeListener) {
            Location location = event.getLocation();
            final Guild guild = plugin.getGuild(location);

            if (guild != null) {
                if (!blockedGuilds.containsKey(guild.getTag())) {
                    EnhancedRunnable.startTask(plugin.getBukkit(), new EnhancedRunnable() {
                        /**
                         * Time left to 'free' guild.
                         */
                        private int blockTime = GenConf.defaultTNTBlockTime;

                        @Override
                        public void run() {
                            blockTime = blockedGuilds.get(guild.getTag());
                            if (blockTime == 0) {
                                blockedGuilds.remove(guild.getTag());
                                this.stopTask();
                                return;
                            }

                            blockedGuilds.put(guild.getTag(), blockTime--);
                        }
                    }, 5L, 20L);
                }

                blockedGuilds.put(guild.getTag(), 30);
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
