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
package pl.grzegorz2047.openguild2047.cuboidmanagement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleCuboid;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class CuboidStuff {

    public static HashMap<String, String> playersenteredcuboid = new HashMap<String, String>();
    
    public static boolean canMove(Player player, Location from, Location to) {
        //To tylko jest proba, moze sie uda xd
        Iterator<Map.Entry<String, SimpleCuboid>> it = Data.getInstance().cuboids.entrySet().iterator();
        if(Data.getInstance().isPlayerInGuild(player.getUniqueId())) {
            String tag = Data.getInstance().getPlayersGuild(player.getUniqueId()).getTag();
            if(Data.getInstance().cuboids.get(tag).isinCuboid(to)) {
                return true;
            } else {
                return !CuboidStuff.checkIfInAnyCuboid(it, to);
            }
        } else {
            return !CuboidStuff.checkIfInAnyCuboid(it, to);
        }
    }
    public static void notifyGuildWhenPlMoves(Player player){
        Iterator<Map.Entry<String, SimpleCuboid>> it = Data.getInstance().cuboids.entrySet().iterator();
        if(Data.getInstance().isPlayerInGuild(player.getUniqueId())) {
            String tag = Data.getInstance().getPlayersGuild(player.getUniqueId()).getTag();
            String checked = CuboidStuff.checkIfInOtherCuboid(it, player, tag);
            if(checked != null) {
                
                if(CuboidStuff.playersenteredcuboid.containsKey(player.getName())){
                    String tagsaved = CuboidStuff.playersenteredcuboid.get(player.getName());
                    if(tagsaved.equals(checked)) {
                        return;
                    }
                } else {
                    CuboidStuff.playersenteredcuboid.put(player.getName(), checked);
                }
                SimpleGuild sg = Data.getInstance().guilds.get(checked);
                for(UUID memeber :sg.getMembers()){
                    Player p = Bukkit.getPlayer(memeber);
                    if(p != null) {
                        if(GenConf.cubNotifyMem) {
                            p.sendMessage(MsgManager.get("entercubmems")
                                    .replace("{PLAYER}", player.getName())
                                    .replace("{GUILD}", tag));
                        }
                        if(GenConf.cubNotifySound) {
                            p.playSound(p.getLocation(), GenConf.cubNotifySoundType, 10, 5);
                        }
                    }
                }
                player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", checked));
            } else {
                CuboidStuff.playersenteredcuboid.remove(player.getName());
            }
        } else {
            String tag = "";
            String checked = CuboidStuff.checkIfInOtherCuboid(it, player, tag);
            if(checked != null) {
                SimpleGuild sg = Data.getInstance().guilds.get(checked);
                if(CuboidStuff.playersenteredcuboid.containsKey(player.getName())) {
                    String tagsaved = CuboidStuff.playersenteredcuboid.get(player.getName());
                    if(tagsaved.equals(checked)) {
                        return;
                    }
                } else {
                    CuboidStuff.playersenteredcuboid.put(player.getName(), checked);
                }
                for(UUID memeber : sg.getMembers()) {
                    Player p = Bukkit.getPlayer(memeber);
                    if(p != null) {
                        if(GenConf.cubNotifyMem) {
                            p.sendMessage(MsgManager.get("entercubmems")
                                    .replace("{PLAYER}", player.getName())
                                    .replace("{GUILD}", tag));
                        }
                        if(GenConf.cubNotifySound) {
                            p.playSound(p.getLocation(), GenConf.cubNotifySoundType, 10, 5);
                        }
                    }
                }
                player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", checked));
            } else {
                CuboidStuff.playersenteredcuboid.remove(player.getName());
            }
        }

        
    }

    public static boolean checkIfInAnyCuboid(Iterator<Map.Entry<String, SimpleCuboid>> it, Location to) {
        while(it.hasNext()) {
            if(it.next().getValue().isinCuboid(to)) {
                return true;
            }
        }
        return false;
    }
    public static String checkIfInOtherCuboid(Iterator<Map.Entry<String, SimpleCuboid>> it, Player p, String tag) {
        while(it.hasNext()) {
            Map.Entry<String, SimpleCuboid> next = it.next();
            if(next.getKey().equals(tag)){
                return null;
            }
            if(next.getValue().isinCuboid(p.getLocation())) {
                return next.getValue().getOwner();
            }
        }
        return null;
    }
    private static boolean checkCuboidInAnyCuboid(Iterator<Map.Entry<String, SimpleCuboid>> it, Location loc) {
        while(it.hasNext()) {
            SimpleCuboid c = it.next().getValue();
            Location loc1 = c.getCenter();
            double distance = loc1.distance(loc);
            if (distance <= (c.getRadius() * 2))//Totalne uproszczenie, bo juz nie wyrabialem
            {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfCuboidFarForGuild(Location loc) {
        Iterator<Map.Entry<String, SimpleCuboid>> it = Data.getInstance().cuboids.entrySet().iterator();
        return !CuboidStuff.checkCuboidInAnyCuboid(it, loc);
    }

}
