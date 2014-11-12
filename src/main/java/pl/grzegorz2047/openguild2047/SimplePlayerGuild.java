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
package pl.grzegorz2047.openguild2047;

import java.util.UUID;
import pl.grzegorz2047.openguild2047.api.PlayerGuild;

/**
 *
 * @author Grzegorz
 */
public class SimplePlayerGuild implements PlayerGuild {
    /*
     Ta klasa bedzie potrzebna, ale jak to wszystko 
     jakos logicznie ogarnac to jeszcze nie wiem xd
     */

    private UUID playername;
    private String clantag;
    private boolean leader;

    public SimplePlayerGuild(UUID playername, String tag, boolean leader) {
        this.playername = playername;
        this.clantag = tag;
        this.leader = leader;
    }

    @Override
    public UUID getPlayerUUID() {
        return this.playername;
    }

    @Override
    public String getClanTag() {
        return this.clantag;
    }

    @Override
    public boolean isLeader() {
        return this.leader;
    }

}
