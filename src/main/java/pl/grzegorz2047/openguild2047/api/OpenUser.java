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
import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.User;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OpenUser implements User {
    
    private UUID uuid;
    
    @Override
    public Player getBukkit() {
        return Bukkit.getPlayer(uuid);
    }
    
    @Override
    public int getDeads() {
        return 0; // TOOD
    }
    
    @Override
    public Guild getGuild() {
        return OpenGuild.getGuild(this);
    }
    
    @Override
    public double getKD() {
        return getKills() / getDeads();
    }
    
    @Override
    public int getKills() {
        return 0; // TODO
    }
    
    @Override
    public boolean isLeader() {
        if(getGuild() == null)
            return false;
        return getGuild().getLeader().equals(getBukkit().getUniqueId());
    }
    
}
