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

package pl.grzegorz2047.openguild2047.api.command;

import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandDescription;
import pl.grzegorz2047.openguild2047.commands.command.CommandInfo;
import pl.grzegorz2047.openguild2047.commands.command.CommandManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class OpenCommandManager implements CommandManager {
    
    private final File file = new File("plugins/OpenGuild2047/commands.yml");
    private final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
    private static int loadedCmds;
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
    
    public static void registerPluginCommands(OpenGuild plugin) {
        List<String> cmds = OpenGuild.getAPI().getCmdManager().getCommands();
        loadedCmds = 0;
        if(cmds == null) {
            return;
        }
        Guilds.getLogger().log(Level.INFO, "Loaded " + loadedCmds + " commands.");
    }
    
    private static void register(String cmd, Command exe, boolean perm, String us) {
        CommandDescription cmdDesc = new CommandDescription();
        cmdDesc.set(MsgManager.getIgnorePref("cmd-" + cmd));
        
        String perms = null;
        if(perm) perms = "openguild.command." + cmd;
        
        OpenGuild.getAPI().registerCommand(new CommandInfo(null, cmd, cmdDesc, exe, perms, us));
        loadedCmds++;
    }
    
}
