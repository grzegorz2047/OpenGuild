/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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

package pl.grzegorz2047.openguild2047;

import java.util.List;

/**
 *
 * @author Grzegorz
 */
public class GenConf {
    
    public static String prefix = "§7[§6OpenGuild§7]§7 ";//Mozna pokolorowac
    public static boolean teampvp = false;
    public static boolean homecommand = true;
    public static boolean playerprefixenabled = true;
    public static boolean guildprefixinchat = true; 
    //Max 11, bo prefix jest 16 znakowy - 5 znaków kolorujacych
    public static int maxclantag = 6;
    public static int minclantag = 4;
    public static String colortagu = "§6";
    public static List<String> badwords = OpenGuild.get().getConfig().getStringList("forbiddenguildnames");
    public static List<String> reqitems;
    public static final int MIN_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.min-radius");
    public static final int MAX_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.max-radius");
    public static final int TELEPORT_COOLDOWN = OpenGuild.get().getConfig().getInt("teleport-cooldown");
    public static final boolean EXTRA_PROTECTION = OpenGuild.get().getConfig().getBoolean("cuboid.extra-protection");
    public static final boolean CANENTERAREA = OpenGuild.get().getConfig().getBoolean("cuboid.canenterarea");
    public static final List<String> BREAKING_ITEMS = OpenGuild.get().getConfig().getStringList("cuboid.breaking-blocks.item-types");
    public static final short BREAKING_DAMAGE = Short.parseShort(OpenGuild.get().getConfig().getString("cuboid.breaking-blocks.damage"));
}
