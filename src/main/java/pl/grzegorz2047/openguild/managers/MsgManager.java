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
package pl.grzegorz2047.openguild.managers;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild.configuration.GenConf;

/**
 * @author Grzegorz
 */
public class MsgManager {

    private static HashMap<String, String> messages;

    public static File file = new File("plugins/OpenGuild/messages_" + GenConf.LANG.toLowerCase() + ".yml");
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static String get(String path) {
        return GenConf.prefix + getIgnorePref(path);
    }

    public static String getIgnorePref(String path) {
        return getIgnorePref(path, getNullMessage(GenConf.LANG, path));
    }

    public static String get(String path, String def) {
        return GenConf.prefix + getIgnorePref(path, getNullMessage(GenConf.LANG, path));
    }

    private static String getIgnorePref(String path, String def) {
        if (messages == null) {
            loadMessages();
        }
        if (messages.get(path) == null) {
            return def;
        } else {
            return messages.get(path);
        }

    }

    private static void loadMessages() {
        messages = new HashMap<>();
        for (String path : config.getConfigurationSection("").getKeys(false)) {
            messages.put(path, config.getString(path).replace("&", "§"));
        }
    }

    private static String getNullMessage(String lang, String path) {
        String result;

        if (lang.equalsIgnoreCase("PL"))
            result = "Wiadomosc nie znaleziona";
        else if (lang.equalsIgnoreCase("SV"))
            result = "Meddelande har inte hittas";
        else
            result = "Message not found";

        return ChatColor.RED + result + " :( MSG: " + path;
    }

}
