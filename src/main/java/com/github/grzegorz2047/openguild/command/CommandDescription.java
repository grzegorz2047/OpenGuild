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

package com.github.grzegorz2047.openguild.command;

import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandDescription {

    private final HashMap<String, String> list = new HashMap<String, String>();
    private String desc;

    public String get(@Nonnull String lang) {
        if(desc != null)
            return desc;
        else
            return list.get(lang);
    }

    public boolean has(@Nonnull String lang) {
        return desc != null || list.containsKey(lang.toUpperCase());
    }

    public void set(@Nullable String description) {
        desc = description;
    }

    public void set(@Nonnull String lang, @Nullable String description) {
        list.remove(lang.toUpperCase());
        list.put(lang.toUpperCase(), description);
    }

}
