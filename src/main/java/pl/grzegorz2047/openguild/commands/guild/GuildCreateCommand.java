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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.events.guild.GuildCreateEvent;
import pl.grzegorz2047.openguild.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.utils.GenUtil;

import java.util.List;
import java.util.UUID;

/**
 * Command used to create new guild.
 * <p>
 * Usage: /guild create [tag] [description]
 */
public class GuildCreateCommand extends Command {
    private final Guilds guilds;
    private List<String> badWords;
    private boolean playerGuildTagsEnabled;

    public GuildCreateCommand(String[] aliases, Cuboids cuboids, Guilds guilds, SQLHandler sqlHandler, TagManager tagManager, FileConfiguration config) {
        super(aliases);
        setPermission("openguild.command.create");
        this.guilds = guilds;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;

        String tag = args[1].toUpperCase();
        String description = GenUtil.argsToString(args, 2, args.length);
        boolean descriptionAdded = isDescriptionAdded(args.length);
        guilds.createGuild(player, tag, description, descriptionAdded);
    }

    private boolean isDescriptionAdded(int argsLength) {
        return argsLength >= 3;
    }


    @Override
    public int minArgs() {
        return 2;
    }

}
