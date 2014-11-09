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
