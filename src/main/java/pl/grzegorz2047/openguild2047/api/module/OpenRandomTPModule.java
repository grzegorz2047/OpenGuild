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

package pl.grzegorz2047.openguild2047.api.module;

import com.github.grzegorz2047.openguild.User;
import com.github.grzegorz2047.openguild.module.RandomTPModule;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class OpenRandomTPModule implements RandomTPModule {
    
    @Override
    public boolean isEnabled() {
        return GenConf.ranTpEnabled;
    }
    
    @Override
    public boolean isButtonEnabled() {
        return GenConf.ranTpButton;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        GenConf.ranTpEnabled = enabled;
    }
    
    @Override
    public void setButtonEnabled(boolean enabled) {
        GenConf.ranTpButton = enabled;
    }
    
    @Override
    public void teleport(Player player) {
        Random random = new Random();
        World world = player.getWorld();
        int x = random.nextInt();
        int z = random.nextInt();
        if(random.nextBoolean())
            x = x - (2 * x);
        if(random.nextBoolean())
            z = z - (2 * z);
        
        Location location = world.getHighestBlockAt(new Location(world, x, 64, z)).getLocation();
        player.sendMessage(MsgManager.get("rantp"));
        player.teleport(location);
    }
    
    @Override
    public void teleport(User user) {
        teleport(user.getBukkit());
    }
    
}
