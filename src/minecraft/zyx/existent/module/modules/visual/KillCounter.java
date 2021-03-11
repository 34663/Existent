package zyx.existent.module.modules.visual;

import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextFormatting;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.modules.hud.HUD;
import zyx.existent.module.modules.hud.TabGui;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class KillCounter extends Module {
    private static int count = 0;

    public KillCounter(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        CFontRenderer font = Fonts.elliot17;
        int y = font.getHeight() + 11;

        if (Existent.getModuleManager().isEnabled(TabGui.class))
            y += 70;
        font.drawStringWithShadow("KillCount: " + count, 4, y, -1);
    }
    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (event.getPacket() != null && event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());
            if (message != null && message.length() > 0) {
                String[] text = message.split(" ");
                if ((text[1].equalsIgnoreCase("killed") || text[1].equalsIgnoreCase("shot")) && text[0].startsWith(mc.thePlayer.getName() + "(")) {
                    count++;
                }
            }
        }
    }

    public static void setCount(int negro) {
        count = negro;
    }
}
