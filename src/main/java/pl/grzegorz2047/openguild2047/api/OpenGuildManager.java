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

import com.github.grzegorz2047.openguild.interfaces.GuildManager;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

public class OpenGuildManager implements GuildManager {
    
    private static String tag;
    
    @Override
    public String getNicknameTag() {
        if(tag == null) {
            setNicknameTag(GenConf.nicknameTag);
        }
        return tag;
    }
    
    @Override
    public void setNicknameTag(String tag) {
        if(tag.length() > 15) {
            throw new IllegalArgumentException("Tag gildii jest za dlugi! " + tag.length() + "/15");
        } else {
            OpenGuildManager.tag = tag.replace('&', '§').toUpperCase();
        }
    }
    
}
