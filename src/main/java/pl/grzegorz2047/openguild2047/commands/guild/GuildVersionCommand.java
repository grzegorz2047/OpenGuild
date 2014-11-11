/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.commands.guild;

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Command used to check version of this plugin.
 * 
 * Usage: /guild version
 */
public class GuildVersionCommand extends Command {
    private final List<Author> authors;

    public GuildVersionCommand() {
        this.authors = new ArrayList<Author>();
        this.loadAuthors();
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(this.getTitle(ChatColor.GOLD + "OpenGuild2047"));
        sender.sendMessage(ChatColor.DARK_GRAY + "Version: " + ChatColor.GOLD + this.getPlugin().getDescription().getVersion());
        sender.sendMessage(ChatColor.DARK_GRAY + "Authors: " + this.getAuthors());
        sender.sendMessage(ChatColor.DARK_GRAY + "GitHub: " + ChatColor.GOLD + "https://github.com/grzegorz2047/OpenGuild2047");
        sender.sendMessage(ChatColor.DARK_GRAY + "BukkitDev: " + ChatColor.GOLD + "http://dev.bukkit.org/bukkit-plugins/openguild");
        sender.sendMessage(ChatColor.DARK_GRAY + "MCStats: " + ChatColor.GOLD + "http://mcstats.org/plugin/OpenGuild2047");
    }

    @Override
    public int minArgs() {
        return 1;
    }

    private String getAuthors() {
        StringBuilder builder = new StringBuilder();
        for (Author author : this.authors) {
            builder.append(author.getMessage()).append("\n");
        }
        return builder.toString();
    }

    private void loadAuthors() {
        this.authors.add(new Author("filippop1", "Tester"));
        this.authors.add(new Author("grzegorz2047", "Project leader"));
        this.authors.add(new Author("shooly", "Coder"));
        this.authors.add(new Author("TheMolkaPL", "Coder"));
    }

    private class Author {
        private final String author;
        private final String description;

        public Author(String author, String description) {
            this.author = author;
            this.description = description;
        }

        public String getAuthor() {
            return this.author;
        }

        public String getDescription() {
            return this.description;
        }

        public String getMessage() {
            return ChatColor.GREEN + "@" + this.getAuthor() + ChatColor.GRAY + " - " + this.getDescription() + ChatColor.RESET;
        }
    }
}
