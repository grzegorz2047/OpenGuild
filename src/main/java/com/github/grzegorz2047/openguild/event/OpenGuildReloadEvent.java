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

import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenGuildReloadEvent extends Event {
    public enum Status {
        BEGINING, ENDING;
    }
    
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final Status status;
    
    public OpenGuildReloadEvent(@Nonnull CommandSender sender, @Nonnull Status status) {
        this.sender = sender;
        this.status = status;
    }
    
    @Override
    @Nonnull public HandlerList getHandlers() {
        return handlers;
    }
    
    @Nonnull public CommandSender getSender() {
        return sender;
    }
    
    @Nonnull public Status getStatus() {
        return status;
    }
}
