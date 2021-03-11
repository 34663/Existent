package zyx.existent.module.modules.misc;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.timer.Timer;

public class AntiImmobilizer extends Module {
    private final String AISPEED = "AI Speed";

    public AntiImmobilizer(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(AISPEED, new Setting(AISPEED, 0.13, "AI Speed.", 0.01, 0.01, 1.0));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(2)) && mc.thePlayer.onGround && mc.thePlayer.getActivePotionEffect(Potion.getPotionById(2)).getDuration() < 10000) {
            Potion.getPotionById(2).removeAttributesModifiersFromEntity(mc.thePlayer, mc.thePlayer.getAttributeMap(), 255);
            mc.thePlayer.setAIMoveSpeed(((Number) settings.get(AISPEED).getValue()).floatValue());

            for (int i = 0; i < mc.thePlayer.getActivePotionEffect(Potion.getPotionById(2)).getDuration() / 20; ++i) {
                mc.getConnection().sendPacketSilent(new CPacketPlayer(mc.thePlayer.onGround));
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof SPacketEntityEffect) {
            SPacketEntityEffect entityEffect = (SPacketEntityEffect) event.getPacket();

            if (entityEffect.getEntityId() == mc.thePlayer.getEntityId()) {
                if (entityEffect.getEffectId() == 2 && entityEffect.getDuration() < 10000) {
                    ChatUtils.printChatprefix("Immoの刑");
                }
            }
        }
    }
}
