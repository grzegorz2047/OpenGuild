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

package pl.grzegorz2047.openguild2047.api;

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.interfaces.PluginUpdater;
import java.util.List;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.utils.Updater;

public class OpenPluginUpdater implements PluginUpdater {
    
    private static final Updater updater = new Updater();
    private static boolean available;
    private static boolean checked = false;
    
    @Override
    public List<String> getVersions() {
        return updater.getVersions();
    }
    
    @Override
    public boolean isAvailable() {
        if(!checked)
            checkForUpdates();
        return available;
    }
    
    @Override
    public boolean isEnabled() {
        return GenConf.updater;
    }
    
    private void checkForUpdates() {
        available = true;
        for(String version : getVersions()) {
            if(version.equals(OpenGuild.getVersion())) {
                available = false;
            }
        }
        checked = true;
    }
    
}
