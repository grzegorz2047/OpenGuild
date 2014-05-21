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

import com.github.grzegorz2047.openguild.module.ModuleManager;
import java.util.HashMap;
import java.util.Set;
import pl.grzegorz2047.openguild2047.api.Guilds;

public class OpenModuleManager implements ModuleManager {
    
    private HashMap<String, Object> modules;
    
    public OpenModuleManager() {
        defaultModules();
    }
    
    @Override
    public Object getModule(String name) {
        return modules.get(name.toLowerCase());
    }
    
    @Override
    public Set<String> getModules() {
        return modules.keySet();
    }
    
    @Override
    public boolean registerModule(String name, Object object) {
        name = name.toLowerCase();
        if(modules.containsKey(name)) {
            Guilds.getLogger().severe("Could not load module '" + name + "'! This modules name already exists!");
            return false;
        } else {
            modules.put(name, object);
            Guilds.getLogger().info("Loaded module '" + name + "' from " + object.toString() + ".");
            return true;
        }
    }
    
    private void defaultModules() {
        modules = new HashMap<String, Object>();
        this.registerModule("hardcore", new OpenHardcoreModule());
        this.registerModule("spawn", new OpenSpawnModule());
    }
    
}
