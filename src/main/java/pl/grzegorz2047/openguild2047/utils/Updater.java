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

package pl.grzegorz2047.openguild2047.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import pl.grzegorz2047.openguild2047.api.Guilds;

public class Updater {
    
    private static String address;
    private static Scanner scanner;
    private static List<String> versions;
    
    public Updater() {
        this.address = "https://raw.githubusercontent.com/grzegorz2047/OpenGuild2047/master/updater.txt";
    }
    
    public List<String> getVersions() {
        if(versions == null)
            read();
        return versions;
    }
    
    private Scanner getScanner() {
        try {
            URL url = new URL(this.address);
            Guilds.getLogger().info("Checking for updates...");
            Scanner sc = new Scanner(url.openStream());
            return sc;
        } catch(MalformedURLException ex) {} catch(IOException ex) {}
        return null;
    }
    
    private void read() {
        scanner = getScanner();
        if(scanner != null) {
            versions = new ArrayList<String>();
            while(scanner.hasNext()) {
                String version = scanner.next();
                versions.add(version);
            }
        }
    }
    
    public static void reset() {
        versions = null;
    }
    
}
