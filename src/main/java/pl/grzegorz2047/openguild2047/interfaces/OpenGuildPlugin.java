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

package pl.grzegorz2047.openguild2047.interfaces;


import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface OpenGuildPlugin {

    void execute(@Nonnull CommandSender sender, @Nonnull String cmd);

    @Nonnull
    Plugin getBukkit();

    @Nonnull
    Set<String> getCommands();


    @Nonnull
    Messages getMessages();

    @Nonnull
    OpenGuildPlugin getPlugin();

    @Nonnull
    PluginUpdater getUpdater();


    @Nonnull
    String getVersion();


    @Deprecated
    void reload(@Nonnull CommandSender sender);


}
