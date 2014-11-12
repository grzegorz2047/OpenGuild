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

package com.github.grzegorz2047.openguild.module;

public class ModuleInfo {

    private final String name;
    private final String desc;
    private final String version;

    public ModuleInfo(String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.version = "1.0";
    }

    public ModuleInfo(String name, String desc, String version) {
        this.name = name;
        this.desc = desc;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public String getVersion() {
        return version;
    }

}
