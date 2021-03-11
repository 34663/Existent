package zyx.existent.command.commands;

import zyx.existent.command.Command;
import zyx.existent.module.modules.visual.KillCounter;
import zyx.existent.utils.ChatUtils;

public class Counter extends Command {
    public Counter(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 0) {
            ChatUtils.printChatprefix("-Counter reset");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                KillCounter.setCount(0);
                ChatUtils.printChatprefix("Reset KillCounter");
            }
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
