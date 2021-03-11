package zyx.existent.module.modules.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.timer.Timer;

import java.util.Collections;

public class AntiVoid extends Module {
    private Timer timer = new Timer();
    private boolean saveMe;
    private String VOID = "VOID";
    private String MODE = "MODE";
    private String DISTANCE = "DIST";

    public AntiVoid(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(VOID, new Setting(VOID, true, "VoidCheck."));
        settings.put(DISTANCE, new Setting(DISTANCE, 5, "The fall distance needed to catch.", 1, 1, 10));
        settings.put(MODE,new Setting(MODE,new Options("Mode", "Packet", new String[] {"Packet", "SetY", "YZero", "Motion"}),"AntiVoid method."));
    }

    @EventTarget
    public void onMove(EventMove eventMove) {
        if ((saveMe && timer.delay(150)) || mc.thePlayer.isCollidedVertically) {
            saveMe = false;
            timer.reset();
        }
        int dist = ((Number) settings.get(DISTANCE).getValue()).intValue();
        if (mc.thePlayer.fallDistance > dist && !(Existent.getModuleManager().isEnabled(Flight.class) && Existent.getModuleManager().isEnabled(Glide.class))) {
            if (!((Boolean) settings.get(VOID).getValue()) || !isBlockUnder()) {
                if (!saveMe) {
                    saveMe = true;
                    timer.reset();
                }
                mc.thePlayer.fallDistance = 0;
                switch (((Options) settings.get(MODE).getValue()).getSelected()) {
                    case "Packet":
                        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));
                        break;
                    case "Motion":
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + dist, mc.thePlayer.posZ);
                        break;
                    case "YZero":
                        mc.thePlayer.motionY = 0;
                        break;
                    case "SetY":
                        eventMove.setY(-1.0E-10D);
                        break;
                }
            }
        }
    }

    private boolean isBlockUnder() {
        for (int i = (int) mc.thePlayer.posY; i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }
}
