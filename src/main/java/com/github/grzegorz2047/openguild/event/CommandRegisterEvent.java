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

import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.module.Module;
import javax.annotation.Nonnull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandRegisterEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final boolean canCancel;
    private boolean cancel;
    private final CommandInfo command;

    public CommandRegisterEvent(@Nonnull CommandInfo command, boolean canCancel) {
        this.canCancel = canCancel;
        this.command = command;
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
        if(!canCancel)
            return false;
        else return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if(canCancel) this.cancel = cancel;
    }

    public boolean canCancel() {
        return canCancel;
    }

    @Nonnull public CommandInfo getCommand() {
        return command;
    }

}
