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

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import org.bukkit.entity.Snowball;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class EntityDamageByEntityListener implements Listener {

    public OpenGuild plugin;
    
    public EntityDamageByEntityListener(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void handleEvent(EntityDamageByEntityEvent event) {
        if(event.isCancelled() || GenConf.teampvp){
            return;
        }
        
        if(event.getDamager() instanceof Player) {
            if(event.getEntity() instanceof Player) {
                Player attacked = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                
                if(plugin.getGuilds().hasGuild(attacked) && plugin.getGuilds().hasGuild(attacker)) {
                    Guild attackerGuild = plugin.getGuilds().getPlayerGuild(attacker.getUniqueId());
                    Guild attackedGuild = plugin.getGuilds().getPlayerGuild(attacked.getUniqueId());
                    if(attackerGuild.containsMember(attacked.getUniqueId())) {
                        event.setCancelled(true);
                        
                        if(GenConf.TEAMPVP_MSG) {
                            attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                        }
                    }else if(attackerGuild.isAlly(attackedGuild)){
                        event.setCancelled(true);
                        
                        if(GenConf.TEAMPVP_MSG) {
                            attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                        }
                    }
                }
            }
        }
        else if(event.getDamager() instanceof Arrow) {
            if(event.getEntity() instanceof Player) {
                Player attacked = (Player) event.getEntity();
                ProjectileSource attackerEntity = ((Arrow) event.getDamager()).getShooter();
                
                if(attackerEntity instanceof Player) {
                    Player attacker = (Player) attackerEntity;
                    
                    if(plugin.getGuilds().hasGuild(attacked) && plugin.getGuilds().hasGuild(attacker)) {
                        Guild attackerGuild = plugin.getGuilds().getPlayerGuild(attacker.getUniqueId());
                        Guild attackedGuild = plugin.getGuilds().getPlayerGuild(attacked.getUniqueId());
                        if(attackerGuild.containsMember(attacked.getUniqueId())) {
                            if(attacker.equals(attacked)){
                                return;
                            }

                            if(GenConf.TEAMPVP_MSG) {
                                attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                            }
                            event.setCancelled(true);
                        }else if(attackerGuild.isAlly(attackedGuild)){
                            event.setCancelled(true);

                            if(GenConf.TEAMPVP_MSG) {
                                attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                            }
                        }
                    }
                }
            }
        }
        else if(event.getDamager() instanceof Snowball) {
            if(event.getEntity() instanceof Player) {
                Player attacked = (Player) event.getEntity();
                ProjectileSource attackerEntity = ((Snowball) event.getDamager()).getShooter();
                
                if(attackerEntity instanceof Player) {
                    Player attacker = (Player) attackerEntity;
                    
                    if(plugin.getGuilds().hasGuild(attacked) && plugin.getGuilds().hasGuild(attacker)) {
                        Guild attackerGuild = plugin.getGuilds().getPlayerGuild(attacker.getUniqueId());
                        Guild attackedGuild = plugin.getGuilds().getPlayerGuild(attacked.getUniqueId());
                        if(attackerGuild.containsMember(attacked.getUniqueId())) {
                            if(attacker.equals(attacked)){
                                return;
                            }
                            event.setCancelled(true);

                            if(GenConf.TEAMPVP_MSG) {
                                attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                            }
                        }else if(attackerGuild.isAlly(attackedGuild)){
                            event.setCancelled(true);

                            if(GenConf.TEAMPVP_MSG) {
                                attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
                            }
                        }
                    }
                }
            }
        }
    }
}
