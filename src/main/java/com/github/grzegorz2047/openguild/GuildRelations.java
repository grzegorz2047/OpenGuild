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
package com.github.grzegorz2047.openguild;

import java.util.ArrayList;
import java.util.List;

public class GuildRelations {
    
    protected final pl.grzegorz2047.openguild2047.OpenGuild plugin;
    private Guild guild;

    private List<Guild> alliances = new ArrayList<Guild>();
    private List<Guild> enemies = new ArrayList<Guild>();

    public GuildRelations(pl.grzegorz2047.openguild2047.OpenGuild plugin) {
        this.plugin = plugin;
    }

    protected void setRelationsGuild(Guild guild) {
        this.guild = guild;
    }

    public void setAlliances(List<Guild> alliances) {
        this.alliances = alliances;
    }

    public List<Guild> getAlliances() {
        return alliances;
    }

    public void setEnemies(List<Guild> enemies) {
        this.enemies = enemies;
    }

    public List<Guild> getEnemies() {
        return enemies;
    }
}
