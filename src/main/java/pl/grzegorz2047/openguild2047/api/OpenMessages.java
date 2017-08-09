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

import com.github.grzegorz2047.openguild.Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class OpenMessages implements Messages {
    
    private static List<String> langs;
    
    @Override
    public String getErrorMessage(String lang, String path) {
        return MsgManager.getNullMessage(lang.toUpperCase(), path);
    }
    
    @Override
    public List<String> getLangList() {
        if(langs == null)
            load();
        return langs;
    }
    
    @Override
    public String getMessage(String path) {
        return MsgManager.getIgnorePref(path);
    }
    
    @Override
    public String getPrefixedMessage(String path) {
        return MsgManager.get(path);
    }
    
    @Override
    public void reload() {
        langs = null;
        load();
    }
    
    private void load() {
        langs = new ArrayList<String>();
        for(File lang : new File("plugins/OpenGuild2047").listFiles()) {
            if(lang.getName().startsWith("messages_") && lang.getName().endsWith(".yml")) {
                langs.add(lang.getName().substring(9, 11));
            }
        }
    }
    
}
