/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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
