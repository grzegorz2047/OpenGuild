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

package pl.grzegorz2047.openguild2047.modules.hardcore;

import pl.grzegorz2047.openguild2047.BagOfEverything;
import pl.grzegorz2047.openguild2047.modules.module.Module;
import pl.grzegorz2047.openguild2047.modules.module.ModuleInfo;
import pl.grzegorz2047.openguild2047.modules.module.ModuleLoadException;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

public class ModuleHardcore implements Module {
    
    @Override
    public ModuleInfo module() {
        return new ModuleInfo("Hardcore", "Hardcore mode features", "1.0");
    }
    
    @Override
    public void enable(String id) throws ModuleLoadException {
        if(GenConf.hcBans) {
            HardcoreSQLHandler.createTables();
            Bukkit.getPluginManager().registerEvents(new HardcoreListeners(), BagOfEverything.getBukkit());
        }
    }
    
}