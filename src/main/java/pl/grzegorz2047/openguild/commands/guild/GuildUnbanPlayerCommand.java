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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.commands.command.UsageException;
import pl.grzegorz2047.openguild.hardcore.HardcoreHandler;
import pl.grzegorz2047.openguild.hardcore.HardcoreSQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;

public class GuildUnbanPlayerCommand extends Command {

    private final HardcoreSQLHandler hardcoreSQLHandler;
    private final HardcoreHandler hardcoreHandler;

    public GuildUnbanPlayerCommand(HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler) {
        setPermission("openguild.command.hardcore.unban");
        this.hardcoreSQLHandler = hardcoreSQLHandler;
        this.hardcoreHandler = hardcoreHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!hardcoreHandler.isEnabled()) {
            throw new CommandException(MsgManager.get("hcnotenabled"));
        } else if (args.length != 2) {
            throw new UsageException(MsgManager.get("wrongcmdargument"));
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (player == null || !player.hasPlayedBefore()) {
            throw new CommandException(MsgManager.get("notplayedbefore").replace("{PLAYER}", args[1]));
        }

        long banned = hardcoreSQLHandler.getBan(player.getUniqueId());
        if (banned != 0) {
            hardcoreSQLHandler.update(player.getUniqueId(), HardcoreSQLHandler.Column.BAN_TIME, "0");
            OpenGuild.getOGLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") was unbanned by " + sender.getName());
            sender.sendMessage(MsgManager.get("hcub").replace("{PLAYER}", player.getName()));
        } else {
            throw new CommandException(MsgManager.get("notbanned").replace("{PLAYER}", player.getName()));
        }
    }

    @Override
    public int minArgs() {
        return 2;
    }

}