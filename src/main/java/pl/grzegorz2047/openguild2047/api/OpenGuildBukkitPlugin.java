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

package pl.grzegorz2047.openguild2047.api;

import com.github.grzegorz2047.openguild.Configuration;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.GuildManager;
import com.github.grzegorz2047.openguild.Messages;
import com.github.grzegorz2047.openguild.OpenGuildPlugin;
import com.github.grzegorz2047.openguild.PluginUpdater;
import com.github.grzegorz2047.openguild.User;
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
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.api.module.OpenModuleManager;
import pl.grzegorz2047.openguild2047.commands.arguments.ReloadArg;
import com.github.grzegorz2047.openguild.command.CommandInfo;

public class OpenGuildBukkitPlugin implements OpenGuildPlugin {
    
    private static final Plugin bukkit = Bukkit.getPluginManager().getPlugin("OpenGuild2047");
    private static final HashMap<String, CommandInfo> commands = new HashMap<String, CommandInfo>();
    private static final Configuration configuration = new OpenConfiguration();
    private static final GuildManager manager = new OpenGuildManager();
    private static final Messages messages = new OpenMessages();
    private static final ModuleManager modules = new OpenModuleManager();
    private static final PluginUpdater updater = new OpenPluginUpdater();
    private static OpenGuildPlugin openGuild;
    
    public OpenGuildBukkitPlugin() {
        openGuild = this;
    }
    
    @Override
    public Plugin getBukkit() {
        return bukkit;
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
        return getGuild(Data.getInstance().guildsplayers.get(player.getUniqueId()).getClanTag());
    }
    
    @Override
    public Guild getGuild(String name) {
        for(com.github.grzegorz2047.openguild.Guild guild : getGuilds()) {
            if(guild.getTag().equalsIgnoreCase(name)) {
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
        Validate.notNull(command.getExecutor(), "executor can not be null");
        Validate.notNull(command.getName(), "name can not be null");
        Validate.notNull(command, "command can not be null");
        
        if(commands.containsKey(command.getName())) {
            throw new IllegalArgumentException("Command " + command.getName() + " is already listed");
        }
        
        commands.put(command.getName(), command);
        for(String alias : command.getAliases()) {
            commands.put(alias, command);
        }
    }
    
    @Override
    public void reload(CommandSender sender) {
        ReloadArg.execute(sender);
    }
    
    @Override
    public Guild[] sortGuilds() {
        return (Guild[]) getGuilds().toArray();
    }
    
}
