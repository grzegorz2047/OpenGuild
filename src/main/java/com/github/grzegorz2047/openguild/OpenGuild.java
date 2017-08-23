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

package com.github.grzegorz2047.openguild;

import com.github.grzegorz2047.openguild.interfaces.*;
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
import pl.grzegorz2047.openguild2047.guilds.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenGuild {

    private static OpenGuildPlugin guild;

    public static void execute(@Nonnull CommandSender sender, @Nonnull String cmd) {
        guild.execute(sender, cmd);
    }

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

    @Nonnull public static Logger getLogger() {
        return guild.getLogger();
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
