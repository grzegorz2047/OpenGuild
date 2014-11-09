/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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
package pl.grzegorz2047.openguild2047;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import com.github.grzegorz2047.openguild.Guild;

public class GuildHelper {
    private Map<String, Guild> guilds = new HashMap<String, Guild>();
    private Map<UUID, Guild> players = new HashMap<UUID, Guild>();

    private Map<String, SimpleCuboid> cuboids = new HashMap<String, SimpleCuboid>();
    
    /**
     * @param uuid uuid of player, who guild should be returned
     * @return instance of specified player's guild, or null.
     */
    public Guild getPlayerGuild(UUID uuid) {
        if(this.hasGuild(uuid)) {
            return players.get(uuid);
        }
        
        return null;
    }
    
    /**
     * @param uuid UUID of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(UUID uuid) {
        return players.containsKey(uuid) && players.get(uuid) != null;
    }
    
    /**
     * @param player Player class instance, of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(Player player) {
        return hasGuild(player.getUniqueId());
    }
    
    /**
     * @param tag tag of guild, for which existance should map be searched.
     * @return boolean
     */
    public boolean doesGuildExists(String tag) {
        return guilds.containsKey(tag);
    }

    public void setGuilds(Map<String, Guild> guilds) {
        this.guilds = guilds;
    }
    
    /**
     * @return map which contains all guilds and their tags.
     */
    public Map<String, Guild> getGuilds() {
        return guilds;
    }
    
    public void setPlayers(Map<UUID, Guild> players) {
        this.players = players;
    }

    /**
     * @return map which contains all players, who are members of guilds.
     */
    public Map<UUID, Guild> getPlayers() {
        return players;
    }

    public Map<String, SimpleCuboid> getCuboids() {
        return cuboids;
    }

    public void setCuboids(Map<String, SimpleCuboid> cuboids) {
        this.cuboids = cuboids;
    }
}
