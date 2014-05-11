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

package pl.grzegorz2047.openguild2047.modules.spawn;

import org.bukkit.Location;

import pl.grzegorz2047.openguild2047.GenConf;

public class SpawnChecker {
    
    public static boolean isSpawn(Location location) {
        Location l = location;
        Location c1 = GenConf.spawnMax;
        Location c2 = GenConf.spawnMin;
        if(l.getWorld().getName().equals(c1.getWorld().getName()))
            if(l.getBlockX() > c2.getBlockX() && l.getBlockX() < c1.getBlockX())
                if(l.getBlockZ() > c2.getBlockZ() && l.getBlockZ() < c1.getBlockZ())
                    return true;
        return false;
    }
    
}
