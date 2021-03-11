package zyx.existent.module.modules.combat;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class Regen extends Module {
    private String MODE = "MODE";
    private String PACKET = "PACKET";

    public Regen(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Packet", new String[]{"Packet", "Packet2", "Potion"}), "Regen method"));
        settings.put(PACKET, new Setting(PACKET, 10.0, "PacketSize.", 0.1, 1.0, 1000.0));
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Packet":
                packetRegen(((Number) settings.get(PACKET).getValue()).intValue());
                break;
            case "Packet2":
                if (!this.mc.thePlayer.capabilities.isCreativeMode && this.mc.thePlayer.getFoodStats().getFoodLevel() > 17 && this.mc.thePlayer.getHealth() < this.mc.thePlayer.getMaxHealth() && this.mc.thePlayer.getHealth() != 0.0F) {
                    for (int i = 0; i < ((Number) settings.get(PACKET).getValue()).intValue(); i++) {
                        this.mc.getConnection().sendPacket(new CPacketPlayer());
                    }
                }
                break;
            case "Potion":
                if (mc.thePlayer.isPotionActive(Potion.getPotionById(10)))
                    packetRegen((int)(((Number) settings.get(PACKET).getValue()).doubleValue() / 2.0D * mc.thePlayer.getActivePotionEffect(Potion.getPotionById(10)).getAmplifier()));
                break;
        }
    }

    private void packetRegen(int packets) {
        if (mc.thePlayer.onGround) {
            for (int i = 0; i < packets; i++) {
                mc.getConnection().sendPacket(new CPacketPlayer(true));
            }
        }
    }
}
