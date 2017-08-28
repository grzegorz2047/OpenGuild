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

package pl.grzegorz2047.openguild2047;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild2047.interfaces.Messages;
import pl.grzegorz2047.openguild2047.interfaces.OpenGuildPlugin;
import pl.grzegorz2047.openguild2047.interfaces.PluginUpdater;

import javax.annotation.Nonnull;
import java.util.Set;

public class BagOfEverything {

    private static OpenGuildPlugin guild;

    public static void execute(@Nonnull CommandSender sender, @Nonnull String cmd) {
        guild.execute(sender, cmd);
    }

    @Nonnull public static Plugin getBukkit() {
        return guild.getBukkit();
    }

    @Nonnull public static Set<String> getCommands() {
        return guild.getCommands();
    }

    @Nonnull public static Messages getMessages() {
        return guild.getMessages();
    }
    

    @Nonnull public static OpenGuildPlugin getPlugin() {
        return guild.getPlugin();
    }

    @Nonnull public static PluginUpdater getUpdater() {
        return guild.getUpdater();
    }


    @Nonnull public static String getVersion() {
        return guild.getVersion();
    }


    @Deprecated
    public static void reload(@Nonnull CommandSender sender) {
        guild.reload(sender);
    }


    @Deprecated
    public static void setOpenGuild(OpenGuildPlugin plugin) {
        guild = plugin;
    }

}
