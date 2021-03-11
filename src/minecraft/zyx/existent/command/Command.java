package zyx.existent.command;

import net.minecraft.util.text.TextFormatting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MCUtil;

import java.util.Arrays;

public abstract class Command implements MCUtil {
    private final String[] names;
    private final String description;

    public Command(String[] names, String description) {
        this.names = names;
        this.description = description;
    }

    public abstract void fire(String[] args);

    protected void printDescription() {
        String message = getName() + TextFormatting.GRAY + " : " + description;
        ChatUtils.printChatprefix(message);
    }

    protected void printUsage() {
        String message = getName() + TextFormatting.GRAY + " : " + getUsage();
        ChatUtils.printChatprefix(message);
    }

    public abstract String getUsage();

    public String getName() {
        return names[0];
    }

    public boolean isMatch(String text) {
        return Arrays.asList(names).contains(text.toLowerCase());
    }

    public String getDescription() {
        return description;
    }
}