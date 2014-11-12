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
import com.github.grzegorz2047.openguild.command.CommandDescription;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.module.Module;
import com.github.grzegorz2047.openguild.module.ModuleInfo;
import com.github.grzegorz2047.openguild.module.ModuleLoadException;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.GenConf;

public class ModuleRandomTP implements Module {
    
    @Override
    public ModuleInfo module() {
        return new ModuleInfo("Random Teleport", "Teleporting to random coordinates", "1.0");
    }
    
    @Override
    public void enable(String id) throws ModuleLoadException {
        if(GenConf.ranTpEnabled) {
            Bukkit.getPluginManager().registerEvents(new RandomTPListeners(), OpenGuild.getBukkit());
            
            CommandDescription desc = new CommandDescription();
            desc.set("EN", "Teleport to the random location");
            desc.set("PL", "Teleport do losowej lokalizacji");
            OpenGuild.registerCommand(new CommandInfo(null,
                    "randomtp",
                    desc,
                    new Randomtp(),
                    "openguild.randomtp",
                    "[player]"));
        }
    }
    
}
