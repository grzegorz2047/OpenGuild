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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;

public class PlayerMoveListener implements Listener {


    private final Cuboids cuboids;

    public PlayerMoveListener(Cuboids cuboids) {
         this.cuboids = cuboids;
    }
    
    @EventHandler
    public void handleEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        cuboids.notifyGuildWhenPlayerEntersCuboid(player);
        if(event.isCancelled() || GenConf.CANENTERAREA || player.hasPermission("openguild.cuboid.bypassenterflag")) {
            return;
        }
        if(!cuboids.canMove(player, event.getFrom(), event.getTo())) {
            event.setTo(event.getFrom());
        }
    }
}
