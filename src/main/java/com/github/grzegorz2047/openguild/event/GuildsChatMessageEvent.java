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

package com.github.grzegorz2047.openguild.event;

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.User;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildsChatMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private User user;
    private Guild guild;
    private String format;

    public GuildsChatMessageEvent(@Nonnull User author,
            @Nonnull Guild guild,
            @Nonnull String message) {
        this.cancelled = false;
        this.user = author;
        this.guild = guild;
        this.format = ChatColor.GRAY + "[Guild] " + ChatColor.BLUE + author + ChatColor.GRAY + ": " + ChatColor.WHITE + message;
    }

    @Nonnull public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    @Nonnull public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Nonnull public String getFormat() {
        return format;
    }

    @Nonnull public Guild getGuild() {
        return guild;
    }

    @Nonnull public User getUser() {
        return user;
    }

    public void setFormat(@Nonnull String format) {
        this.format = format;
    }

    public void setGuild(@Nonnull Guild guild) {
        this.guild = guild;
    }

    public void setUser(@Nonnull User user) {
        this.user = user;
    }

}
