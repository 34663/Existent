package zyx.existent.command.commands;

import zyx.existent.command.Command;
import zyx.existent.utils.ChatUtils;

public class Register extends Command {
    public Register(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 0) {
            mc.thePlayer.sendChatMessage("/register " + mc.thePlayer.getName() + stringToNumber(mc.thePlayer.getName()) + "@Gmail.com");
        }
    }

    @Override
    public String getUsage() {
        return null;
    }

    public long stringToNumber(String s) {
        long result = 0;

        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            result += (int) ch;
        }

        return result;
    }
}
