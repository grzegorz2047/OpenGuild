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

import org.bukkit.ChatColor;

public class CommandInfo {
    
    private final String[] aliases;
    private final String command;
    private final CommandDescription description;
    private final Command executor;
    private final String permission;
    private final String usage;
    
    public CommandInfo(String[] aliases, String command, CommandDescription description, Command executor, String permission, String usage) {
        this.aliases = aliases;
        this.command = command.toLowerCase();
        this.description = description;
        this.executor = executor;
        this.permission = permission;
        this.usage = usage;
    }
    
    public String[] getAliases() {
        return aliases;
    }
    
    public String getName() {
        return command;
    }
    
    public CommandDescription getDescription() {
        if(description == null) {
            return description;
        } else {
            CommandDescription desc = new CommandDescription();
            desc.set("EN", ChatColor.ITALIC + "Not available yet" + ChatColor.RESET);
            return desc;
        }
    }
    
    public Command getExecutor() {
        return executor;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public String getUsage() {
        if(usage != null) {
            return "/g" + usage;
        } else {
            return "/g " + getName();
        }
    }
    
    public boolean hasExecutor() {
        return executor != null;
    }
    
    public boolean hasPermission() {
        return permission != null;
    }
    
}
