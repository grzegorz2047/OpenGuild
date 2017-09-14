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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

/**
 * Command used to see items, which are needed to create a guild.
 * <p>
 * Usage: /guild items
 */
public class GuildItemsCommand extends Command {
    private final Plugin plugin;
    private final Guilds guilds;

    public GuildItemsCommand(String[] aliases, Plugin plugin, Guilds guilds) {
        super(aliases);
        setPermission("openguild.command.items");
        this.plugin = plugin;
        this.guilds = guilds;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (guilds.getRequiredItemsSize() == 0) {
            sender.sendMessage(MsgManager.get("reqitemsoff"));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Player player = (Player) sender;
        Inventory requiredItemsInventory = guilds.prepareItemGuildWindowInventory(player.getInventory());
        player.openInventory(requiredItemsInventory);
    }

    @Override
    public int minArgs() {
        return 1;
    }


}
