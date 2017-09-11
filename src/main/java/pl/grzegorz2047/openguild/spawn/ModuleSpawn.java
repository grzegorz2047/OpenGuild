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

package pl.grzegorz2047.openguild.spawn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ModuleSpawn {

    private boolean BLOCK_GUILD_CREATION_ON_SPAWN;


    public void enable(JavaPlugin plugin) {
        BLOCK_GUILD_CREATION_ON_SPAWN = plugin.getConfig().getBoolean("spawn.block-guild-creating", true);
        if (BLOCK_GUILD_CREATION_ON_SPAWN) {
            Bukkit.getPluginManager().registerEvents(new SpawnListeners(plugin.getConfig()), plugin);
        }
    }

}
