package zyx.existent.command.commands;

import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;

public class Toggle extends Command {
    public Toggle(String[] names, String description) {
        super(names, description);
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public void fire(String[] args) {
        if (args == null) {
            ChatUtils.printChatprefix("t <module>");
            return;
        }
        Module module = null;
        if (args.length > 0) {
            module = Existent.getModuleManager().getString(args[0]);
        }
        if (module == null) {
            ChatUtils.printChatprefix("Module notfound.");
            return;
        }
        if (args.length == 1) {
            module.toggle();
            ChatUtils.printChatprefix(module.getName() + " has been" + (module.isEnabled() ? "\247a Enabled." : "\247c Disabled."));
        }
    }
}
