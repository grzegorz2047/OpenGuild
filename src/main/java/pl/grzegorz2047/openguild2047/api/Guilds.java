/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.api;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleCuboid;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimpleLogger;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.utils.PastebinWriter;

/**
 * Glowna klasa API OpenGuild2047
 */
public class Guilds {

    @Nonnull public static Cuboid getCuboid() {
        Cuboid cuboid = new SimpleCuboid();
        return cuboid;
    }

    @Nullable public static Guild getGuild(@Nonnull Player player) throws NullPointerException {
        SimplePlayerGuild guildPlayer = Data.getInstance().guildsplayers.get(player.getUniqueId());
        SimpleGuild guild = Data.getInstance().guilds.get(guildPlayer.getClanTag());
        return guild;
    }

    @Nullable public static Guild getGuild(@Nonnull String tag) throws NullPointerException {
        Guild guild = Data.getInstance().guilds.get(tag.toLowerCase());
        return guild;
    }

    @Nonnull public static Logger getLogger() {
        Logger logger = new SimpleLogger();
        return logger;
    }

    @Nullable public static PlayerGuild getPlayer(@Nonnull UUID uuid) throws NullPointerException {
        PlayerGuild guild = Data.getInstance().guildsplayers.get(uuid);
        return guild;
    }

    public static void report(String error) {
        if(GenConf.SNOOPER) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            StringBuilder builder = new StringBuilder();
            builder.append("Generated " + format.format(new Date()));
            builder.append("The following error occurred: " + error);
            builder.append("-------------------------");
            builder.append("Enabled plugins: ");
            builder.append(Bukkit.getPluginManager().getPlugins().toString());
            builder.append("-------------------------");
            builder.append("Plugin version == " + OpenGuild.get().getDescription().getVersion());
            builder.append("Engine (Bukkit) version == " + Bukkit.getBukkitVersion() + " (" + Bukkit.getVersion() + ")");
            builder.append("System.getProperty(\"os.name\") == " + System.getProperty("os.name"));
            builder.append("System.getProperty(\"os.version\") == " + System.getProperty("os.version"));
            builder.append("System.getProperty(\"os.arch\") == " + System.getProperty("os.arch"));
            builder.append("System.getProperty(\"java.version\") == " + System.getProperty("java.version"));
            builder.append("System.getProperty(\"java.vendor\") == " + System.getProperty("java.vendor"));
            builder.append("System.getProperty(\"sun.arch.data.model\") == " + System.getProperty("sun.arch.data.model"));
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
