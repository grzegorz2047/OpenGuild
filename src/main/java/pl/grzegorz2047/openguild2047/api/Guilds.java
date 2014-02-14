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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimpleLogger;

/**
 * Glowna klasa API OpenGuild2047
 */
public class Guilds {
    
	/**
	 * Zdobadz gildie
	 * @param player Member gildii
	 * @return Gildia
	 * @throws NullPointerException jezeli player nie jest w zadnej gildii
	 */
	@Nullable public static Guild getGuild(@Nonnull Player player) throws NullPointerException {
	    SimpleGuild guild = Data.getInstance().guilds.get(player.getName());
	    return getGuild(guild.getTag());
	}
	
    /**
     * Zdobadz gildie
     * @param tag Tag gildii
     * @return Gildia
     * @throws NullPointerException jezeli wskazana gildia nie istnieje
     */
    @Nullable public static Guild getGuild(@Nonnull String tag) throws NullPointerException {
        Guild guild = new SimpleGuild(tag);
        return guild;
    }
    
    /**
     * Zdobadz Logger
     * @return Logger
     */
    @Nonnull public static Logger getLogger() {
        Logger logger = new SimpleLogger();
        return logger;
    }
    
    /**
     * Zdobadz gildie gracz
     * @param name Nick gracza
     * @return Gildia gracza
     * @throws NullPointerException jezeli player nie jest w zadnej gildii
     */
    @Nullable public static PlayerGuild getPlayer(@Nonnull String name) throws NullPointerException {
        PlayerGuild guild = Data.getInstance().guildsplayers.get(name);
        return guild;
    }
	
}
