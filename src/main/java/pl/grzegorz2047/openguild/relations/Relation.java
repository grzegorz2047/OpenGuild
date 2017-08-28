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
package pl.grzegorz2047.openguild.relations;

/**
 * @author Grzegorz
 */
public class Relation {

    private String baseGuild;
    private String alliedGuildTag;
    private long startDate;
    private long expireDate;
    public enum Status {ALLY, ENEMY}
    private Status state;

    public Relation(String baseGuild, String alliedGuildTag, long expires, Status relationStatus) {
        this.baseGuild = baseGuild;
        this.alliedGuildTag = alliedGuildTag;
        this.expireDate = expires;
        this.state = relationStatus;
    }

    public String getBaseGuildTag() {
        return baseGuild;
    }

    public String getAlliedGuildTag() {
        return alliedGuildTag;
    }

    public void setAlliedGuildTag(String alliedGuildTag) {
        this.alliedGuildTag = alliedGuildTag;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status state) {
        this.state = state;
    }


}
