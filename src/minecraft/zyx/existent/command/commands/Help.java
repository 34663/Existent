package zyx.existent.command.commands;

import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.utils.ChatUtils;

public class Help extends Command {
    public Help(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 0) {
            ChatUtils.printChat("---------------- \247cHelp \247f----------------");
            for (Command command : Existent.getCommandManager().getCommands()) {
                ChatUtils.printChat(" -" + command.getName() + " : \2477" + command.getDescription());
            }
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
