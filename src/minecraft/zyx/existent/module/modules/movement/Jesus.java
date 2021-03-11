package zyx.existent.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventEntityCollision;
import zyx.existent.event.events.EventJump;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.BlockUtils;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.timer.Timer;

import java.util.ArrayList;
import java.util.Arrays;

public class Jesus extends Module {
    private final String MODE = "MODE";
    private final Timer timer = new Timer();
    private int stage, ticks;
    public boolean wasWater;
    public boolean isFloat;

    public Jesus(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE,new Setting(MODE,new Options("Mode", "NCP", new String[] {"NCP", "Dolphin", "Flight"}),"Jesus method."));
    }

    @Override
    public void onEnable() {
        stage = 0;
        this.wasWater = false;
        this.isFloat = false;
        super.onEnable();
    }

    @EventTarget
    public void onBlockCollision(EventEntityCollision event) {
        if (mc.theWorld != null && event.getBlock() instanceof BlockLiquid && shouldSetBoundingBox() && !this.isInLiquid()) {
            event.setBoundingBox(new AxisAlignedBB(0.3D, 0.0D, 0.3D, 0.7D, 0.99999D, 0.7D));
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "NCP":
                if (event.isPre()) {
                    if (this.isOnLiquid() && !this.isInLiquid() && !mc.thePlayer.isSneaking()) {
                        this.ticks = 1;
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            event.setY(event.getY() - 1.0E-4D);
                        } else {
                            event.setY(event.getY() + 1.0E-4D);
                        }
                    }
                    if (isInLiquid() && !mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = 0.126799999;
                    }
                }
                if (this.ticks == 1 && !mc.thePlayer.isOnLiquid())
                    this.ticks = 0;
                break;
            case "Dolphin":
                if (event.isPre()) {
                    boolean sh = shouldJesus();
                    if (mc.thePlayer.onGround && !mc.thePlayer.isInWater() && sh) {
                        stage = 1;
                        timer.reset();
                    }
                    if (stage > 0 && !timer.delay(2500)) {
                        if ((mc.thePlayer.isCollidedVertically && !MoveUtils.isOnGround(0.001)) || mc.thePlayer.isSneaking()) {
                            stage = -1;
                        }
                        mc.thePlayer.motionX *= 0;
                        mc.thePlayer.motionZ *= 0;
                        if (!mc.thePlayer.isInLiquid() && !mc.thePlayer.isInWater()) {
                            MoveUtils.setMotion(0.25 + MoveUtils.getSpeedEffect() * 0.05);
                        }
                        double motionY = getMotionY(stage);
                        if (motionY != -999) {
                            mc.thePlayer.motionY = motionY;
                        }
                        stage++;
                    }
                    if (isInLiquid() && !mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = 0.126799999;
                    }
                }
                break;
            case "Flight":
                if (event.isPre()) {
                    boolean sh = shouldJesus();
                    if (mc.thePlayer.onGround && !mc.thePlayer.isInWater() && sh) {
                        this.stage = 1;
                        this.timer.reset();
                    }
                    if (this.stage > 0) {
                        if ((mc.thePlayer.isCollidedVertically && !MoveUtils.isOnGround(0.001D)) || mc.thePlayer.isSneaking())
                            this.stage = -1;
                        mc.thePlayer.motionX *= 0.0D;
                        mc.thePlayer.motionZ *= 0.0D;
                        if (!mc.thePlayer.isInLiquid() && !mc.thePlayer.isInWater())
                            MoveUtils.setMotion(0.25D + MoveUtils.getSpeedEffect() * 0.05D);
                        if (this.stage > 18)
                            this.stage = 13;
                        double motionY = getMotionY(this.stage);
                        if (motionY != -999.0D)
                            mc.thePlayer.motionY = motionY;
                        this.stage++;
                    }
                    if (isInLiquid() && !mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = 0.126799999;
                    }
                }
                break;
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Dolphin")||((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Flight")) {
            Packet<?> p = event.getPacket();
            if (p instanceof SPacketPlayerPosLook) {
                stage = 0;
            }
        }
    }
    @EventTarget
    public void onJump(EventJump eventJump) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (BlockUtils.isOnLiquid(0.001)) {
                if (BlockUtils.isTotalOnLiquid(0.001) && mc.thePlayer.onGround && !mc.thePlayer.isInWater()) {
                    eventJump.setCancelled(mc.thePlayer.ticksExisted % 2 != 0);
                }
            }
        }
    }

    private boolean shouldJesus() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        ArrayList<BlockPos> pos = new ArrayList<>(Arrays.asList(new BlockPos(x + 0.3, y, z + 0.3), new BlockPos(x - 0.3, y, z + 0.3), new BlockPos(x + 0.3, y, z - 0.3), new BlockPos(x - 0.3, y, z - 0.3)));
        for (BlockPos po : pos) {
            if (!(mc.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
                continue;
            if (mc.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) instanceof Integer) {
                if (mc.theWorld.getBlockState(po).getValue((IProperty<Integer>) BlockLiquid.LEVEL) <= 4) {
                    return true;
                }
            }
        }
        return false;
    }
    public double getMotionY(double stage) {
        stage--;
        double[] motion = new double[]{0.500, 0.484, 0.468, 0.436, 0.404, 0.372, 0.340, 0.308, 0.276, 0.244, 0.212, 0.180, 0.166, 0.166, 0.156, 0.123, 0.135, 0.111, 0.086, 0.098, 0.073, 0.048, 0.06, 0.036, 0.0106, 0.015, 0.004, 0.004, 0.004, 0.004, -0.013, -0.045, -0.077, -0.109};
        if (stage < motion.length && stage >= 0) {
            return motion[(int) stage];
        } else {
            return -999;
        }
    }
    private boolean shouldSetBoundingBox() {
        if (mc.thePlayer != null)
            return (!mc.thePlayer.isSneaking() && mc.thePlayer.fallDistance < 12.0F);
        return false;
    }
    public boolean isOnLiquid() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean onLiquid = false;
        final int y = (int) mc.thePlayer.boundingBox.offset(0.0, -0.0, 0.0).minY;
        for (int x = MathHelper.floor(mc.thePlayer.boundingBox.minX); x < MathHelper.floor(mc.thePlayer.boundingBox.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(mc.thePlayer.boundingBox.minZ); z < MathHelper.floor(mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }
    public boolean isInLiquid() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean inLiquid = false;
        final int y = (int) (mc.thePlayer.boundingBox.minY + 0.02);
        for (int x = MathHelper.floor(mc.thePlayer.boundingBox.minX); x < MathHelper.floor(mc.thePlayer.boundingBox.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(mc.thePlayer.boundingBox.minZ); z < MathHelper.floor(mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }
}
