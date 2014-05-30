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
package pl.grzegorz2047.openguild2047.commands.arguments;

import com.github.grzegorz2047.openguild.event.MessageBroadcastEvent;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleCuboid;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;
import pl.grzegorz2047.openguild2047.modules.spawn.SpawnChecker;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 *
 * @author Grzegorz
 */
public class CreateArg {

    private CreateArg() {

    }

    public static boolean execute(CommandSender sender, String[] args) {
        String clantag = args[1].toLowerCase();
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        Player p = (Player) sender;
        if(!Data.getInstance().guildExists(clantag)) {
            if(!Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
                if(SpawnChecker.isSpawn(p.getLocation()) && GenConf.blockGuildCreating) {
                    sender.sendMessage(GenConf.prefix + ChatColor.RED + GenConf.spawnMessage);
                    return true;
                }
                if(clantag.matches("[0-9a-zA-Z]*")) {
                    if(clantag.length() <= GenConf.maxclantag && clantag.length() >= GenConf.minclantag) {
                        if(GenConf.badwords == null || !GenConf.badwords.contains(clantag)) {
                            if(GenConf.reqitems != null && !GenConf.reqitems.isEmpty() && GenUtil.hasEnoughItemsForGuild(p.getInventory())) {
                                if(CuboidStuff.checkIfCuboidFarForGuild(p.getLocation())) {
                                    if(!GenUtil.isPlayerNearby(p, GenConf.MIN_CUBOID_RADIUS)) {
                                        if(GenConf.reqitems != null && !GenConf.reqitems.isEmpty()) {
                                            GenUtil.removeRequiredItemsForGuild(p.getInventory());
                                        }
                                        SimpleGuild sg = new SimpleGuild(clantag);
                                        sg.setLeader(p.getUniqueId());
                                        sg.setHome(p.getLocation());
                                        sg.addMember(p.getUniqueId());
                                        SimpleCuboid c = new SimpleCuboid();
                                        c.setOwner(clantag);
                                        c.setRadius(GenConf.MIN_CUBOID_RADIUS);
                                        c.setCenter(p.getLocation());
                                        //TODO: dodac jakies dane o cuboidzie w mysql
                                        if(args.length > 2) {
                                            String desc = GenUtil.argsToString(args, 2, args.length);
                                            if(desc.length() > 32) {
                                                p.sendMessage(MsgManager.desctoolong);
                                                return true;
                                            }
                                            sg.setDescription(desc);
                                        } else {
                                            sg.setDescription("Domyslny opis gildii :<");
                                        }
                                        Data.getInstance().cuboids.put(clantag, c);

                                        SimplePlayerGuild spg = new SimplePlayerGuild(p.getUniqueId(), sg.getTag(), true);
                                        Data.getInstance().guilds.put(sg.getTag(), sg);
                                        Data.getInstance().ClansTag.add(sg.getTag());
                                        Data.getInstance().guildsplayers.put(p.getUniqueId(), spg);
                                        if(GenConf.playerprefixenabled) {
                                          TagManager.setTag(p.getUniqueId());           
                                        }

                                        saveToDb(clantag, sg.getDescription(), p, p.getLocation());

                                        Guilds.getLogger().log(Level.INFO, "Gracz " + p.getName() + " stworzyl gildie o nazwie " + spg.getClanTag());
                                        p.sendMessage(MsgManager.createguildsuccess);

                                        Location l = p.getLocation();
                                        l.getBlock().setType(Material.DRAGON_EGG);
                                        new Location(l.getWorld(), l.getBlockX(), l.getBlockY() - 1, l.getBlockZ()).getBlock().setType(Material.BEDROCK);
                                        
                                        // Event
                                        MessageBroadcastEvent event = new MessageBroadcastEvent(MessageBroadcastEvent.Message.CREATE);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if(!event.isCancelled()) {
                                            Bukkit.broadcastMessage(event.getMessage().replace("{TAG}", sg.getTag().toUpperCase()).replace("{PLAYER}", sender.getName()));
                                        }
                                        return true;
                                    } else {
                                        p.sendMessage(MsgManager.playerstooclose);
                                        return true;
                                    }
                                } else {
                                    p.sendMessage(MsgManager.gildtocloseothers);
                                    return true;
                                }

                            } else {
                                p.sendMessage(MsgManager.notenoughitems);
                                return true;
                            }
                        } else {
                            p.sendMessage(MsgManager.illegaltag);
                            return true;
                        }
                    } else {
                        p.sendMessage(MsgManager.toolongshorttag);
                        return true;
                    }
                } else {
                    p.sendMessage(MsgManager.unsupportedchars);
                    return true;
                }
            } else {
                p.sendMessage(MsgManager.alreadyinguild);
                return true;
            }
        } else {
            p.sendMessage(MsgManager.guildexists);
            return true;
        }

    }

    private static void saveToDb(String tag, String description, Player leader, Location home) {
        SQLHandler.insert(tag, description, leader.getUniqueId(), null, home.getBlockX(), home.getBlockY(), home.getBlockZ(), home.getWorld().getName(), GenConf.MIN_CUBOID_RADIUS);
        SQLHandler.update(leader.getUniqueId(), SQLHandler.PType.GUILD, tag);
        SQLHandler.update(leader.getUniqueId(), SQLHandler.PType.ISLEADER, "true");//ustawia isleader wa bazie na true
    }

}
