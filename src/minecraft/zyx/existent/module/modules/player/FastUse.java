package zyx.existent.module.modules.player;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class FastUse extends Module {
    private String DELAY = "DELAY";

    public FastUse(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(DELAY, new Setting(DELAY, 4, "RightClickDelay", 1, 0, 10));
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 4;
        super.onDisable();
    }

    @EventTarget
    public void onTick(EventTick event) {
        mc.rightClickDelayTimer = ((Number) settings.get(DELAY).getValue()).intValue();
        if (mc.thePlayer.getItemInUseCount() == 1.0D && !(mc.thePlayer.getActiveItemStack().getItem() instanceof ItemBow) && !(mc.thePlayer.getActiveItemStack().getItem() instanceof ItemSword) && mc.gameSettings.keyBindUseItem.pressed) {
            for (int i = 1; i < 20; i++) {
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                if (mc.thePlayer.ticksExisted % 5 == 0)
                    mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ, false));
            }
        }
    }
}
