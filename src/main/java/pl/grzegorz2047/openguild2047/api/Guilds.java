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
package pl.grzegorz2047.openguild2047.api;

import com.github.grzegorz2047.openguild.Guild;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Cuboid;
import pl.grzegorz2047.openguild2047.utils.PastebinWriter;

/**
 * Glowna klasa API OpenGuild2047
 */
public class Guilds {

    @Nonnull public static Cuboid getCuboid() {
        Cuboid cuboid = new Cuboid();
        return cuboid;
    }

    @Nullable public static Guild getGuild(@Nonnull Player player) throws NullPointerException {
        //SimplePlayerGuild guildPlayer = GuildHelper.getInstance().guildsplayers.get(player.getUniqueId());
        //SimpleGuild guild = GuildHelper.getInstance().guilds.get(guildPlayer.getClanTag());
        return null;
    }

    @Nullable public static Guild getGuild(@Nonnull String tag) throws NullPointerException {
        //Guild guild = GuildHelper.getInstance().guilds.get(tag.toLowerCase());
        return null;
    }

    @Nonnull public static Logger getLogger() {
        return OpenGuild.getInstance().getOGLogger();
    }

    @Nullable public static PlayerGuild getPlayer(@Nonnull UUID uuid) throws NullPointerException {
        //PlayerGuild guild = GuildHelper.getInstance().guildsplayers.get(uuid);
        return null;
    }

    public static void report(String error) {
        if(GenConf.SNOOPER) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            StringBuilder builder = new StringBuilder();
            builder.append("Generated ").append(format.format(new Date()));
            builder.append("The following error occurred: ").append(error);
            builder.append("-------------------------");
            builder.append("Enabled plugins: ");
            builder.append(Bukkit.getPluginManager().getPlugins().toString());
            builder.append("-------------------------");
            builder.append("Plugin version == ").append(OpenGuild.getInstance().getDescription().getVersion());
            builder.append("Engine (Bukkit) version == ").append(Bukkit.getBukkitVersion()).append(" (").append(Bukkit.getVersion()).append(")");
            builder.append("System.getProperty(\"os.name\") == ").append(System.getProperty("os.name"));
            builder.append("System.getProperty(\"os.version\") == ").append(System.getProperty("os.version"));
            builder.append("System.getProperty(\"os.arch\") == ").append(System.getProperty("os.arch"));
            builder.append("System.getProperty(\"java.version\") == ").append(System.getProperty("java.version"));
            builder.append("System.getProperty(\"java.vendor\") == ").append(System.getProperty("java.vendor"));
            builder.append("System.getProperty(\"sun.arch.data.model\") == ").append(System.getProperty("sun.arch.data.model"));
            builder.append("-------------------------");
            PastebinWriter.paste(builder.toString(), new PastebinWriter.Callback() {

                @Override
                public void success(URL url) {
                    getLogger().info("Error has been sent to " + url.toString());
                    getLogger().info("Please send it (" + url.toString() + ") to https://github.com/grzegorz2047/OpenGuild2047/issues");
                }

                @Override
                public void error(String err) {
                    getLogger().warning("Could not sent error to Pastebin: " + err);
                }
            });
        }
    }

}
