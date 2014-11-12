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
