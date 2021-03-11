package zyx.existent.module.modules.misc;

import net.minecraft.network.play.client.CPacketChatMessage;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

import java.util.ArrayList;

public class ChatFilter extends Module {
    public static String OBF = "OBFSUCATOR";
    private String BYPASS = "BYPASS";

    public ChatFilter(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(BYPASS, new Setting(BYPASS, true, "ChatBypass."));
        settings.put(OBF, new Setting(OBF, false, "ChatObfsucator."));
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if ((Boolean) settings.get(BYPASS).getValue()) {
            if (event.isOutgoing() && event.getPacket() instanceof CPacketChatMessage) {
                final CPacketChatMessage c01PacketChatMessage = (CPacketChatMessage) event.getPacket();
                String string = "";
                final ArrayList<String> list = new ArrayList<String>();
                final String[] split = c01PacketChatMessage.getMessage().split(" ");
                for (int i = 0; i < split.length; ++i) {
                    final char[] charArray = split[i].toCharArray();
                    for (int j = 0; j < charArray.length; ++j) {
                        list.add(charArray[j] + "\u061c");
                    }
                    list.add(" ");
                }
                for (int k = 0; k < list.size(); ++k) {
                    string += list.get(k);
                }
                if (c01PacketChatMessage.getMessage().startsWith("%")) {
                    c01PacketChatMessage.setMessage(string.replaceFirst("%", ""));
                    list.clear();
                }
            }
        }
    }
}
