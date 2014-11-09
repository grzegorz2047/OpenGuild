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

import com.github.grzegorz2047.openguild.Cuboid;
import com.github.grzegorz2047.openguild.Guild;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.*;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class CuboidStuff {
    private static OpenGuild plugin;
    
    public CuboidStuff(OpenGuild plugin) {
        CuboidStuff.plugin = plugin;
    }

    public static HashMap<String, String> playersenteredcuboid = new HashMap<String, String>();
    
    public static boolean canMove(Player player, Location from, Location to) {
        Iterator<Map.Entry<String, Cuboid>> it = plugin.getGuildHelper().getCuboids().entrySet().iterator();
        if(plugin.getGuildHelper().hasGuild(player.getUniqueId())) {
            String tag = plugin.getGuildHelper().getPlayerGuild(player.getUniqueId()).getTag();
            if(plugin.getGuildHelper().getCuboids().get(tag).isinCuboid(to)) {
                return true;
            } else {
                return !CuboidStuff.checkIfInAnyCuboid(it, to);
            }
        } else {
            return !CuboidStuff.checkIfInAnyCuboid(it, to);
        }
    }
    public static void notifyGuildWhenPlMoves(Player player){
        Iterator<Map.Entry<String, Cuboid>> it = plugin.
                getGuildHelper().
                getCuboids().
                entrySet().
                iterator();

        if(plugin.getGuildHelper().hasGuild(player)) {
            String tag = plugin.getGuildHelper().getPlayerGuild(player.getUniqueId()).getTag();
            String check = checkIfInOtherCuboid(it, player, tag);

            if(check != null) {
                 Guild guild = plugin.getGuildHelper().getGuilds().get(check);
                if(CuboidStuff.playersenteredcuboid.containsKey(player.getName())) {
                    String tagSaved = CuboidStuff.playersenteredcuboid.get(player.getName());
                    if(tagSaved.equals(check)) {   
                        player.sendMessage("You entered on your own guild!");
                        return;
                    }
                } else {
                    CuboidStuff.playersenteredcuboid.put(player.getName(), check);
                }

               

                for(UUID mem : guild.getMembers()) {
                    OfflinePlayer op = plugin.getServer().getOfflinePlayer(mem);
                    if(op.isOnline()) {
                        if(GenConf.cubNotifyMem) {
                            op.getPlayer().sendMessage(MsgManager.get("entercubmems").replace("{PLAYER}", player.getName()).replace("{GUILD}", tag));
                        }

                        if(GenConf.cubNotifySound) {
                            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
                        }
                    }
                }
            } else {
                CuboidStuff.playersenteredcuboid.remove(player.getName());
            }
        } else {
            String tag = "";
            String checked = CuboidStuff.checkIfInOtherCuboid(it, player, tag);

            if(checked != null) {
                Guild guild = plugin.getGuildHelper().getGuilds().get(checked);
                if(CuboidStuff.playersenteredcuboid.containsKey(player.getName())) {
                    String tagSaved = CuboidStuff.playersenteredcuboid.get(player.getName());
                    if(tagSaved.equals(checked)) {
                        return;
                    }
                } else {
                    CuboidStuff.playersenteredcuboid.put(player.getName(), checked);
                }

                for(UUID mem : guild.getMembers()) {
                    OfflinePlayer op = plugin.getServer().getOfflinePlayer(mem);
                    if(op.isOnline()) {
                        if(GenConf.cubNotifyMem) {
                            op.getPlayer().sendMessage(MsgManager.get("entercubmemsnoguild").replace("{PLAYER}", player.getName()));
                        }

                        if(GenConf.cubNotifySound) {
                            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
                        }
                    }
                }

                player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", checked));
            } else {
                CuboidStuff.playersenteredcuboid.remove(player.getName());
            }
        }
    }

    public static boolean checkIfInAnyCuboid(Iterator<Map.Entry<String, Cuboid>> it, Location to) {
        while(it.hasNext()) {
            if(it.next().getValue().isinCuboid(to)) {
                return true;
            }
        }
        return false;
    }
    public static String checkIfInOtherCuboid(Iterator<Map.Entry<String, Cuboid>> it, Player p, String tag) {
        while(it.hasNext()) {
            Map.Entry<String, Cuboid> next = it.next();
            if(next.getKey().equals(tag)){
                return null;
            }
            if(next.getValue().isinCuboid(p.getLocation())) {
                return next.getValue().getOwner();
            }
        }
        return null;
    }
    private static boolean checkCuboidInAnyCuboid(Iterator<Map.Entry<String, Cuboid>> it, Location loc) {
        while(it.hasNext()) {
            Cuboid c = it.next().getValue();
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
        Iterator<Map.Entry<String, Cuboid>> it = plugin.getGuildHelper().getCuboids().entrySet().iterator();
        return !CuboidStuff.checkCuboidInAnyCuboid(it, loc);
    }

}
