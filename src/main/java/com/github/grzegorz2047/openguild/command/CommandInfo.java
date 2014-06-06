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

package com.github.grzegorz2047.openguild.command;

import com.github.grzegorz2047.openguild.OpenGuild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;

public class CommandInfo {

    private final String[] aliases;
    private final String command;
    private final CommandDescription description;
    private final Command executor;
    private final String permission;
    private final String usage;

    public CommandInfo(@Nullable String[] aliases,
            @Nonnull String command,
            @Nonnull CommandDescription description,
            @Nullable Command executor,
            @Nullable String permission,
            @Nullable String usage) {
        this.aliases = aliases;
        this.command = command.toLowerCase();
        this.description = description;
        this.executor = executor;
        this.permission = permission;
        this.usage = usage;
    }

    @Nullable public String[] getAliases() {
        return aliases;
    }

    @Nonnull public String getName() {
        return command;
    }

    @Nonnull public CommandDescription getDescription() {
        if(description != null) {
            return description;
        } else {
            CommandDescription desc = new CommandDescription();
            desc.set(ChatColor.ITALIC + OpenGuild.getMessages().getMessage("cmdnodesc") + ChatColor.RESET);
            return desc;
        }
    }

    @Nullable public Command getExecutor() {
        return executor;
    }

    @Nullable public String getPermission() {
        return permission;
    }

    @Nonnull public String getUsage() {
        String result = "";
        if(usage != null)
            result = " " + usage;
        return "/g " + getName() + result;
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    public boolean hasPermission() {
        return permission != null;
    }

}
