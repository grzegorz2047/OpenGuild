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
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import org.bukkit.entity.Snowball;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class EntityDamageByEntityListener implements Listener {

    private final Guilds guilds;
    private AntiLogoutManager logout;

    public EntityDamageByEntityListener(AntiLogoutManager logout, Guilds guilds) {
        this.logout = logout;
        this.guilds = guilds;
    }

    @EventHandler
    private void entityDMGbyEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || GenConf.teampvp) {
            return;
        }
        Player attacked = null;
        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                attacked = (Player) event.getEntity();
                attacker = (Player) event.getDamager();
            }
        } else if (event.getDamager() instanceof Arrow) {
            if (event.getEntity() instanceof Player) {
                attacked = (Player) event.getEntity();
                ProjectileSource attackerEntity = ((Arrow) event.getDamager()).getShooter();
                if (attackerEntity instanceof Player) {
                    attacker = (Player) attackerEntity;
                }
            }
        } else if (event.getDamager() instanceof Snowball) {
            if (event.getEntity() instanceof Player) {
                attacked = (Player) event.getEntity();
                ProjectileSource attackerEntity = ((Snowball) event.getDamager()).getShooter();
                if (attackerEntity instanceof Player) {
                    attacker = (Player) attackerEntity;
                }
            }
        }
        if (attacker != null && attacked != null) {
            teamPVPCheck(event, attacked, attacker);
            if (GenConf.ANTI_LOGOUT) {
                logout.updatePlayersFight(attacker, attacked);

            }
        }
    }

    private void teamPVPCheck(EntityDamageByEntityEvent event, Player attacked, Player attacker) {
        if (areBothInGuild(attacked, attacker)) {
            checkTheirTeams(event, attacked, attacker);
        }
    }

    private void checkTheirTeams(EntityDamageByEntityEvent event, Player attacked, Player attacker) {
        Guild attackerGuild = guilds.getPlayerGuild(attacker.getUniqueId());
        Guild attackedGuild = guilds.getPlayerGuild(attacked.getUniqueId());
        if (attackerGuild.equals(attackedGuild)) {
            if (isTheSamePlayer(attacked, attacker)) {
                return;
            }
            processTeamPVP(event, attacker);
        } else if (attackerGuild.isAlly(attackedGuild)) {
            processTeamPVP(event, attacker);
        }
    }

    private boolean areBothInGuild(Player attacked, Player attacker) {
        return guilds.hasGuild(attacked) && guilds.hasGuild(attacker);
    }

    private boolean isTheSamePlayer(Player attacked, Player attacker) {
        return attacker.equals(attacked);
    }

    private void processTeamPVP(EntityDamageByEntityEvent event, Player attacker) {
        if (isTeamPvpDisabled()) {
            event.setCancelled(true);
            sendMessageTeamPvpBlocked(attacker);
        }
    }

    private void sendMessageTeamPvpBlocked(Player attacker) {
        if (GenConf.TEAMPVP_MSG) {
            attacker.sendMessage(MsgManager.get("pvpguildmember", "&cNie mozesz uderzyc gracza sojuszniczej gildii"));
        }
    }

    private boolean isTeamPvpDisabled() {
        return !GenConf.teampvp;
    }
}
