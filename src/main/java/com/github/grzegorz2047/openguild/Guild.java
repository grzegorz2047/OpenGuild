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

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public interface Guild {

    List<String> getAllyGuilds(); // Dlaczego nie obiekt Guild?

    String getDescription();

    List<String> getEnemyGuilds(); // Dlaczego nie obiekt Guild?

    Location getHome();

    List<UUID> getInvitedPlayers();

    UUID getLeader();

    List<UUID> getMembers();

    double getPoints();

    String getTag();

    void reloadPoints();

    void setDesciption(String description);

    void setHome(Location home);

    void setInvitedPlayers(List<UUID> invited);

    void setLeader(UUID leader);

    void setMembers(List<UUID> members);

    void setTag(String tag);

}