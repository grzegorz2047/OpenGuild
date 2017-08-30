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

package pl.grzegorz2047.openguild.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.OpenGuild;

public class HardcoreHandler {


    private final HardcoreSQLHandler hardcoreSQLHandler;
    private final Plugin plugin;
    private String hardcoreBansKickMessage;
    private String hadcoreBansLoginBannedMessage;
    private long hardcoreBansTime;
    private boolean enabled;

    public HardcoreHandler(HardcoreSQLHandler hardcoreSQLHandler, Plugin plugin) {
        this.hardcoreSQLHandler = hardcoreSQLHandler;
        this.plugin = plugin;
    }

    public void loadBans(boolean bansEnabled, String hardcoreBansKickMessage, String hadcoreBansLoginBannedMessage, String banTime) {
        if (!bansEnabled) {
            return;
        }
        enabled = bansEnabled;
        hardcoreSQLHandler.createTables();
        Bukkit.getPluginManager().registerEvents(new HardcoreListeners(hardcoreSQLHandler, this, plugin), plugin);
        this.hardcoreBansKickMessage = hardcoreBansKickMessage;
        this.hadcoreBansLoginBannedMessage = hadcoreBansLoginBannedMessage;

        String length = banTime.substring(0, banTime.length() - 1);
        long result;
        try {
            result = Long.parseLong(length);
        } catch (NumberFormatException ex) {
            OpenGuild.getOGLogger().warning("Could not load ban time, defaults using 1 minute. Check your ban-time in config.yml file.");
            hardcoreBansTime = 60 * 1000;
            return;
        }
        if (banTime.endsWith("s")) { // Seconds
            result = result * 1000;
        } else if (banTime.endsWith("m")) { // Minutes
            result = result * 60 * 1000;
        } else if (banTime.endsWith("h")) { // Hours
            result = result * 60 * 60 * 1000;
        } else if (banTime.endsWith("d")) { // Days
            result = result * 60 * 24 * 60 * 1000;
        }
        hardcoreBansTime = result;

    }

    public String getHardcoreBansKickMessage() {
        return hardcoreBansKickMessage;
    }

    public String getHadcoreBansLoginBannedMessage() {
        return hadcoreBansLoginBannedMessage;
    }

    public void setHadcoreBansLoginBannedMessage(String hadcoreBansLoginBannedMessage) {
        this.hadcoreBansLoginBannedMessage = hadcoreBansLoginBannedMessage;
    }

    public long getHardcoreBansTime() {
        return hardcoreBansTime;
    }

    public void setHardcoreBansTime(long hardcoreBansTime) {
        this.hardcoreBansTime = hardcoreBansTime;
    }

    public boolean isEnabled() {
        return enabled;
    }
}