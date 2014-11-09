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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used to reload guilds.
 * 
 * Usage: /guild reload
 */
public class GuildReloadCommand extends CommandHandler {

    public GuildReloadCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("openguild.admin.reload") || !sender.isOp()) {
            sender.sendMessage(MsgManager.get("permission"));
            return;
        }
        
        try {
            this.getPlugin().getConfig().load(this.getPlugin().getConfig().getCurrentPath());
            sender.sendMessage(MsgManager.get("configreloaded"));
        } catch (FileNotFoundException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        } catch (IOException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        } catch (InvalidConfigurationException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        }
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

}
