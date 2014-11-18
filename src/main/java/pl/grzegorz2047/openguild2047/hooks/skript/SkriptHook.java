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
package pl.grzegorz2047.openguild2047.hooks.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.grzegorz2047.openguild.event.guild.GuildCreateEvent;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.hook.Hook;
import org.bukkit.event.Event;

/**
 *
 * @author Aleksander
 */
public class SkriptHook extends Hook {
    public SkriptHook() {
        super("Skript");
    }
    
    @Override
    public void enable(Plugin plugin) {
        Skript.registerAddon(OpenGuild.getInstance());
        new SkriptEventRegistration().defaults();
        loadExpressions();
        OpenGuild.getInstance().getOGLogger().log(Level.INFO,
                "Hooked with Skript v" + plugin.getDescription().getVersion() + ".");
    }
    
    private void loadExpressions() {
        
    }
    
    private class SkriptEventRegistration {
        private final String prefix = "[open]guild";
        
        private void defaults() {
            register("Guild create", "1.6.4", GuildCreateEvent.class, prefix + " creat(e|ing)");
            // TODO We need to add new Bukkit events first!
        }
        
        private void register(String name, String since, Class<? extends Event> event, String... patterns) {
            Skript.registerEvent(name, SimpleEvent.class, event, patterns).since(since);
        }
    }
}
