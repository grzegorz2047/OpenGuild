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
import pl.grzegorz2047.openguild2047.hooks.skript.expression.DescriptionExpression;

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
        getLogger().log(Level.INFO,
                "Hooked with Skript v" + plugin.getDescription().getVersion() + ".");
    }
    
    private void loadExpressions() {
        new DescriptionExpression();
    }
    
    private class SkriptEventRegistration {
        private final String guild = "[open]guild";
        
        private void defaults() {
            register("Guild create", "1.6.5", GuildCreateEvent.class, guild + " creat(e|ing)");
            register("Guild disband", "1.6.5", null, guild + " disband");
            register("Guild home", "1.6.5", null, guild + " home [teleport[ing]]");
            register("Guild player invite", "1.6.5", null, guild + " [player] invit(e|ation)");
            register("Guild player join", "1.6.5", null, guild + "[player] join[ing]");
            register("Guild player kick", "1.6.5", null, guild + " [player] kick[ing]");
            register("Guild leave", "1.6.5", null, guild + " [player] leave[ing]");
            register("Guild relation", "1.6.5", null, guild + " relation [chang(e|ing)]");
            register("Plugin reload", "1.6.5", null, guild + " reload[ing]");
            // TODO We need to add new Bukkit events first!
        }
        
        private void register(String name, String since, Class<? extends Event> event, String... patterns) {
            Skript.registerEvent(name, SimpleEvent.class, event, patterns).since(since);
        }
    }
}
