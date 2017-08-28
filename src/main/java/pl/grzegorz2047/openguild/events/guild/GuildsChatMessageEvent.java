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

package pl.grzegorz2047.openguild.events.guild;

import pl.grzegorz2047.openguild.guilds.Guild;

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
    private String author;
    private Guild guild;
    private String format;

    public GuildsChatMessageEvent(@Nonnull String author,
            @Nonnull Guild guild,
            @Nonnull String message) {
        this.cancelled = false;
        this.author = author;
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

    @Nonnull public String getAuthor() {
        return author;
    }

    public void setFormat(@Nonnull String format) {
        this.format = format;
    }

    public void setGuild(@Nonnull Guild guild) {
        this.guild = guild;
    }

    public void setAuthor(@Nonnull String author) {
        this.author = author;
    }

}
