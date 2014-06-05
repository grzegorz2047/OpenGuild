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

package pl.grzegorz2047.openguild2047.command;

import com.github.grzegorz2047.openguild.command.CommandManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild2047.OpenGuild;

public class OpenCommandManager implements CommandManager {
    
    private final File file = OpenGuild.CMDS;
    private final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
    private HashMap<String, List<String>> aliases;
    private HashMap<String, Boolean> cmds;
    private List<String> enabled;
    
    @Override
    public List<String> getAliases(String cmd) {
        if(aliases == null)
            loadAliases();
        if(aliases != null)
            return aliases.get(cmd);
        else return null;
    }
    
    @Override
    public List<String> getCommands() {
        if(cmds == null)
            loadCmds();
        return enabled;
        
    }
    
    @Override
    public File getFile() {
        return file;
    }
    
    @Override
    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
    
    private void loadAliases() {
        aliases = new HashMap<String, List<String>>();
        
        if(fileConfiguration.getConfigurationSection("aliases") != null) {
            for(String cmd : fileConfiguration.getConfigurationSection("aliases").getKeys(false)) {
                if(fileConfiguration.getStringList("aliases." + cmd) != null && !aliases.containsKey(cmd)) {
                    aliases.put(cmd, fileConfiguration.getStringList("aliases." + cmd));
                }
            }
        }
    }
    
    private void loadCmds() {
        cmds = new HashMap<String, Boolean>();
        enabled = new ArrayList<String>();
        
        for(String cmd : fileConfiguration.getConfigurationSection("commands").getKeys(false)) {
            boolean bool = false;
            if(fileConfiguration.getBoolean("commands." + cmd, true)) {
                bool = true;
                enabled.add(cmd);
            }
            cmds.put(cmd, bool);
        }
    }
    
}
