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
