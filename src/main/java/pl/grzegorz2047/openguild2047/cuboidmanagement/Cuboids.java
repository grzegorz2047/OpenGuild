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
import com.github.grzegorz2047.openguild.Relation;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * @author Grzegorz
 */
public class Cuboids {
    private final Guilds guilds;
    private Map<String, Cuboid> cuboids = new HashMap<String, Cuboid>();


    public Cuboids(Guilds guilds) {
        this.guilds = guilds;
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

    public boolean allowedToDoItHere(Player player, Location location) {
        if (this.checkIfInAnyCuboid(cuboids.entrySet().iterator(), location)) {
            //System.out.println("1 allowed");
            if (guilds.hasGuild(player.getUniqueId())) {
                //System.out.println("2 allowed");
                String tag = guilds.getPlayerGuild(player.getUniqueId()).getName();
                Cuboid playerCuboid = cuboids.get(tag);
                if (playerCuboid.isinCuboid(location)) {
                    //System.out.println("3 allowed");
                    return true;//Gdzies tu budowanie sojusznikow, ale na razie czarna magia
                } else if (!player.hasPermission("openguild.cuboid.bypassplace")) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean canMove(Player player, Location from, Location to) {
        Iterator<Map.Entry<String, Cuboid>> it = cuboids.entrySet().iterator();
        if (isPlayerInGuild(player)) {
            String tag = guilds.getPlayerGuild(player.getUniqueId()).getName();
            return cuboids.get(tag).isinCuboid(to) || !checkIfInAnyCuboid(it, to);
        } else {
            return !checkIfInAnyCuboid(it, to);
        }
    }

    public void notifyGuildWhenPlayerEntersCuboid(Player player) {
        Iterator<Map.Entry<String, Cuboid>> it = cuboids.entrySet().iterator();
        String tag = null;
        if (isPlayerInGuild(player)) {
            tag = guilds.getPlayerGuild(player.getUniqueId()).getName();
        }
        String guildscuboidtag = getOwnerOfCuboidInPlayerPosition(it, player.getLocation());
        //Bukkit.broadcastMessage("Gracz "+player.getName()+" jest na "+guildscuboidtag);
        if (!foundCuboid(guildscuboidtag)) {
            if (isPlayerCurrentlyInCuboidSpace(player)) {
                String tagSaved = playersenteredcuboid.get(player.getName());
                player.sendMessage(MsgManager.get("leavecubpl").replace("{GUILD}", tagSaved.toUpperCase()));
                playersenteredcuboid.remove(player.getName());
            }
            return;
        }
        if (checkIfPlayerIsStillOnCuboid(player, guildscuboidtag)) return;
        player.sendMessage(MsgManager.get("entercubpl").replace("{GUILD}", guildscuboidtag.toUpperCase()));
        playersenteredcuboid.put(player.getName(), guildscuboidtag);
        Guild cuboidowner = guilds.getGuild(guildscuboidtag);
        if (isTheSame(tag, guildscuboidtag)) {
            return;
        }
        for (Relation r : cuboidowner.getAlliances()) {
            if (!isAlly(r)) {
                continue;
            }
            if (isInAGuildOwningThisCuboid(tag, r)) {
                return;
            }
        }
        Guild guild = guilds.getGuild(guildscuboidtag);
        guilds.notifyMembersAboutSomeoneEnteringTheirCuboid(player,guild, tag, foundCuboid(tag));
    }

    private boolean foundCuboid(String guildscuboidtag) {
        return guildscuboidtag != null;
    }

    private boolean checkIfPlayerIsStillOnCuboid(Player player, String guildscuboidtag) {
        if (isPlayerCurrentlyInCuboidSpace(player)) {
            String guildOnList = playersenteredcuboid.get(player.getName());
            if (isTheSame(guildscuboidtag, guildOnList)) {
                return true;
            }
            playersenteredcuboid.remove(player.getName());
        }
        return false;
    }

    private boolean isTheSame(String guildscuboidtag, String guildOnList) {
        return guildOnList.equals(guildscuboidtag);
    }

    private boolean isPlayerCurrentlyInCuboidSpace(Player player) {
        return playersenteredcuboid.containsKey(player.getName());
    }


    private boolean isAlly(Relation r) {
        return r.getState().equals(Relation.Status.ALLY);
    }

    private boolean isInAGuildOwningThisCuboid(String tag, Relation r) {
        return isTheSame(tag, r.getBaseGuildTag()) || isTheSame(tag, r.getAlliedGuildTag());
    }

    private boolean isPlayerInGuild(Player player) {
        return guilds.
                hasGuild(player.getUniqueId());
    }

    public boolean checkIfInAnyCuboid(Iterator<Map.Entry<String, Cuboid>> it, Location to) {
        while (it.hasNext()) {
            if (it.next().getValue().isinCuboid(to)) {
                return true;
            }
        }
        return false;
    }

    private String getOwnerOfCuboidInPlayerPosition(Iterator<Map.Entry<String, Cuboid>> it, Location loc) {
        while (it.hasNext()) {
            Map.Entry<String, Cuboid> next = it.next();
            if (playerInCuboid(loc, next)) {
                return next.getValue().getOwner();
            }
        }
        return null;
    }

    private boolean playerInCuboid(Location loc, Map.Entry<String, Cuboid> next) {
        return next.getValue().isinCuboid(loc);
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
