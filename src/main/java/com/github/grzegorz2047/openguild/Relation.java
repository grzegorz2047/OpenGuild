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
package com.github.grzegorz2047.openguild;

/**
 * @author Grzegorz
 */
public class Relation {

    private String who;
    private String withWho;
    private long startDate;
    private long expireDate;

    public Relation(String who, String withwho, long expires, Status relationStatus) {
        this.who = who;
        this.withWho = withwho;
        this.expireDate = expires;
        this.state = relationStatus;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public enum Status {ALLY, ENEMY}

    ;
    private Status state;

    public String getWithWho() {
        return withWho;
    }

    public void setWithWho(String withWho) {
        this.withWho = withWho;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status state) {
        this.state = state;
    }


}
