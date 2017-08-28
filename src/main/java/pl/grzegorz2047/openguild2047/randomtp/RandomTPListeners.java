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

package pl.grzegorz2047.openguild2047.randomtp;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

public class RandomTPListeners implements Listener {

    private final RandomTPHandler randomTPHandler;

    public RandomTPListeners(RandomTPHandler randomTPHandler) {
        this.randomTPHandler = randomTPHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)
            return;

        if(!randomTPHandler.isEnabled() || !randomTPHandler.isButtonEnabled())
            return;

        Block block = e.getClickedBlock();
        if(block.getType() == Material.STONE_BUTTON ||
                block.getType() == Material.WOOD_BUTTON) {
            Button button = (Button) e.getClickedBlock().getState().getData();
            if(block.getRelative(button.getAttachedFace()).getType() == Material.SPONGE)
                randomTPHandler.teleport(e.getPlayer());
        }
    }
    
}
