/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.listeners;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.OfflinePlayer;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

public class PlayerChatListener implements Listener {

    private OpenGuild plugin;
    
    public PlayerChatListener(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void handleEvent(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();
        if(plugin.getGuildHelper().hasGuild(player)) {
            Guild guild = plugin.getGuildHelper().getPlayerGuild(uuid);
            String tag = guild.getTag().toUpperCase();
            
            if(event.getMessage().startsWith("!") && event.getMessage().length()>=2){
                message = message.split("!")[1];
                String format = MsgManager.getIgnorePref("guildchat")
                        .replace("{GUILD}", guild.getTag())
                        .replace("{PLAYER}", player.getName())
                        .replace("{MESSAGE}", message);
                for(UUID memuuid : guild.getMembers()) {
                    OfflinePlayer op = plugin.getServer().getOfflinePlayer(memuuid);
                    if(op.isOnline()) {//guildchat: '&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}'
                        op.getPlayer().sendMessage(format);
                    }
                }   
                event.setCancelled(true);
                return;
            }else if(event.getMessage().startsWith("@") && event.getMessage().length()>=2){
                message = message.split("@")[1];
                for(Relation r : guild.getAlliances()){
                    Guild ally = null;
                    if(guild.equals(plugin.getGuildHelper().getGuilds().get(r.getWho()))){
                        System.out.print("guild "+guild.getTag()+" equals "+r.getWho());
                        ally = plugin.getGuildHelper().getGuilds().get(r.getWithWho());
                    }else{
                        System.out.print("guild "+guild.getTag()+" equals "+r.getWho());
                        ally = plugin.getGuildHelper().getGuilds().get(r.getWho()); 
                    }
                    if(ally != null){
                        String format = MsgManager.getIgnorePref("guildchatally")
                                .replace("{GUILD}", guild.getTag())
                                .replace("{PLAYER}", player.getName())
                                .replace("{MESSAGE}", message);
                        for(UUID memuuid : ally.getMembers()) {
                            OfflinePlayer op = plugin.getServer().getOfflinePlayer(memuuid);
                            if(op.isOnline()) {//guildchat: '&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}'
                                op.getPlayer().sendMessage(format);
                            }
                        }
                    }
                        
                }
                for(UUID memuuid : guild.getMembers()) {
                    OfflinePlayer op = plugin.getServer().getOfflinePlayer(memuuid);
                    if(op.isOnline()) {//guildchat: '&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}'
                        op.getPlayer().sendMessage(MsgManager.getIgnorePref("guildchat").replace("{GUILD}", guild.getTag()).replace("{PLAYER}", player.getName()).replace("{MESSAGE}", message));
                    }
                }   
                event.setCancelled(true);
                return;
            }
            
            if(!GenConf.guildprefixinchat) {
                if(event.getFormat().contains("%OPENGUILD_TAG%")) {
                    event.setFormat(event.getFormat().replace("%OPENGUILD_TAG%", tag));
                }
            } else {
                event.setFormat("§7[§r" + tag + "§7]§r " + event.getFormat());
            }
        }
    }
}
