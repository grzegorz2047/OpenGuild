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

package pl.grzegorz2047.openguild2047.api;

import com.github.grzegorz2047.openguild.Messages;
import java.util.ArrayList;
import java.util.List;
import pl.grzegorz2047.openguild2047.GenConf.Lang;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class OpenMessages implements Messages {
    
    private static List<String> langs;
    
    @Override
    public String getErrorMessage(String lang) {
        return MsgManager.getNullMessage(Lang.valueOf(lang.toUpperCase()));
    }
    
    @Override
    public List<String> getLangList() {
        if(langs == null) {
            load();
        }
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
        for(Lang lang : Lang.values()) {
            langs.add(lang.name());
        }
    }
    
}
