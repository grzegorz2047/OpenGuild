/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.grzegorz2047.openguild2047;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.grzegorz2047.openguild2047.listeners.PlayerChat;

/**
 *
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin{

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        loadAllListeners();
        Bukkit.getLogger().log(Level.INFO, "§7 {0} by §3grzegorz2047§7 zostal uruchomiony!", this.getName());
        Bukkit.getConsoleSender().sendMessage(this.getName()+" by §3grzegorz2047§7 zostal uruchomiony!");
        
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }
     
    void loadAllListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerChat(), this);
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
    
    
    
    
    
}
