/*
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
package pl.grzegorz2047.openguild2047;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Grzegorz
 */
public class Data {

    //Temp data
    public HashMap<String, SimpleGuild> guilds;
    public HashMap<UUID, SimplePlayerGuild> guildsplayers;
    public List<String> ClansTag;
    static Data instance;
    //Test mojego pomysłu z cuboidami
    // <tag gildii,obiekt>
    public HashMap<String, SimpleCuboid> cuboids;

    public Data() {
        this.guilds = new HashMap<String, SimpleGuild>();
        this.ClansTag = new ArrayList<String>();
        this.guildsplayers = new HashMap<UUID, SimplePlayerGuild>();
        this.cuboids = new HashMap<String, SimpleCuboid>();
    }

    public boolean isPlayerInGuild(UUID playeruuid) {
        if(this.guildsplayers.containsKey(playeruuid)) {
            String tag = this.guildsplayers.get(playeruuid).getClanTag();
            if(Data.getInstance().guildExists(tag)) {
                SimpleGuild sg = this.guilds.get(tag);
                return sg.containsMember(playeruuid);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public SimpleGuild getPlayersGuild(UUID uuid) {
        if(this.isPlayerInGuild(uuid)) {
            return this.guilds.get(this.guildsplayers.get(uuid).getClanTag());
        }
        return null;
    }

    public boolean guildExists(String tag){
        if(Data.getInstance().guilds.containsKey(tag.toLowerCase())){
            return false;
        }
        return true;
    }
    public String getGuildChatTag(UUID uuid){
        SimplePlayerGuild spg = Data.getInstance().guildsplayers.get(uuid);
        if(Data.getInstance().guildsplayers.containsKey(uuid)) {
            if(spg.getClanTag().equals("") || spg.getClanTag().equals("null")) {
                return "";
            }else{
                return spg.getClanTag();
            }
        }
        return "";
    }
    
    public static Data getInstance() {
        return Data.instance;
    }

    public static void setDataInstance(Data pd) {
        Data.instance = pd;
    }

}
