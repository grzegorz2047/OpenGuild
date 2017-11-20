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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

/**
 * @author Grzegorz
 */
public class MsgManager {

    private static HashMap<String, String> messages;
    public static String prefix = "§7[§6OpenGuild§7]§7 ";
    public static String LANG = "EN";
    public static File file;

    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static String get(String path) {
        return prefix + getIgnorePref(path);
    }

    public static String getIgnorePref(String path) {
        return getIgnorePref(path, getNullMessage(LANG, path));
    }

    public static String get(String path, String def) {
        return prefix + getIgnorePref(path, getNullMessage(LANG, path));
    }

    private static String getIgnorePref(String path, String def) {
        if (messages.get(path) == null) {
            return def;
        } else {
            return messages.get(path);
        }

    }

    public static void loadMessages() {
        file = new File("plugins/OpenGuild/messages_" + LANG.toLowerCase() + ".yml");
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
    public static void setLANG(String lang) {
       LANG = lang;

    }
}
