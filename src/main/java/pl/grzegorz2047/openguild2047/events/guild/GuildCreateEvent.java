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

package pl.grzegorz2047.openguild2047.events.guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is calling when player creates a guild
 * This event is calling <b>BEFORE</b> the {@link GuildCreatedEvent} and you can <b>cancel</b> it!
 */
public class GuildCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final String tag;
    private final String description;
    private final Player leader;
    private final Location home;
    private boolean cancel;

    public GuildCreateEvent(@Nonnull String tag, @Nullable String description, @Nonnull Player leader, @Nonnull Location home) {
        this.tag = tag;
        this.description = description;
        this.leader = leader;
        this.home = home;
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
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Nonnull public String getTag() {
        return tag;
    }

    @Nullable public String getDescription() {
        return description;
    }

    @Nonnull public Player getLeader() {
        return leader;
    }

    @Nonnull public Location getHome() {
        return home;
    }
}
