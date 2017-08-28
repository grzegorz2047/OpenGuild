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

package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Command used to check version of this plugin.
 * <p>
 * Usage: /guild version
 */
public class GuildVersionCommand extends Command {
    private final List<Author> authors;
    private final Plugin plugin;

    public GuildVersionCommand(Plugin plugin) {
        this.authors = new ArrayList<Author>();
        this.loadAuthors();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(this.getTitle(ChatColor.GOLD + "OpenGuild2047"));
        sender.sendMessage(ChatColor.DARK_GRAY + "Version: " + ChatColor.GOLD + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.DARK_GRAY + "Authors: \n" + this.getAuthors());
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
        this.authors.add(new Author("grzegorz2047", "Project leader, Coder"));
        this.authors.add(new Author("shooly", "Coder"));
        this.authors.add(new Author("TheMolkaPL", "Coder"));
        this.authors.add(new Author("filippop1", "Tester"));
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