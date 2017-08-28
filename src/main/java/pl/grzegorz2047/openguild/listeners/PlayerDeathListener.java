/*
 * Copyright 2014 Aleksander.
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
package pl.grzegorz2047.openguild.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.grzegorz2047.openguild.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.ranking.EloRanking;

/**
 * @author Aleksander
 */
public class PlayerDeathListener implements Listener {

    private final SQLHandler sqlHandler;
    private final AntiLogoutManager antiLogoutManager;
    private final EloRanking eloranking;

    public PlayerDeathListener(SQLHandler sqlHandler, AntiLogoutManager antiLogoutManager, EloRanking eloRanking) {
        this.sqlHandler = sqlHandler;
        this.antiLogoutManager = antiLogoutManager;
        this.eloranking = eloRanking;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            sqlHandler.addDeath(player);
            sqlHandler.addKill(killer);
            antiLogoutManager.removePlayerFromFight(killer.getName());
            eloranking.recountEloFight(killer, player);
            //set last kill to winner w metadata i potem jak zginie ten sam nie nalicza elo
        }
        antiLogoutManager.removePlayerFromFight(player.getName());
    }


}
