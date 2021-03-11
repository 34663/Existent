package zyx.existent.module.modules.visual;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUseEntity;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketSend;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

import java.util.Random;

public class Crack extends Module {
    private String ENCHANT = "ENCHANT";
    private String CRITICALS = "CRITICALS";
    private String BLOOD = "BLOOD";
    private String SIZE = "CRACK SIZE";

    public Crack(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ENCHANT, new Setting(ENCHANT, false, ""));
        settings.put(CRITICALS, new Setting(CRITICALS, false, ""));
        settings.put(BLOOD, new Setting(BLOOD, false, ""));
        settings.put(SIZE, new Setting(SIZE, 1, "Crack Size.", 1, 1, 10.0));
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                for (int i = 0; i < ((Number) settings.get(SIZE).getValue()).intValue(); i++) {
                    if ((Boolean) settings.get(ENCHANT).getValue())
                        mc.thePlayer.onEnchantmentCritical(packet.getEntity());
                    if ((Boolean) settings.get(CRITICALS).getValue())
                        mc.thePlayer.onCriticalHit(packet.getEntity());
                    if ((Boolean) settings.get(BLOOD).getValue()) {
                        Random random = new Random();
                        Entity target = packet.getEntity();
                        double i22;
                        for (i22 = 0.0D; i22 < target.height; i22 += 0.2D) {
                            for (int i1 = 0; i1 < 9; i1++) {
                                mc.effectRenderer.spawnEffectParticle(37, target.posX, target.posY + i22, target.posZ, ((random.nextInt(6) - 3) / 5), 0.1D, ((random.nextInt(6) - 3) / 5), Block.getIdFromBlock(Blocks.REDSTONE_BLOCK));
                            }
                        }
                    }
                }
            }
        }
    }
}
