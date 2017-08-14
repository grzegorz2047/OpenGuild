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

import com.github.grzegorz2047.openguild.Cuboid;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * @author Grzegorz
 */
public class Cuboids {
    private OpenGuild plugin;
    private Map<String, Cuboid> cuboids = new HashMap<String, Cuboid>();


    public Cuboids(OpenGuild plugin) {
        this.plugin = plugin;
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
        if (plugin.getCuboids().checkIfInAnyCuboid(cuboids.entrySet().iterator(), location)) {
            if (plugin.getGuilds().hasGuild(player.getUniqueId())) {
                String tag = plugin.getGuilds().getPlayerGuild(player.getUniqueId()).getTag();
                if (cuboids.get(tag).isinCuboid(location)) {
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
            String tag = plugin.getGuilds().getPlayerGuild(player.getUniqueId()).getTag();
            return cuboids.get(tag).isinCuboid(to) || !checkIfInAnyCuboid(it, to);
        } else {
            return !checkIfInAnyCuboid(it, to);
        }
    }

    public void notifyGuildWhenPlayerEntersCuboid(Player player) {
        Iterator<Map.Entry<String, Cuboid>> it = cuboids.entrySet().iterator();
        String tag = null;
        if (isPlayerInGuild(player)) {
            tag = plugin.getGuilds().getPlayerGuild(player.getUniqueId()).getTag();
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
        Guild cuboidowner = getGuild(guildscuboidtag);
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
        Guild guild = getGuild(guildscuboidtag);
        notifyMembersAboutSomeoneEnteringTheirCuboid(player, tag, guild);
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

    private Guild getGuild(String guildtag) {
        return plugin.getGuilds().getGuilds().get(guildtag);
    }

    private void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, String tag, Guild guild) {
        for (UUID mem : guild.getMembers()) {
            OfflinePlayer op = plugin.getServer().getOfflinePlayer(mem);
            if (op.isOnline()) {
                notifySomeoneEnteredCuboid(player, tag, op);
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (GenConf.cubNotifySound) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(Player player, String tag, OfflinePlayer op) {
        if (GenConf.cubNotifyMem) {
            if (!foundCuboid(tag)) {

                op.getPlayer().sendMessage(MsgManager.get("entercubmemsnoguild").
                        replace("{PLAYER}", player.getName()));
            } else {
                op.getPlayer().sendMessage(MsgManager.get("entercubmems").
                        replace("{PLAYER}", player.getName()).
                        replace("{GUILD}", tag.toUpperCase()));
            }

        }
    }

    private boolean isAlly(Relation r) {
        return r.getState().equals(Relation.Status.ALLY);
    }

    private boolean isInAGuildOwningThisCuboid(String tag, Relation r) {
        return isTheSame(tag, r.getWho()) || isTheSame(tag, r.getWithWho());
    }

    private boolean isPlayerInGuild(Player player) {
        return plugin.
                getGuilds().
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
