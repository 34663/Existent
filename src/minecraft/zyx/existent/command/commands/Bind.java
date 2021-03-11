package zyx.existent.command.commands;

import org.lwjgl.input.Keyboard;
import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;

public class Bind extends Command {
    public Bind(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 1 && args[0].toLowerCase().equals("resetall")) {
            Existent.getModuleManager().getModules().forEach(m -> m.setKeybind(0));
            NotificationPublisher.queue("Command", "Reset all keybinds.", NotificationType.SUCCESS);
        } else if (args.length >= 2) {
            String moduleName = args[0];
            Module module = Existent.getModuleManager().getString(moduleName);

            if (module != null) {
                int keyCode = Keyboard.getKeyIndex(args[1].toUpperCase());
                if (keyCode != -1) {
                    module.setKeybind(keyCode);
                    ChatUtils.printChatprefix(module.getName() + " is now bound to \"" + Keyboard.getKeyName(keyCode) + "\".");
                    NotificationPublisher.queue("Command", module.getName() + " is now bound to \"" + Keyboard.getKeyName(keyCode) + "\".", NotificationType.SUCCESS);
                } else {
                    ChatUtils.printChatprefix("That is not a valid key code.");
                }
            } else {
                NotificationPublisher.queue("Command", "That module does not exist.", NotificationType.ERROR);
                ChatUtils.printChatprefix("That module does not exist.");
                ChatUtils.printChatprefix("Type \"modules\" for a list of all modules.");
            }
        } else {
            ChatUtils.printChatprefix("Invalid arguments.");
            ChatUtils.printChatprefix("Usage: \"bind [module] [key]\"");
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
