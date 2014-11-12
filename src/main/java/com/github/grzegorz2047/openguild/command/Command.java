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

package com.github.grzegorz2047.openguild.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.OpenGuild;

public abstract class Command {

    public abstract void execute(CommandSender sender, String[] args) throws CommandException;

    public OpenGuild getPlugin() {
        return OpenGuild.getInstance();
    }

    public String getTitle(String title) {
        String label = ChatColor.GRAY + "------------------" + ChatColor.DARK_GRAY + "[" + ChatColor.RESET;
        String label2 = ChatColor.DARK_GRAY + "]" +  ChatColor.GRAY + "------------------" + ChatColor.RESET;
        return label + " " + title + ChatColor.RESET + " " + label2;
    }

    public abstract int minArgs();

}
