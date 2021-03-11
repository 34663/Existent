package zyx.existent.utils;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils implements MCUtil {
    public final static String chatPrefix = "\2477[\2476Ex\2477] \2478>> \247f";
    public final static String ircchatPrefix = "\2477[\2476Ex\2479IRC\2477] \247f";

    public static void printChat(String text) {
        mc.thePlayer.addChatMessage(new TextComponentString(text));
    }

    public static void printChatprefix(String text) {
        mc.thePlayer.addChatMessage(new TextComponentString(chatPrefix + text));
    }

    public static void printIRCChatprefix(String text) {
        mc.thePlayer.addChatMessage(new TextComponentString(ircchatPrefix + text));
    }

    public static void sendChat_NoFilter(String text) {
        mc.thePlayer.connection.sendPacket(new CPacketChatMessage(text));
    }

    public static void sendChat(String text) {
        mc.thePlayer.sendChatMessage(text);
    }
}
