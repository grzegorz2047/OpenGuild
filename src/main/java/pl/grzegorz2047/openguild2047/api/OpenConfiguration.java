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

import com.github.grzegorz2047.openguild.Configuration;
import java.util.HashMap;

public class OpenConfiguration implements Configuration {
    
    private static HashMap<String, Object> values;
    
    @Override
    public Object getValue(String path) {
        return getValue(path, null);
    }
    
    @Override
    public Object getValue(String path, Object def) {
        if(values == null) {
            load();
        }
        return values.get(path);
    }
    
    @Override
    public void reload() {
        values = null;
        load();
    }
    
    private void load() {
        values = new HashMap<String, Object>();
        // TODO load configuration
    }
    
}
