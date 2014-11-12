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

import com.github.grzegorz2047.openguild.Cuboid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import com.github.grzegorz2047.openguild.Guild;

public class GuildHelper {
    private Map<String, Guild> guilds = new HashMap<String, Guild>();
    private Map<UUID, Guild> players = new HashMap<UUID, Guild>();

    private Map<String, Cuboid> cuboids = new HashMap<String, Cuboid>();
    
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

    public Map<String, Cuboid> getCuboids() {
        return cuboids;
    }

    public void setCuboids(Map<String, Cuboid> cuboids) {
        this.cuboids = cuboids;
    }
}
