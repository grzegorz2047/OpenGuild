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

package pl.grzegorz2047.openguild2047.managers;

import pl.grzegorz2047.openguild2047.GenConf;

/**
 *
 * @author Grzegorz
 */
public class MsgManager {
    
    public static String createguildsuccess = "Gildia zostala pomysnie stworzona.";
    public static String cmdonlyforplayer = "Komenda jedynie dla graczy na serwerze!";
    public static String alreadyinguild = "Aby stworzyc nowa gildie musisz najpierw opuscic aktualna!";
    public static String usupportedchars = "Nazwa gildii moze zawierac jedynie znaki z tego zakresu [0-9a-zA-Z]";
    public static String toolongshorttag = "Twoja nazwa gildii jest za dluga badz za krotka!"
            + "\n min. ilosc: "+GenConf.minclantag
            + "\n max. ilosc: "+GenConf.maxclantag;
    public static String illegaltag = "Twoja nazwa posiada zabronione slowa!";
    public static String notinguild = "Aby to wykonac musisz byc w gildii!";
    public static String errornotinguild = "Nie jestes w gildii?! Czyszcze bledne dane!";
    public static String teleportsuccess = "Zostales pomyslnie przeteleportowany!";
    public static String leaveguildsuccess = "Pomyslnie opusciles gildie!";
    public static String nomembersinguild = "Aktualnie nie posiadasz zadnych czlonkow w gildii.";
    public static String notenoughitems = "Posiadasz nie wystarczaczajaca liczbe przedmiotow na stworzenie gildii";
    public static String homenotenabled = "Na tym serwerze komenda /gildia dom jest wylaczona!";
    public static String wrongcmdargument = "Podales bledny argument!";
    public static String wronguseddesccmd = "Poprawne uzycie: /gildia dom zmien <Tresc>";
    public static String playernotleader = "Nie mozesz tego wykonac, poniewaz nie jestes liderem gildii!";
}
