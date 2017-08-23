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

package pl.grzegorz2047.openguild2047.api.module;

import pl.grzegorz2047.openguild2047.events.misc.ModuleLoadEvent;
import pl.grzegorz2047.openguild2047.modules.module.Module;
import pl.grzegorz2047.openguild2047.modules.module.ModuleInfo;
import pl.grzegorz2047.openguild2047.modules.module.ModuleLoadException;
import pl.grzegorz2047.openguild2047.modules.module.ModuleManager;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.modules.hardcore.ModuleHardcore;
import pl.grzegorz2047.openguild2047.modules.randomtp.ModuleRandomTP;
import pl.grzegorz2047.openguild2047.modules.spawn.ModuleSpawn;

public class OpenModuleManager implements ModuleManager {
    
    private HashMap<String, Module> modules;
    
    @Override
    public Module getModule(String id) {
        return modules.get(id.toLowerCase());
    }
    
    @Override
    public Set<String> getModules() {
        return modules.keySet();
    }
    
    @Override
    public boolean registerModule(String id, Module module) {
        id = id.toLowerCase();
        if(modules.containsKey(id)) {
            Guilds.getLogger().severe("Could not load module ID '" + id + "'! This modules name already exists!");
            return false;
        } else {
            Guilds.getLogger().info("Enabling module '" + id + "'...");
            try {
                ModuleInfo info = module.module();
                if(info == null) {
                    throw new ModuleLoadException("ModuleInfo can not be null");
                }
                
                ModuleLoadEvent event = new ModuleLoadEvent(module);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()) {
                    Guilds.getLogger().info("Loading cancelled by ModuleLoadEvent");
                    return false;
                }
                
                module.enable(id);
                
                modules.put(id, module);
                Guilds.getLogger().info("Module " + info.getName() + " (as " + id + ") v" + info.getVersion() + " has been enabled.");
                return true;
            } catch(ModuleLoadException ex) {
                throw new ModuleLoadException("Could not load module '" + id + "': " + ex.getMessage());
            }
        }
    }
    
    public void defaultModules() {
        modules = new HashMap<String, Module>();
        this.registerModule("random-tp", new ModuleRandomTP());
        this.registerModule("spawn", new ModuleSpawn());
        this.registerModule("Hardcore", new ModuleHardcore());
    }
    
}
