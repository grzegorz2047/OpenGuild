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

package pl.grzegorz2047.openguild2047.commands.command;

import pl.grzegorz2047.openguild2047.BagOfEverything;
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
            desc.set(ChatColor.ITALIC + BagOfEverything.getMessages().getMessage("cmdnodesc") + ChatColor.RESET);
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
