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

    /**
     * Zdobadz gildie
     *
     * @param player Member gildii
     * @return Gildia
     * @throws NullPointerException jezeli player nie jest w zadnej gildii
     */
    @Nullable public static Guild getGuild(@Nonnull Player player) throws NullPointerException {
        SimplePlayerGuild guildPlayer = Data.getInstance().guildsplayers.get(player.getUniqueId());
        SimpleGuild guild = Data.getInstance().guilds.get(guildPlayer.getClanTag());
        return guild;
    }

    /**
     * Zdobadz gildie
     *
     * @param tag Tag gildii
     * @return Gildia
     * @throws NullPointerException jezeli wskazana gildia nie istnieje
     */
    @Nullable public static Guild getGuild(@Nonnull String tag) throws NullPointerException {
        Guild guild = Data.getInstance().guilds.get(tag);
        return guild;
    }

    /**
     * Zdobadz Logger
     *
     * @return Logger
     */
    @Nonnull public static Logger getLogger() {
        Logger logger = new SimpleLogger();
        return logger;
    }

    /**
     * Zdobadz gildie gracz
     *
     * @param uuid
     * @return Gildia gracza
     * @throws NullPointerException jezeli player nie jest w zadnej gildii
     */
    @Nullable public static PlayerGuild getPlayer(@Nonnull UUID uuid) throws NullPointerException {
        PlayerGuild guild = Data.getInstance().guildsplayers.get(uuid);
        return guild;
    }

    public static void report(String error) {
        if(GenConf.SNOOPER) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            StringBuilder builder = new StringBuilder();
            builder.append("Wygenerowano " + format.format(new Date()));
            builder.append("Wystapil nastepujacy blad " + error);
            builder.append("-------------------------");
            builder.append("Uruchomione pluginy");
            builder.append(Bukkit.getPluginManager().getPlugins().toString());
            builder.append("-------------------------");
            builder.append("Wersja pluginu == " + OpenGuild.get().getDescription().getVersion());
            builder.append("Wersja silnika == " + Bukkit.getBukkitVersion() + " (" + Bukkit.getVersion() + ")");
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
                    getLogger().info("Pomyslnie wyslano blad pod adresem " + url.toString());
                    getLogger().info("Prosimy wyslac link (" + url.toString() + ") na https://github.com/grzegorz2047/OpenGuild2047/issues");
                }

                @Override
                public void error(String err) {
                    getLogger().warning("Nie udalo sie wyslac bledu z powodu: " + err);
                }
            });
        }
    }

}
