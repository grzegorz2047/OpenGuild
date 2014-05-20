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
import com.github.grzegorz2047.openguild.Messages;
import com.github.grzegorz2047.openguild.OpenGuildPlugin;
import com.github.grzegorz2047.openguild.PluginUpdater;
import com.github.grzegorz2047.openguild.User;
import com.github.grzegorz2047.openguild.module.ModuleManager;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.api.module.OpenModuleManager;
import pl.grzegorz2047.openguild2047.commands.arguments.ReloadArg;

public class OpenGuildBukkitPlugin implements OpenGuildPlugin {
    
    private static Configuration configuration = new OpenConfiguration();
    private static Messages messages = new OpenMessages();
    private static ModuleManager modules = new OpenModuleManager();
    private static OpenGuildPlugin openGuild;
    private static PluginUpdater updater = new OpenPluginUpdater();
    
    public OpenGuildBukkitPlugin() {
        openGuild = this;
    }
    
    @Override
    public Plugin getBukkit() {
        return Bukkit.getPluginManager().getPlugin("OpenGuild2047");
    }
    
    @Override
    public Configuration getConfig() {
        return configuration;
    }
    
    @Override
    public Guild getGuild(Location location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Guild getGuild(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Guild getGuild(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Guild getGuild(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Guild> getGuilds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public User getUser(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public User getUser(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<User> getUsers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getVersion() {
        return getBukkit().getDescription().getVersion();
    }
    
    @Override
    public void reload(CommandSender sender) {
        ReloadArg.execute(sender);
    }
    
    @Override
    public Guild[] sortGuilds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
