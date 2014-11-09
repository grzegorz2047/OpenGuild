
package pl.grzegorz2047.openguild2047.commands;

import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.OpenGuild;

public abstract class CommandHandler {
    private final OpenGuild plugin;
    
    public CommandHandler(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    public abstract void executeCommand(CommandSender sender, String[] args);
    public abstract int getMinimumArguments();
    
    public OpenGuild getPlugin() {
        return this.plugin;
    }
}
