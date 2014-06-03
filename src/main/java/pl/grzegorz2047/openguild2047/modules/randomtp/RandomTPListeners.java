/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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

package pl.grzegorz2047.openguild2047.modules.randomtp;

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.module.RandomTPModule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

public class RandomTPListeners implements Listener {
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)
            return;
        RandomTPModule module = (RandomTPModule) OpenGuild.getModules().getModule("random-tp");
        if(!module.isEnabled() || !module.isButtonEnabled())
            return;
        
        Block block = e.getClickedBlock();
        if(block.getType() == Material.STONE_BUTTON ||
                block.getType() == Material.WOOD_BUTTON) {
            Button button = (Button) e.getClickedBlock().getState().getData();
            if(block.getRelative(button.getAttachedFace()).getType() == Material.SPONGE)
                module.teleport(e.getPlayer());
        }
    }
    
}
