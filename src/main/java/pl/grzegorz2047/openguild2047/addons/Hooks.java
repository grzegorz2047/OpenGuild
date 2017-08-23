/*
 * Copyright 2014 Aleksander.
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
package pl.grzegorz2047.openguild2047.addons;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.addons.hooks.skript.SkriptHook;

/**
 *
 * @author Aleksander
 */
public class Hooks {
    private static final List<Hook> hooks = new ArrayList<Hook>();
    
    public static void addHook(Hook hook, JavaPlugin javaPlugin) {
        Plugin plugin = hook.getBukkitPlugin();
        if(hook.isEnabled()) {
            try {
                hook.enable(plugin, javaPlugin);
            } catch(Throwable ex) {
                OpenGuild.getOGLogger().log(Level.SEVERE,
                        "Could not load " + plugin.getName() + " hook. Error was " + ex.getMessage());
                ex.printStackTrace();
            }
            
            hooks.add(hook);
        }
    }
    
    public static List<Hook> getEnabledHooks() {
        return hooks;
    }
    
    public static void registerDefaults(JavaPlugin javaPlugin) {
        addHook(new SkriptHook(), javaPlugin); // Skript by Njol - http://dev.bukkit.org/bukkit-plugins/skript/
    }
}
