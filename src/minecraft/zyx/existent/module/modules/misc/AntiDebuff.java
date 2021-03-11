package zyx.existent.module.modules.misc;

import net.minecraft.init.PotionTypes;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class AntiDebuff extends Module {
    public AntiDebuff(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            if (mc.thePlayer.isOnLiquid() || mc.thePlayer.isInLiquid()) {
                return;
            }
            if (mc.thePlayer.isPotionActive(Potion.getPotionById(15))) {
                mc.thePlayer.removePotionEffect(Potion.getPotionById(15));
            }
            if (mc.thePlayer.isPotionActive(Potion.getPotionById(9))) {
                mc.thePlayer.removePotionEffect(Potion.getPotionById(9));
            }
            if (mc.thePlayer.isPotionActive(Potion.getPotionById(4))) {
                mc.thePlayer.removePotionEffect(Potion.getPotionById(4));
            }
            if (mc.thePlayer.isBurning() && mc.thePlayer.isCollidedVertically) {
                for (int i = 0; i < 12; ++i) {
                    mc.thePlayer.connection.sendPacket(new CPacketPlayer(true));
                }
            }
            for (int j = 0; j < 27; ++j) {
                final Potion potion = Potion.REGISTRY.getObjectById(j);
                if (potion != null && potion.isBadEffect() && mc.thePlayer.isPotionActive(potion)) {
                    final PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(potion);
                    for (int k = 0; k < activePotionEffect.getDuration() / 20; ++k) {
                        mc.thePlayer.connection.sendPacket(new CPacketPlayer(true));
                    }
                }
            }
        }
    }
}
