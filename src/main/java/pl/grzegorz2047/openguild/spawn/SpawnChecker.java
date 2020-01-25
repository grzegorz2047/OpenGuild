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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnChecker {

    private final static int extra = 50;
    private static Location SPAWN_MIN_CORNER_LOCATION;
    private static Location SPAWN_MAX_CORNER_LOCATION;

    public static void loadSpawnCoords(List listMax, List listMin) {
        SPAWN_MIN_CORNER_LOCATION = new Location(Bukkit.getWorld((String) listMin.get(0)), (Integer) listMin.get(1), Integer.MIN_VALUE, (Integer) listMin.get(2));
        SPAWN_MAX_CORNER_LOCATION = new Location(Bukkit.getWorld((String) listMax.get(0)), (Integer) listMax.get(1), Integer.MAX_VALUE, (Integer) listMax.get(2));
    }

    public static boolean isSpawn(Location l) {
        String worldName = l.getWorld().getName();
        Location c1 = SPAWN_MAX_CORNER_LOCATION;
        Location c2 = SPAWN_MIN_CORNER_LOCATION;
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
        Location c1 = SPAWN_MAX_CORNER_LOCATION;
        Location c2 = SPAWN_MIN_CORNER_LOCATION;
        if (location.getWorld().getName().equals(c1.getWorld().getName())) {
            if (location.getBlockX() > c2.getBlockX() - extra && location.getBlockX() < c1.getBlockX() + extra) {
                return location.getBlockZ() > c2.getBlockZ() - extra && location.getBlockZ() < c1.getBlockZ() + extra;
            }
        }
        return false;
    }

    public static boolean cantDoItOnSpawn(Player player, Location playerLocation) {
        return SpawnChecker.isSpawn(playerLocation) && !player.hasPermission("openguild.spawn.bypass");
    }

}
