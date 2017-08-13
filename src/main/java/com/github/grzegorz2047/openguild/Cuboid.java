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
package com.github.grzegorz2047.openguild;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import pl.grzegorz2047.openguild2047.GenConf;

import java.util.ArrayList;

/**
 * @author Grzegorz
 */
public class Cuboid {

    private Location center;
    private String owner;
    private Location min;
    private Location max;
    private int cuboidSize;

    public Cuboid(Location center, String owner, int size) {
        this.center = center;
        this.owner = owner;
        this.cuboidSize = size;
        this.min = new Location(center.getWorld(), center.getBlockX() - size, Integer.MIN_VALUE, center.getBlockZ() - size);
        this.max = new Location(center.getWorld(), center.getBlockX() + size, Integer.MAX_VALUE, center.getBlockZ() + size);
    }

    public Location getCenter() {
        return this.center;
    }

    public boolean isinCuboid(Location loc) {
        Vector v = loc.toVector();
        return v.isInAABB(this.getMin().toVector(), this.getMax().toVector());
    }

    public String getOwner() {
        return this.owner;
    }

    public Location getMin() {
        return this.min;
    }

    public Location getMax() {
        return this.max;
    }

    public int getCuboidSize() {
        return cuboidSize;
    }

    public String getWorldName() {
        return center.getWorld().getName();
    }

    public boolean isColliding(Cuboid cuboid) {
        if (!cuboid.getWorldName().equals(getWorldName())) {
            return false;
        }

        int localMinX = Math.min(this.min.getBlockX(), max.getBlockX());
        int localMaxX = Math.max(this.min.getBlockX(), max.getBlockX());
        int localMinZ = Math.min(this.min.getBlockZ(), this.max.getBlockZ());
        int localMaxZ = Math.max(this.min.getBlockZ(), this.max.getBlockZ());

        int foreignMinX = Math.min(this.getMin().getBlockX(), this.getMax().getBlockX());
        int foreignMaxX = Math.max(this.getMin().getBlockX(), this.getMax().getBlockX());
        int foreignMinZ = Math.min(this.getMin().getBlockZ(), this.getMax().getBlockZ());
        int foreignMaxZ = Math.max(this.getMin().getBlockZ(), this.getMax().getBlockZ());

        if (foreignMinX > localMinX && foreignMaxX < localMaxX) {
            if (foreignMinZ > localMinZ && foreignMaxZ < localMaxZ) {
                return true;
            }
        }
        return false;
    }

}
