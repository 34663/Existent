package zyx.existent.command;

import zyx.existent.command.commands.*;
import zyx.existent.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CommandManager {
    public static final ArrayList<Command> commandMap = new ArrayList<>();

    /**
     * ここにコマンドを追加していく。
     * ウンコ
     */
    public void setup() {
        // test
        addCommand(new Toggle(new String[]{"t", "toggle", "Toggle"}, "Toggle Modules"));
        addCommand(new Config(new String[]{"config", "Config"}, "Load or Save Configs"));
        addCommand(new SetStrings(new String[]{"setstring", "SetString"}, "Set Strings"));
        addCommand(new Bind(new String[]{"bind"}, "Set ModuleKeybinds"));
        addCommand(new Friend(new String[]{"friend"}, "Add or Delete Friends"));
        addCommand(new Help(new String[]{"help", "h"}, "Help"));
        addCommand(new Register(new String[]{"register"}, "AutoRegister"));
        addCommand(new Counter(new String[]{"counter"}, "SetCounter"));
    }

    public void addCommand(Command command) {
        commandMap.add(command);
    }

    public Collection<Command> getCommands() {
        return commandMap;
    }

    public boolean fire(String text) {
        if (!text.startsWith("-")) {
            return false;
        }
        text = text.substring(1);
        String[] arguments = text.split(" ");
        for (Command cmd : commandMap) {
            if (cmd.getName().equalsIgnoreCase(arguments[0])) {
                String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
                cmd.fire(args);
                return true;
            }
        }
        ChatUtils.printChat("§c Invalid Command!");
        return false;
    }
}
