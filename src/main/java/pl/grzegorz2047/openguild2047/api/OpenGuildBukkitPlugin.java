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

package pl.grzegorz2047.openguild2047.api;

import com.github.grzegorz2047.openguild.interfaces.Configuration;
import com.github.grzegorz2047.openguild.interfaces.GuildManager;
import com.github.grzegorz2047.openguild.interfaces.Logger;
import com.github.grzegorz2047.openguild.interfaces.Messages;
import com.github.grzegorz2047.openguild.interfaces.OpenGuildPlugin;
import com.github.grzegorz2047.openguild.interfaces.PluginUpdater;
import com.github.grzegorz2047.openguild.interfaces.User;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.command.CommandManager;
import com.github.grzegorz2047.openguild.event.CommandRegisterEvent;
import com.github.grzegorz2047.openguild.module.ModuleManager;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.OGLogger;
import pl.grzegorz2047.openguild2047.api.command.OpenCommandManager;
import pl.grzegorz2047.openguild2047.api.module.OpenModuleManager;
import pl.grzegorz2047.openguild2047.guilds.Guild;

public class OpenGuildBukkitPlugin implements OpenGuildPlugin {
    
    private static final Plugin bukkit = Bukkit.getPluginManager().getPlugin("OpenGuild2047");
    private static final CommandManager cmdManager = new OpenCommandManager();
    private static final HashMap<String, CommandInfo> commands = new HashMap<String, CommandInfo>();
    private static final Configuration configuration = new OpenConfiguration();
    private static final Logger logger = new OGLogger();
    private static final GuildManager manager = new OpenGuildManager();
    private static final Messages messages = new OpenMessages();
    private static final ModuleManager modules = new OpenModuleManager();
    private static final PluginUpdater updater = new OpenPluginUpdater();
    private static OpenGuildPlugin openGuild;
    
    public OpenGuildBukkitPlugin() {
        openGuild = this;
    }
    
    @Override
    public void execute(CommandSender sender, String cmd) {
        Bukkit.dispatchCommand(sender, "g " + cmd);
    }
    
    @Override
    public Plugin getBukkit() {
        return bukkit;
    }
    
    @Override
    public CommandManager getCmdManager() {
        return cmdManager;
    }
    
    @Override
    public CommandInfo getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    @Override
    public Set<String> getCommands() {
        return commands.keySet();
    }
    
    @Override
    public Configuration getConfig() {
        return configuration;
    }
    
    @Override
    public Guild getGuild(Location location) {
        return null; // TODO
    }
    
    @Override
    public Guild getGuild(Player player) {
        return null;
        // return getGuild(GuildHelper.getInstance().guildsplayers.get(player.getUniqueId()).getClanTag());
    }
    
    @Override
    public Guild getGuild(String name) {
        for(Guild guild : getGuilds()) {
            if(guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }
    
    @Override
    public Guild getGuild(User user) {
        return getGuild(user.getBukkit());
    }
    
    @Override
    public GuildManager getGuildManager() {
        return manager;
    }
    
    @Override
    public List<Guild> getGuilds() {
        return null; // TODO
    }
    
    @Override
    public Logger getLogger() {
        return logger;
    }
    
    @Override
    public Messages getMessages() {
        return messages;
    }
    
    @Override
    public ModuleManager getModules() {
        return modules;
    }
    
    @Override
    public OpenGuildPlugin getPlugin() {
        return openGuild;
    }
    
    @Override
    public PluginUpdater getUpdater() {
        return updater;
    }
    
    @Override
    public User getUser(String name) {
        for(User user : getUsers()) {
            if(user.getBukkit().getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }
    
    @Override
    public User getUser(Player player) {
        for(User user : getUsers()) {
            if(user.getBukkit().getUniqueId().equals(player.getUniqueId())) {
                return user;
            }
        }
        return null;
    }
    
    @Override
    public User getUser(UUID uuid) {
        for(User user : getUsers()) {
            if(user.getBukkit().getUniqueId().equals(uuid)) {
                return user;
            }
        }
        return null;
    }
    
    @Override
    public List<User> getUsers() {
        return null; // TODO
    }
    
    @Override
    public String getVersion() {
        return getBukkit().getDescription().getVersion();
    }
    
    @Override
    public void registerCommand(CommandInfo command) {
        Validate.notNull(command, "command can not be null");
        Validate.notNull(command.getExecutor(), "command executor can not be null");
        Validate.notNull(command.getName(), "command name can not be null");
        
        if(commands.containsKey(command.getName())) {
            throw new IllegalArgumentException("Command " + command.getName() + " is already listed");
        }
        
        boolean canCancel = true;
        String[] cancelList = new String[] {"help", "reload", "version"};
        
        for(String cancelCmd : cancelList) {
            if(command.getName().equals(cancelCmd)) canCancel = false;
        }
        
        CommandRegisterEvent event = new CommandRegisterEvent(command, canCancel);
        Bukkit.getPluginManager().callEvent(event);
        
        if(!event.isCancelled()) {
            commands.put(command.getName(), command);
            if(command.getAliases() != null) {
                for(String alias : command.getAliases()) {
                    if(!commands.containsKey(alias)) commands.put(alias, command);
                }
            }
        }
    }
    
    @Override
    public void reload(CommandSender sender) {
        // ReloadArg.execute(sender);
        /** @TODO */
    }
    
    @Override
    public Guild[] sortGuilds() {
        return (Guild[]) getGuilds().toArray();
    }
    
}
