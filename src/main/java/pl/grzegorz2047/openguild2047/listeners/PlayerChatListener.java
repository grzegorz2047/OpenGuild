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
        if(plugin.getGuilds().hasGuild(player)) {
            Guild guild = plugin.getGuilds().getPlayerGuild(uuid);
            String tag = guild.getTag().toUpperCase();
            
            if(event.getMessage().startsWith(GenConf.guildChatKey) && event.getMessage().length()>=2){
                message = message.substring(1);
                String format = GenConf.guildChatFormat
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
            }else if(event.getMessage().startsWith(GenConf.allyChatKey) && event.getMessage().length()>=2){
                message = message.substring(1);
                for(Relation r : guild.getAlliances()){
                    Guild ally = null;
                    if(guild.equals(plugin.getGuilds().getGuilds().get(r.getWho()))){
                        ally = plugin.getGuilds().getGuilds().get(r.getWithWho());
                    }else{
                        ally = plugin.getGuilds().getGuilds().get(r.getWho());
                    }
                    if(ally != null){
                        String format = GenConf.allyChatFormat
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
                    String format = GenConf.guildChatFormat
                            .replace("{PLAYER}", player.getName())
                            .replace("{MESSAGE}", message);
                    if(op.isOnline()) {//guildchat: '&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}'
                        op.getPlayer().sendMessage(format);
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
