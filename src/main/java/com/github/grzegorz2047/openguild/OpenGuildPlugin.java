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

public interface OpenGuildPlugin {

    void execute(@Nonnull CommandSender sender, @Nonnull String cmd);

    @Nonnull Plugin getBukkit();

    @Nonnull CommandManager getCmdManager();

    @Nonnull CommandInfo getCommand(@Nonnull String name);

    @Nonnull Set<String> getCommands();

    @Nonnull Configuration getConfig();

    @Nullable Guild getGuild(@Nonnull Location location);

    @Nullable Guild getGuild(@Nonnull Player player);

    @Nullable Guild getGuild(@Nonnull String name);

    @Nullable Guild getGuild(@Nonnull User user);

    @Nonnull GuildManager getGuildManager();

    @Nonnull List<Guild> getGuilds();

    @Nonnull Logger getLogger();

    @Nonnull Messages getMessages();

    @Nonnull ModuleManager getModules();

    @Nonnull OpenGuildPlugin getPlugin();

    @Nonnull PluginUpdater getUpdater();

    @Nullable User getUser(@Nonnull String name);

    @Nullable User getUser(@Nonnull Player player);

    @Nullable User getUser(@Nonnull UUID uuid);

    @Nonnull List<User> getUsers();

    @Nonnull String getVersion();

    void registerCommand(@Nonnull CommandInfo command);

    @Deprecated
    void reload(@Nonnull CommandSender sender);

    @Nonnull Guild[] sortGuilds();

}
