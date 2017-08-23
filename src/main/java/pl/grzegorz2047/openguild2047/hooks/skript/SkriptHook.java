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
import pl.grzegorz2047.openguild2047.events.misc.ModuleLoadEvent;
import pl.grzegorz2047.openguild2047.events.misc.OpenGuildReloadEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildCreateEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildDescriptionChangeEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildDisbandEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildHomeTeleportEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildInvitationEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildJoinEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildKickEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildLeaveEvent;
import pl.grzegorz2047.openguild2047.events.guild.GuildRelationEvent;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.addons.Hook;
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
            register("Guild created", "1.6.5", GuildCreatedEvent.class, guild + " created");
            register("Guild description change", "1.6.5", GuildDescriptionChangeEvent.class, guild + " desc[ription] [change]");
            register("Guild disband", "1.6.5", GuildDisbandEvent.class, guild + " (disband[(s|ing)]|clos(e|ing))");
            register("Guild home teleport", "1.6.5", GuildHomeTeleportEvent.class, guild + " home teleport[ing]");
            register("Guild player invite", "1.6.5", GuildInvitationEvent.class, guild + " [player] invit(e|ation)");
            register("Guild player join", "1.6.5", GuildJoinEvent.class, guild + "[player] join[ing]");
            register("Guild player kick", "1.6.5", GuildKickEvent.class, guild + " [player] kick[ing]");
            register("Guild leave", "1.6.5", GuildLeaveEvent.class, guild + " [player] leave[ing]");
            register("Guild relation", "1.6.5", GuildRelationEvent.class, guild + " relation [status] [(chang(e|ing)]");
            register("OpenGuild's module load", "1.6.5", ModuleLoadEvent.class, "openguild[[']s] module[s] load[ing][s]");
            register("OpenGuild plugin reload", "1.6.5", OpenGuildReloadEvent.class, "openguild [plugin] reload[ing]");
            // TODO We need to add new Bukkit events first!
        }
        
        private void register(String name, String since, Class<? extends Event> event, String... patterns) {
            Skript.registerEvent(name, SimpleEvent.class, event, patterns).since(since);
        }
    }
}
