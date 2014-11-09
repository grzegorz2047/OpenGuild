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
