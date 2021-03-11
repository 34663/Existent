package zyx.existent.module.modules.movement;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.timer.Timer;

public class NoSlowDown extends Module {
    public final static String MODE = "MODE";
    private final Timer timer = new Timer();

    public NoSlowDown(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE,new Setting(MODE,new Options("Mode", "NCP", new String[] {"NCP", "AAC"}),"NoSlowDown method."));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        if ((currentmode.equalsIgnoreCase("AAC") && timer.delay(20)) || (currentmode.equalsIgnoreCase("NCP") && mc.thePlayer.onGround)) {
            if (mc.thePlayer.isBlocking() && mc.thePlayer.isMoving()) {
                if (event.isPre()) {
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                } else if (event.isPost()) {
                    timer.reset();
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                }
            }
        }
    }
}
