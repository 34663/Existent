package zyx.existent.command.commands;

import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.module.modules.hud.HUD;
import zyx.existent.module.modules.misc.AutoL;
import zyx.existent.module.modules.misc.Spammer;
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.utils.ChatUtils;

public class SetStrings extends Command {
    public SetStrings(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 0) {
            ChatUtils.printChat("\2477== \2476SetStringHelp \2477==");
            ChatUtils.printChat(" -setstring ClientName <Value>");
            ChatUtils.printChat(" -setstring Spam <Value>");
            ChatUtils.printChat(" -setstring StreamName <Value>");
            ChatUtils.printChat(" -setstring AutoL <Value>");
        } else if (args.length == 1) {
            if (args[0].toLowerCase().equalsIgnoreCase("clientname")) {
                ChatUtils.printChatprefix("-setstring clientname <Value>");
            } else if (args[0].toLowerCase().equalsIgnoreCase("spam")) {
                ChatUtils.printChatprefix("-setstring spam <Value>");
            } else if (args[0].toLowerCase().equalsIgnoreCase("streamname")) {
                ChatUtils.printChatprefix(" -setstring streamname <Value>");
            } else if (args[0].toLowerCase().equalsIgnoreCase("autol")) {
                ChatUtils.printChatprefix(" -setstring autol <Value>");
            }
        } else {
            String value = "";

            if (args.length == 2) {
                value = args[1];
            } else if (args.length == 3) {
                value = args[1] + " " + args[2];
            } else if (args.length == 4) {
                value = args[1] + " " + args[2] + " " + args[3];
            }

            if (args[0].equalsIgnoreCase("clientname")) {
                HUD hud = (HUD) Existent.getModuleManager().getClazz(HUD.class);

                hud.getSetting(HUD.CLIENTTEXT).setValue(value);
                ChatUtils.printChatprefix("Set ClientName: " + value);
            } else if (args[0].equalsIgnoreCase("spam")) {
                Spammer spammer = (Spammer) Existent.getModuleManager().getClazz(Spammer.class);

                spammer.getSetting(Spammer.MESSAGE).setValue(value);
                ChatUtils.printChatprefix("Set Message: " + value);
            } else if (args[0].equalsIgnoreCase("streamname")) {
                StreamerMode streamerMode = (StreamerMode) Existent.getModuleManager().getClazz(StreamerMode.class);

                streamerMode.getSetting(StreamerMode.YOURNAME).setValue(value);
                ChatUtils.printChatprefix("Set YouName: " + value);
            } else if (args[0].toLowerCase().equalsIgnoreCase("autol")) {
                AutoL autoL = (AutoL) Existent.getModuleManager().getClazz(AutoL.class);

                autoL.getSetting(AutoL.MESSAGE).setValue(value);
                ChatUtils.printChatprefix("Set Message: " + value);
            }
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
