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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.events.guild.GuildHomeTeleportEvent;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.teleporters.Teleporter;

/**
 * Command used by players to teleport to their guild home.
 * <p>
 * Usage: /guild home
 */
public class GuildHomeCommand extends Command {

    private final Guilds guilds;
    private final int TELEPORT_COOLDOWN;
    private Teleporter teleporter;


    public GuildHomeCommand(String[] aliases, Teleporter teleporter, Guilds guilds, FileConfiguration config) {
        super(aliases);
        setPermission("openguild.command.home");
        this.teleporter = teleporter;
        this.guilds = guilds;
        TELEPORT_COOLDOWN = config.getInt("teleport-cooldown", 10);

    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        final Player player = (Player) sender;
        if (!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("notinguild"));
            return;
        }

        Guild guild = guilds.getPlayerGuild(player.getUniqueId());

        GuildHomeTeleportEvent event = new GuildHomeTeleportEvent(guild, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        teleporter.addRequest(player.getUniqueId(), player.getLocation(), guild.getHome(), TELEPORT_COOLDOWN);
        player.sendMessage(ChatColor.GRAY + MsgManager.get("timetotpnotify").replace("{GUILD}", guild.getName().toUpperCase()).replace("{HOMETPSECONDS}", String.valueOf(TELEPORT_COOLDOWN)));

    }

    @Override
    public int minArgs() {
        return 1;
    }

}
