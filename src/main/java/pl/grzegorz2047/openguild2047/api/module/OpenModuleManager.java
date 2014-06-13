/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.grzegorz2047.openguild2047.api.module;

import com.github.grzegorz2047.openguild.event.ModuleLoadEvent;
import com.github.grzegorz2047.openguild.module.Module;
import com.github.grzegorz2047.openguild.module.ModuleInfo;
import com.github.grzegorz2047.openguild.module.ModuleLoadException;
import com.github.grzegorz2047.openguild.module.ModuleManager;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.modules.hardcore.ModuleHardcore;
import pl.grzegorz2047.openguild2047.modules.randomtp.ModuleRandomTP;
import pl.grzegorz2047.openguild2047.modules.spawn.ModuleSpawn;

public class OpenModuleManager implements ModuleManager {
    
    private HashMap<String, Module> modules;
    
    public OpenModuleManager() {
        defaultModules();
    }
    
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
    
    private void defaultModules() {
        modules = new HashMap<String, Module>();
        this.registerModule("hardcore", new ModuleHardcore());
        this.registerModule("random-tp", new ModuleRandomTP());
        this.registerModule("spawn", new ModuleSpawn());
    }
    
}
