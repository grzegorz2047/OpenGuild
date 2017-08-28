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

package pl.grzegorz2047.openguild.spawn;

import org.bukkit.Location;
import pl.grzegorz2047.openguild.configuration.GenConf;

public class SpawnChecker {

    private static int extra = 50;

    public static boolean isSpawn(Location l) {
        String worldName = l.getWorld().getName();
        Location c1 = GenConf.spawnMax;
        Location c2 = GenConf.spawnMin;
        return worldName.equals(c1.getWorld().getName()) &&
                isWithinXCords(l, c1, c2) &&
                isWithinZCords(l, c1, c2);
    }

    private static boolean isWithinZCords(Location l, Location c1, Location c2) {
        return l.getBlockZ() > Math.min(c1.getBlockZ(), c2.getBlockZ()) && l.getBlockZ() < Math.max(c1.getBlockZ(), c2.getBlockZ());
    }

    private static boolean isWithinXCords(Location l, Location c1, Location c2) {
        return l.getBlockX() > Math.min(c1.getBlockX(), c2.getBlockX()) && l.getBlockX() < Math.max(c1.getBlockX(), c2.getBlockX());
    }

    @Deprecated
    public static boolean isSpawnExtra(Location location) {
        Location l = location;
        Location c1 = GenConf.spawnMax;
        Location c2 = GenConf.spawnMin;
        if (l.getWorld().getName().equals(c1.getWorld().getName())) {
            if (l.getBlockX() > c2.getBlockX() - extra && l.getBlockX() < c1.getBlockX() + extra) {
                if (l.getBlockZ() > c2.getBlockZ() - extra && l.getBlockZ() < c1.getBlockZ() + extra) {
                    return true;
                }
            }
        }
        return false;
    }

}
