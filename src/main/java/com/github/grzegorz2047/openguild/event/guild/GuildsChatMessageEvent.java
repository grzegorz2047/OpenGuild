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

package com.github.grzegorz2047.openguild.event.guild;

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.User;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @deprecated NOT WORKING YET!
 */
@Deprecated
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
