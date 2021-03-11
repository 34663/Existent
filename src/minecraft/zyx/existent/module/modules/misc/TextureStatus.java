package zyx.existent.module.modules.misc;

import net.minecraft.network.play.client.CPacketResourcePackStatus;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class TextureStatus extends Module {
    private String MODE = "MODE";

    public TextureStatus(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Success", new String[] {"Success", "Accept", "Declined", "Failed"}), "TextureStatus method."));
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        String currentmode = ((Options)settings.get(MODE).getValue()).getSelected();

        if (e.getPacket() instanceof CPacketResourcePackStatus) {
            CPacketResourcePackStatus p = (CPacketResourcePackStatus) e.getPacket();
            switch (currentmode) {
                case "Success":
                    p.action = CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED;
                    break;
                case "Accept":
                    p.action = CPacketResourcePackStatus.Action.ACCEPTED;
                    break;
                case "Declined":
                    p.action = CPacketResourcePackStatus.Action.DECLINED;
                    break;
                case "Failed":
                    p.action = CPacketResourcePackStatus.Action.FAILED_DOWNLOAD;
                    break;
            }
        }
    }
}
