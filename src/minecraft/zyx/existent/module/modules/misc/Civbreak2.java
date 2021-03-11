package zyx.existent.module.modules.misc;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockStone;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.*;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.RayTraceUtil;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.timer.Timer;

public class Civbreak2 extends Module {
    public int ticks;
    public static BlockPos pos;
    public EnumFacing side;
    public float[] rotations;
    public boolean isBreaking;
    public boolean isWaiting;
    public CPacketPlayerDigging packet;
    public final Timer timer = new Timer();

    private final String MODE = "MODE";
    private final String REACH = "REACH";
    private final String DELAY = "DELAY";

    public Civbreak2(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Zigga", new String[]{"Zigga", "Legit", "Nigga", "NaeNi"}), "Civbreak2 method"));
        settings.put(DELAY, new Setting(DELAY, 5, "CivDelay.", 1, 1, 10));
        settings.put(REACH, new Setting(REACH, 5, "Range.", 1, 1, 10));
    }

    @Override
    public void onEnable() {
        this.pos = null;
        this.packet = null;
        this.isBreaking = false;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            final BlockPos nexus = this.getNexus();
            if (nexus != null) {
                this.pos = nexus;
                this.side = EnumFacing.UP;
            }
            if (this.pos != null) {
                if (mc.thePlayer.getDistance(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > ((Number) settings.get(REACH).getValue()).floatValue()) {
                    this.packet = null;
                    this.isBreaking = false;
                    return;
                }
                if (mc.theWorld.getBlockState(this.pos).getBlock() == Blocks.BEDROCK) {
                    return;
                }
                final Vec3d from = new Vec3d(event.getX(), event.getY() + mc.thePlayer.getEyeHeight(), event.getZ());
                this.rotations = RotationUtils.getNeededFacing(new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5), from);
                final RayTraceResult raytrace = RayTraceUtil.rayTrace(((Number) settings.get(REACH).getValue()).floatValue(), this.rotations[0], this.rotations[1]);
                if (this.packet != null && !this.packet.getPosition().toString().equalsIgnoreCase(this.pos.toString())) {
                    this.packet = null;
                }
                if (raytrace.sideHit != null) {
                    this.side = raytrace.sideHit;
                }
                event.setYaw(this.rotations[0]);
                event.setPitch(this.rotations[1]);
                mc.thePlayer.renderYawOffset = this.rotations[0];
                mc.thePlayer.rotationYawHead = this.rotations[0];
                mc.thePlayer.rotationPitchHead = this.rotations[1];
                final EnumFacing side = (raytrace.sideHit != null) ? raytrace.sideHit : this.side;
                if (this.packet == null) {
                    this.ticks = ((Number) settings.get(DELAY).getValue()).intValue();
                    if (!this.isBreaking) {
                        this.isBreaking = true;
                        mc.playerController.clickBlock(this.pos, side);
                    }
                    mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                    mc.playerController.onPlayerDamageBlock(this.pos, side);
                } else {
                    event.setOnGround(true);

                    switch (((Options) settings.get(MODE).getValue()).getSelected()) {
                        case "Zigga":
                            if (mc.theWorld.getBlockState(this.pos).getBlock() != Blocks.AIR) {
                                this.ticks = ((Number) settings.get(DELAY).getValue()).intValue();
                                mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, side));
                                mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, EnumFacing.DOWN));
                            }
                            break;
                        case "Nigga":
                            if (this.ticks > 0) {
                                --this.ticks;
                                mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, EnumFacing.DOWN));
                            } else {
                                this.ticks = 3;
                                mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, side));
                                mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, EnumFacing.DOWN));
                            }
                            break;
                        case "NaeNi":
                            mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                            mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, side));
                            mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                            mc.thePlayer.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, EnumFacing.DOWN));
                            break;
                        case "Legit":
                            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                            mc.playerController.onPlayerDamageBlock(this.pos, side);
                            break;
                    }
                }
            } else {
                this.isBreaking = false;
            }
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.pos != null) {
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2929);
            GL11.glEnable(2848);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0f);
            if (this.pos != null && MathHelper.sqrt(mc.thePlayer.getDistanceSq(this.pos)) > ((Number) settings.get(REACH).getValue()).floatValue()) {
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof BlockAir) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            } else if (mc.theWorld.getBlockState(this.pos).getBlock() == Blocks.BEDROCK) {
                GL11.glColor4f(0.4f, 0.4f, 0.4f, 1.0f);
            } else {
                GL11.glColor4f(0.5f, 1.0f, 0.5f, 1.0f);
            }
            double var10000 = this.pos.getX();
            mc.getRenderManager();
            final double var10001 = var10000 - RenderManager.renderPosX;
            var10000 = this.pos.getY();
            mc.getRenderManager();
            final double y = var10000 - RenderManager.renderPosY;
            var10000 = this.pos.getZ();
            mc.getRenderManager();
            final double z = var10000 - RenderManager.renderPosZ;
            final double xo = 1.0;
            final double yo = 1.0;
            final double zo = 1.0;
            if (this.pos != null && MathHelper.sqrt(mc.thePlayer.getDistanceSq(this.pos)) > ((Number) settings.get(REACH).getValue()).floatValue()) {
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
            } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof BlockAir) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.2f);
            } else if (mc.theWorld.getBlockState(this.pos).getBlock() == Blocks.BEDROCK) {
                GL11.glColor4f(0.4f, 0.4f, 0.4f, 0.2f);
            } else {
                RenderingUtils.glColor(Colors.rainbow(2000, 0.8f, 1.0f), 0.2f);
            }
            RenderingUtils.drawFilledBox(new AxisAlignedBB(var10001, y, z, var10001 + xo, y + yo, z + zo));
            GL11.glDepthMask(true);
            GL11.glDisable(2848);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
    }
    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.packet = (CPacketPlayerDigging) event.getPacket();
            }
        }
    }
    @EventTarget
    public void onDigging(final EventBlockBreaking event) {
        if (event.getState() == EventBlockBreaking.EnumBlock.CLICK) {
            this.pos = event.getPos();
            this.side = event.getSide();
            this.isBreaking = true;
        }
    }

    public BlockPos getNexus() {
        BlockPos pos = null;
        for (int x = -7; x < 7; ++x) {
            for (int y = -7; y < 7; ++y) {
                for (int z = -7; z < 7; ++z) {
                    pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.END_STONE) {
                        return pos;
                    }
                }
            }
        }
        return this.pos;
    }
}
