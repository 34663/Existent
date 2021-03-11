package zyx.existent.module.modules.player;

import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventPacketSend;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;

public class NoFall extends Module {
    private final String MODE = "MODE";
    private boolean canmeme;
    private int state;

    public NoFall(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Hypixel", new String[]{"Normal", "AAC4", "Hypixel", "Spoof", "Packet"}), "NoFall method"));
    }

    @Override
    public void onEnable() {
        state = 1;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Normal":
                if (mc.thePlayer.onGround) {
                    canmeme = false;
                    mc.thePlayer.fallDistance = 0;
                }
                if (mc.thePlayer.fallDistance >= 2.95) {
                    event.setOnGround(canmeme = true);
                } else {
                    canmeme = false;
                }
                break;
            case "Hypixel":
                if (mc.thePlayer.fallDistance > 2.5F && (mc.thePlayer.posY % 0.0625D != 0.0D || mc.thePlayer.posY % 0.015256D != 0.0D)) {
                    mc.getConnection().sendPacket(new CPacketPlayer(true));
                    mc.thePlayer.fallDistance = 0.0F;
                }
                break;
            case "Spoof":
                if (mc.thePlayer.fallDistance > 2) {
                    event.setOnGround(true);
                    mc.thePlayer.fallDistance = 0;
                }
                break;
            case "Packet":
                if (mc.thePlayer.fallDistance > 2) {
                    mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer(mc.thePlayer.ticksExisted % 2 == 0));
                }
                break;
            case "AAC":
                if (mc.thePlayer.fallDistance > 2F) {
                    mc.thePlayer.connection.sendPacket(new CPacketPlayer(true));
                    state = 2;
                } else if (state == 2 && mc.thePlayer.fallDistance < 2) {
                    mc.thePlayer.motionY = 0.1D;
                    state = 3;
                    return;
                }

                switch (state) {
                    case 3:
                        mc.thePlayer.motionY = 0.1D;
                        state = 4;
                        break;
                    case 4:
                        mc.thePlayer.motionY = 0.1D;
                        state = 5;
                        break;
                    case 5:
                        mc.thePlayer.motionY = 0.1D;
                        state = 1;
                        break;
                }
                break;
        }
    }
    @EventTarget
    public void onPacketSent(EventPacketSend event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            if (canmeme) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isBlockUnder() {
        for (int i = (int) (mc.thePlayer.posY - 1.0D); i > 0; ) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                i--;
                continue;
            }
            return true;
        }
        return false;
    }
    private boolean inVoid() {
        if (mc.thePlayer.posY < 0) {
            return false;
        }
        for (int off = 0; off < mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, off, mc.thePlayer.posZ);
            if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    private boolean inAir(double height, double plus) {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int off = 0; off < height; off += plus) {
            AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY - off, mc.thePlayer.posZ);
            if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
