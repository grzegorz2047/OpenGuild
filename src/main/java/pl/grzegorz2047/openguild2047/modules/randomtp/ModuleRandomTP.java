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

package pl.grzegorz2047.openguild2047.modules.randomtp;

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.User;
import com.github.grzegorz2047.openguild.command.CommandDescription;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.module.Module;
import com.github.grzegorz2047.openguild.module.ModuleInfo;
import com.github.grzegorz2047.openguild.module.ModuleLoadException;
import com.github.grzegorz2047.openguild.module.RandomTPModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ModuleRandomTP implements RandomTPModule, Module {

    @Override
    public ModuleInfo module() {
        return new ModuleInfo("Random Teleport", "Teleporting to random coordinates", "1.0");
    }

    @Override
    public void enable(String id) throws ModuleLoadException {
        if (GenConf.ranTpEnabled) {
            Bukkit.getPluginManager().registerEvents(new RandomTPListeners(), OpenGuild.getBukkit());
            
/*            CommandDescription desc = new CommandDescription();
            desc.set("EN", "Teleport to the random location");
            desc.set("PL", "Teleport do losowej lokalizacji");
            OpenGuild.registerCommand(new CommandInfo(null,
                    "randomtp",
                    desc,
                    new Randomtp(),
                    "openguild.randomtp",
                    "[player]"));
        */
        }
    }

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
        Location location = null;
        try {
            location = findSaveSpot(player);
            player.teleport(location);
        } catch (Exception e) {
            player.sendMessage(MsgManager.get("nosafertp"));
        }
    }

    private static Random random = new Random();
    private List<Material> unsafeMaterials = Arrays.asList(Material.LAVA, Material.WATER);

    private Location findSaveSpot(Player player) throws Exception {
        for (int i = 0; i < 10; i++) {
            World world = player.getWorld();
            int x = random.nextInt(GenConf.ranTpRange);
            int z = random.nextInt(GenConf.ranTpRange);
            if (random.nextBoolean())
                x = x - (2 * x);
            if (random.nextBoolean())
                z = z - (2 * z);
            Location location = world.getHighestBlockAt(new Location(world, x, 64, z)).getLocation();
            player.sendMessage(MsgManager.get("rantp"));
            if (!unsafeMaterials.contains(location.getBlock().getType())) {
                return location;
            }
        }
        throw new Exception("No safe place found");
    }

    @Override
    public void teleport(User user) {
        teleport(user.getBukkit());
    }
}
