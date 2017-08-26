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
package pl.grzegorz2047.openguild2047.cuboidmanagement;

import pl.grzegorz2047.openguild2047.guilds.Guild;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Grzegorz
 */
public class Cuboids {
    private Map<String, Cuboid> cuboids = new HashMap<String, Cuboid>();


    public Cuboids() {

    }

    public HashMap<String, String> playersenteredcuboid = new HashMap<String, String>();

    public Cuboid previewCuboid(Location home, String tag, int size) {
        return new Cuboid(home, tag, size);
    }

    public void addCuboid(Location home, String tag, int size) {
        Cuboid cuboid = new Cuboid(home, tag, size);
        cuboids.put(tag, cuboid);
    }

    public void clearCuboidEnterNotification(Player player) {
        playersenteredcuboid.remove(player.getName());
    }

    public boolean hasRightToThisLocation(Player player, String guildTag, Location location) {
        Cuboid cuboidInLocation = this.getCuboidInLocation(location);
        if (cuboidInLocation != null) {
            //System.out.println("1 allowed");
            if (cuboidInLocation.getOwner().equals(guildTag)) {
                return true;
            } else if (!player.hasPermission("openguild.cuboid.bypassplace")) {
                return false;
            }
        }
        return true;
    }

    public boolean canMove(Guild playerGuild, Location from, Location to) {
        if (playerGuild != null) {
            String tag = playerGuild.getName();
            return cuboids.get(tag).isinCuboid(to) || getCuboidInLocation(to) == null;
        } else {
            return getCuboidInLocation(to) == null;
        }
    }


    public boolean isTheSame(String guildscuboidtag, String guildOnList) {
        return guildOnList.equals(guildscuboidtag);
    }

    public String getGuildTagInLocation(Location loc) {
        for (Cuboid cuboid : cuboids.values()) {
            if (cuboid.isinCuboid(loc)) {
                return cuboid.getOwner();
            }
        }
        return "";
    }

    public boolean wasPlayerPreviouslyOnCuboid(Player player) {
        return playersenteredcuboid.containsKey(player.getName());
    }


    public Cuboid getCuboidInLocation(Location to) {
        Collection<Cuboid> cuboids = this.cuboids.values();
        for (Cuboid cuboid : cuboids) {
            if (cuboid.isinCuboid(to)) {
                return cuboid;
            }
        }
        return null;
    }


    public boolean isCuboidInterferingWithOtherCuboid(Location loc) {
        for (Map.Entry<String, Cuboid> entry : cuboids.entrySet()) {
            Cuboid cuboid = entry.getValue();
            Location loc1 = cuboid.getCenter();
            Boolean withinCuboid = checkIfLocationWithinCuboid(cuboid, loc1, loc);
            if (withinCuboid) return true;
        }
        return false;
    }

    public boolean isCuboidInterferingWithOtherCuboid(Cuboid potential) {
        for (Map.Entry<String, Cuboid> entry : cuboids.entrySet()) {
            Cuboid cuboid = entry.getValue();
            Boolean withinCuboid = cuboid.isColliding(potential);
            if (withinCuboid) return true;
        }
        return false;
    }

    public boolean isInCuboid(Location location, String guildTag) {
        return cuboids.get(guildTag).isinCuboid(location);
    }

    private Boolean checkIfLocationWithinCuboid(Cuboid c, Location loc1, Location loc) {
        return isTheSame(loc1.getWorld().getName(), loc.getWorld().getName()) && c.isinCuboid(loc);
    }


    public Map<String, Cuboid> getCuboids() {
        return cuboids;
    }

    public void setCuboids(Map<String, Cuboid> cuboids) {
        this.cuboids = cuboids;
    }

    public void removeGuildCuboid(String tag) {
        cuboids.remove(tag);
    }
}
