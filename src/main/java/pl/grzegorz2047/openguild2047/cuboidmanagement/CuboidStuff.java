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

import java.util.Iterator;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.api.Cuboid;

/**
 *
 * @author Grzegorz
 */
public class CuboidStuff {
    
    public static boolean canMove(Player player, Location from, Location to){
        //To tylko jest proba, moze sie uda xd
        Iterator<Map.Entry<String, Cuboid>> it = Data.getInstance().cuboids.entrySet().iterator();
        if(Data.getInstance().isPlayerInGuild(player.getName())){
            String tag = Data.getInstance().getPlayersGuild(player.getName()).getTag();
            if(Data.getInstance().cuboids.get(tag).isinCuboid(to)){
                return true;
            }else{
                return !CuboidStuff.checkIfInAnyCuboid(it,to);
            }
            
        }else{
            return !CuboidStuff.checkIfInAnyCuboid(it,to);
        }
    }
    private static boolean checkIfInAnyCuboid(Iterator<Map.Entry<String, Cuboid>> it, Location to){
        while(it.hasNext()){
            if(it.next().getValue().isinCuboid(to)){
                return true;
            }
        }
        return false;
    }
    public static boolean checkIfCuboidFarForGuild(Location loc){
        Iterator<Map.Entry<String, Cuboid>> it = Data.getInstance().cuboids.entrySet().iterator();
        return !CuboidStuff.checkIfInAnyCuboid(it, loc);
    }
    
}
