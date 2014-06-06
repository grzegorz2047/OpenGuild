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
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.command.CommandManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenGuild {

    private static OpenGuildPlugin guild;

    @Nonnull public static Plugin getBukkit() {
        return guild.getBukkit();
    }

    @Nonnull public static CommandManager getCmdManager() {
        return guild.getCmdManager();
    }

    @Nullable public static CommandInfo getCommand(@Nonnull String name) {
        return guild.getCommand(name);
    }

    @Nonnull public static Set<String> getCommands() {
        return guild.getCommands();
    }

    @Nonnull public static Configuration getConfig() {
        return guild.getConfig();
    }

    @Nullable public static Guild getGuild(@Nonnull Location location) {
        return guild.getGuild(location);
    }

    @Nullable public static Guild getGuild(@Nonnull Player player) {
        return guild.getGuild(player);
    }

    @Nullable public static Guild getGuild(@Nonnull String name) {
        return guild.getGuild(name);
    }

    @Nullable public static Guild getGuild(@Nonnull User user) {
        return guild.getGuild(user);
    }

    @Nonnull public static GuildManager getGuildManager() {
        return guild.getGuildManager();
    }

    @Nonnull public static List<Guild> getGuilds() {
        return guild.getGuilds();
    }

    @Nonnull public static Messages getMessages() {
        return guild.getMessages();
    }
    
    @Nonnull public static ModuleManager getModules() {
        return guild.getModules();
    }

    @Nonnull public static OpenGuildPlugin getPlugin() {
        return guild.getPlugin();
    }

    @Nonnull public static PluginUpdater getUpdater() {
        return guild.getUpdater();
    }

    @Nullable public static User getUser(@Nonnull String name) {
        return guild.getUser(name);
    }

    @Nullable public static User getUser(@Nonnull Player player) {
        return guild.getUser(player);
    }

    @Nullable public static User getUser(@Nonnull UUID uuid) {
        return guild.getUser(uuid);
    }

    @Nonnull public static List<User> getUsers() {
        return guild.getUsers();
    }

    @Nonnull public static String getVersion() {
        return guild.getVersion();
    }

    public static void registerCommand(@Nonnull CommandInfo command) {
        guild.registerCommand(command);
    }

    @Deprecated
    public static void reload(@Nonnull CommandSender sender) {
        guild.reload(sender);
    }

    @Nonnull public static Guild[] sortGuilds() {
        return guild.sortGuilds();
    }

    @Deprecated
    public static void setOpenGuild(OpenGuildPlugin plugin) {
        guild = plugin;
    }

}
