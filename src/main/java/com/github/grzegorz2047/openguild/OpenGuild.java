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

package com.github.grzegorz2047.openguild;

import com.github.grzegorz2047.openguild.module.ModuleManager;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class OpenGuild {

    private static OpenGuildPlugin guild;

    public static Plugin getBukkit() {
        return guild.getBukkit();
    }

    public static Configuration getConfig() {
        return guild.getConfig();
    }

    public static Guild getGuild(Location location) {
        return guild.getGuild(location);
    }

    public static Guild getGuild(Player player) {
        return guild.getGuild(player);
    }

    public static Guild getGuild(String name) {
        return guild.getGuild(name);
    }

    public static Guild getGuild(User user) {
        return guild.getGuild(user);
    }

    public static GuildManager getGuildManager() {
        return guild.getGuildManager();
    }

    public static List<Guild> getGuilds() {
        return guild.getGuilds();
    }

    public static Messages getMessages() {
        return guild.getMessages();
    }
    
    public static ModuleManager getModules() {
        return guild.getModules();
    }

    public static OpenGuildPlugin getPlugin() {
        return guild.getPlugin();
    }

    public static PluginUpdater getUpdater() {
        return guild.getUpdater();
    }

    public static User getUser(String name) {
        return guild.getUser(name);
    }

    public static User getUser(Player player) {
        return guild.getUser(player);
    }

    public static User getUser(UUID uuid) {
        return guild.getUser(uuid);
    }

    public static List<User> getUsers() {
        return guild.getUsers();
    }

    public static String getVersion() {
        return guild.getVersion();
    }

    @Deprecated
    public static void reload(CommandSender sender) {
        guild.reload(sender);
    }

    public static Guild[] sortGuilds() {
        return guild.sortGuilds();
    }

    @Deprecated
    public static void setOpenGuild(OpenGuildPlugin plugin) {
        guild = plugin;
    }

}
