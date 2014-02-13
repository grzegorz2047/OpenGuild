/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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

package pl.grzegorz2047.openguild2047;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import pl.grzegorz2047.openguild2047.commands.GildiaCommand;
import pl.grzegorz2047.openguild2047.listeners.EntityDamageByEntity;
import pl.grzegorz2047.openguild2047.listeners.PlayerChat;
import pl.grzegorz2047.openguild2047.listeners.PlayerMove;

/**
 *
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin{

	private static OpenGuild instance;
	
    @Override
    public void onEnable() {
    	long init = System.currentTimeMillis();
    	instance = this;
        copyDefaultFiles();
        checkPlugins();
        getCommand("gildia").setExecutor(new GildiaCommand());
        loadAllListeners();
        Bukkit.getConsoleSender().sendMessage("§a"+this.getName()+"§6 by §3grzegorz2047§6 zostal uruchomiony w " + String.valueOf(System.currentTimeMillis() - init) + " ms!");
        
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void checkPlugins() {
    	if(getServer().getPluginManager().getPlugin("TagAPI") == null) {
            getLogger().severe("Nie znaleziono pluginu TagAPI! Pobierz go ze strony http://dev.bukkit.org/bukkit-plugins/tag");
            getLogger().severe("Wylaczanie pluginu OpenGuild2047...");
            return;
    	}
    }
    
    private void copyDefaultFiles() {
    	//saveDefaultConfig();
    }
    
    void loadAllListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new EntityDamageByEntity(), this);
    }
    
    public static OpenGuild get() {
    	return instance;
    }
    
}
