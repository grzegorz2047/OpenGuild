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
