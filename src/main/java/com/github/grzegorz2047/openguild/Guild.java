/**
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.grzegorz2047.openguild;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class Guild extends GuildMembers {

    private String tag;
    private String description;

    private Location home;

    private String alliancesString = "";
    private List<Guild> alliances = new ArrayList<Guild>();

    private String enemiesString = "";
    private List<Guild> enemies = new ArrayList<Guild>();

    private Cuboid cuboid;

    public Guild(pl.grzegorz2047.openguild2047.OpenGuild plugin) {
        super(plugin);
        this.setGuild(this);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Location getHome() {
        return home;
    }

    public void setAlliances(List<Guild> alliances) {
        this.alliances = alliances;
    }

    public List<Guild> getAlliances() {
        return alliances;
    }

    public void setEnemies(List<Guild> enemies) {
        this.enemies = enemies;
    }

    public List<Guild> getEnemies() {
        return enemies;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setAlliancesString(String alliancesString) {
        this.alliancesString = alliancesString;
    }

    public String getAlliancesString() {
        return alliancesString;
    }

    public void setEnemiesString(String enemiesString) {
        this.enemiesString = enemiesString;
    }

    public String getEnemiesString() {
        return enemiesString;
    }
}
