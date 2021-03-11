package zyx.existent.module.modules.misc;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

public class StreamerMode extends Module {
    public static final String SPOOFSKINS = "Spoof Skins";
    public static final String SCRAMBLE = "Scramble Names";
    public static final String SERVERNAME = "ServerProtection";
    public static final String YOURNAME = "YourName";
    public static String NAMEPROTECT = "NameProtection";
    public static String name;
    public static List<String> strings = new ArrayList<>();

    public StreamerMode(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(NAMEPROTECT, new Setting(NAMEPROTECT, false, "Protect your name."));
        settings.put(SPOOFSKINS, new Setting(SPOOFSKINS, false, "Spoofs player skins."));
        settings.put(SCRAMBLE, new Setting(SCRAMBLE, false, ""));
        settings.put(SERVERNAME, new Setting(SERVERNAME, false, ""));
        settings.put(YOURNAME, new Setting(YOURNAME, "You", ""));
    }

    @Override
    public void onDisable() {
        strings.clear();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            final NetHandlerPlayClient var4 = mc.thePlayer.connection;
            final List<?> players = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(var4.getPlayerInfoMap());
            for (final Object o : players) {
                final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
                if (info == null) {
                    break;
                }
                if (!strings.contains(info.getGameProfile().getName())) {
                    strings.add(info.getGameProfile().getName());
                }
            }
            for (Object o : mc.theWorld.getLoadedEntityList()) {
                if (o instanceof EntityPlayer) {
                    if (!strings.contains(((EntityPlayer) o).getName())) {
                        strings.add(((EntityPlayer) o).getName());
                    }
                }
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        name = (String) settings.get(YOURNAME).getValue();

        if (event.isIncoming() && event.getPacket() instanceof SPacketChat && (Boolean) settings.get(NAMEPROTECT).getValue()) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            if (packet.getChatComponent().getUnformattedText().contains(mc.thePlayer.getName())) {
                String temp = packet.getChatComponent().getFormattedText();
                ChatUtils.printChat(temp.replaceAll(mc.thePlayer.getName(), "\247d" + name + "\247r"));
                event.setCancelled(true);
            } else {
                String[] list = new String[]{"join", "left", "leave", "leaving", "lobby", "server", "fell", "died", "slain", "burn", "void", "disconnect", "kill", "by", "was", "quit", "blood", "game"};
                for (String str : list) {
                    if (packet.getChatComponent().getUnformattedText().toLowerCase().contains(str)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }
}
