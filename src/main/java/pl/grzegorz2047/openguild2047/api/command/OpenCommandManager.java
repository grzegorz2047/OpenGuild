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

package pl.grzegorz2047.openguild2047.api.command;

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandDescription;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.command.CommandManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.commands2.AcceptCmd;
import pl.grzegorz2047.openguild2047.commands2.AdminCmd;
import pl.grzegorz2047.openguild2047.commands2.CreateCmd;
import pl.grzegorz2047.openguild2047.commands2.DescriptionCmd;
import pl.grzegorz2047.openguild2047.commands2.DisbandCmd;
import pl.grzegorz2047.openguild2047.commands2.HomeCmd;
import pl.grzegorz2047.openguild2047.commands2.InfoCmd;
import pl.grzegorz2047.openguild2047.commands2.InviteCmd;
import pl.grzegorz2047.openguild2047.commands2.ItemsCmd;
import pl.grzegorz2047.openguild2047.commands2.KickCmd;
import pl.grzegorz2047.openguild2047.commands2.LeaderCmd;
import pl.grzegorz2047.openguild2047.commands2.LeaveCmd;
import pl.grzegorz2047.openguild2047.commands2.ListCmd;
import pl.grzegorz2047.openguild2047.commands2.MembersCmd;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class OpenCommandManager implements CommandManager {
    
    private final File file = OpenGuild.CMDS;
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
    
    public static void registerPluginCommands() {
        List<String> cmds = OpenGuild.getAPI().getCmdManager().getCommands();
        loadedCmds = 0;
        
        if(cmds.contains("accept")) {
            register("accept", new AcceptCmd(), false, "<guild>");
        }
        if(cmds.contains("admin")) {
            register("admin", new AdminCmd(), true, "<arg>");
        }
        if(cmds.contains("ally")) {
            //register("ally", new AllyCmd(), false, "<guild>");
        }
        if(cmds.contains("create")) {
            register("create", new CreateCmd(), false, "<tag> [description...]");
        }
        if(cmds.contains("description")) {
            register("decline", new DescriptionCmd(), false, "[change <desc...>]");
        }
        if(cmds.contains("disband")) {
            register("disband", new DisbandCmd(), false, null);
        }
        if(cmds.contains("enemy")) {
            //register("enemy", new EnemyCmd(), false, "<guild>");
        }
        if(cmds.contains("expand")) {
            //register("expand", new ExpandCmd(), false, null);
        }
        if(cmds.contains("home")) {
            register("home", new HomeCmd(), false, null);
        }
        if(cmds.contains("info")) {
            register("info", new InfoCmd(), false, "[guild]");
        }
        if(cmds.contains("invite")) {
            register("invite", new InviteCmd(), false, "<player>");
        }
        if(cmds.contains("items")) {
            register("items", new ItemsCmd(), false, null);
        }
        if(cmds.contains("kick")) {
            register("kick", new KickCmd(), false, "<player>");
        }
        if(cmds.contains("leader")) {
            register("leader", new LeaderCmd(), false, "<player>");
        }
        if(cmds.contains("leave")) {
            register("leave", new LeaveCmd(), false, null);
        }
        if(cmds.contains("list")) {
            register("list", new ListCmd(), false, null);
        }
        if(cmds.contains("members")) {
            register("members", new MembersCmd(), false, null);
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
