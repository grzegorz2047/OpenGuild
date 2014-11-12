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

package com.github.grzegorz2047.openguild.event;

import com.github.grzegorz2047.openguild.OpenGuild;
import javax.annotation.Nonnull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageBroadcastEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Message type;
    private String message;

    public MessageBroadcastEvent(@Nonnull MessageBroadcastEvent.Message type) {
        this.type = type;
        this.message = OpenGuild.getMessages().getMessage("broadcast-" + type.name().toLowerCase().replace("_", ""));
    }

    public enum Message {
        JOIN, CREATE, CUBOID_EXPAND, DESCRIPTION, DISBAND, KICK, LEADER, LEAVE
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

    @Nonnull public String getMessage() {
        return message;
    }

    @Nonnull public Message getType() {
        return type;
    }

    public void setMessage(@Nonnull String message) {
        this.message = message;
    }

}
