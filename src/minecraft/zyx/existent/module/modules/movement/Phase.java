package zyx.existent.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventEntityCollision;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventPushOutBlock;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.timer.Timer;

public class Phase extends Module {
    private final String MODE = "MODE";

    private final Timer timer = new Timer();
    private int delay;
    double multiplier;
    double mx;
    double mz;
    double x;
    double z;
    double rot1, rot2;
    boolean shouldSpeed = false;
    float yaw, pitch;

    public Phase(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Phase Mode", "Hypixel", new String[]{"Hypixel", "Yumi", "NCP"}), "Phase exploit method."));
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        String currentPhase = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentPhase) {
            case "Hypixel":
                if (this.isInsideBlock()) {
                    return;
                }

                multiplier = 0.2D;
                mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                x = (double) MovementInput.moveForward * 0.2D * mx + (double) MovementInput.moveStrafe * 0.2D * mz;
                z = (double) MovementInput.moveForward * 0.2D * mz - (double) MovementInput.moveStrafe * 0.2D * mx;
                if (mc.thePlayer.isCollidedHorizontally && event.getPacket() instanceof CPacketPlayer) {
                    ++this.delay;
                    CPacketPlayer player = (CPacketPlayer) event.getPacket();
                    if (this.delay >= 5) {
                        player.x += x;
                        player.z += z;
                        --player.y;
                        this.delay = 0;
                    }
                }
                break;
            case "Yumi":
            case "NCP":
                Packet<?> p = event.getPacket();

                if (p instanceof SPacketPlayerPosLook) {
                    SPacketPlayerPosLook pac = (SPacketPlayerPosLook) p;
                    pac.yaw = mc.thePlayer.rotationYaw;
                    pac.pitch = mc.thePlayer.rotationPitch;
                    shouldSpeed = true;
                    if (!shouldSpeed)
                        rot2 = 0;
                }
                if (event.isOutgoing()) {
                    if (isInsideBlock()) {
                        return;
                    }
                    final double multiplier = 0.2;
                    final double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                    final double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                    final double x = MovementInput.moveForward * multiplier * mx + MovementInput.moveStrafe * multiplier * mz;
                    final double z = MovementInput.moveForward * multiplier * mz - MovementInput.moveStrafe * multiplier * mx;
                    if (mc.thePlayer.isCollidedHorizontally && p instanceof CPacketPlayer) {
                        delay++;
                        final CPacketPlayer player = (CPacketPlayer) p;
                        if (this.delay >= 5) {
                            player.x += x;
                            player.z += z;
                            --player.y;
                            this.delay = 0;
                        }
                    }
                }
                break;
        }
    }
    @EventTarget
    public void onBB(EventEntityCollision event) {
        String currentPhase = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentPhase) {
            case "Hypixel":
                if (event.getBoundingBox() != null && event.getBoundingBox().maxY > mc.thePlayer.boundingBox.minY && mc.thePlayer.isSneaking()) {
                    event.setBoundingBox(null);
                }

                if (mc.thePlayer == null) {
                    return;
                }

                mc.thePlayer.noClip = true;
                if (event.getBlockPos().getY() > mc.thePlayer.posY + (this.isInsideBlock() ? 0 : 1)) {
                    event.setBoundingBox(null);
                }

                if (mc.thePlayer.isCollidedHorizontally && event.getBlockPos().getY() > mc.thePlayer.boundingBox.minY - 0.4D) {
                    event.setBoundingBox(null);
                }
                break;
            case "Yumi":
            case "NCP":
                if ((event.getBoundingBox() != null) && (event.getBoundingBox().maxY > mc.thePlayer.boundingBox.minY)) {
                    event.setCancelled(true);
                }
                break;
        }
    }
    @EventTarget
    public void onPush(EventPushOutBlock event) {
        if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("NCP") || ((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Yumi")) {
            if(event.isPre()){
                event.setCancelled(true);
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentPhase = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentPhase) {
            case "Hypixel":
                if (event.isPost()) {
                    multiplier = 0.3D;
                    mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    x = (double) MovementInput.moveForward * multiplier * mx + (double) MovementInput.moveStrafe * multiplier * mz;
                    z = (double) MovementInput.moveForward * multiplier * mz - (double) MovementInput.moveStrafe * multiplier * mx;
                    double posY;
                    double posX;
                    if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !this.isInsideBlock()) {
                        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
                        posX = mc.thePlayer.posX;
                        posY = mc.thePlayer.posY;
                        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, posY - (isOnLiquid() ? 9000.0D : 0.09D), mc.thePlayer.posZ, false));
                        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                    }
                }
                break;
            case "Yumi":
            case "NCP":
                if (currentPhase.equalsIgnoreCase("Yumi")) {
                    if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                        MoveUtils.setMotion(0);
                        teleport(0.0652);
                    }
                }
                if (!shouldSpeed) {
                    if (isInsideBlock()) {
                        mc.thePlayer.rotationYaw = yaw;
                        mc.thePlayer.rotationPitch = pitch;
                    } else {
                        yaw = mc.thePlayer.rotationYaw;
                        pitch = mc.thePlayer.rotationPitch;
                    }
                }
                if (event.isPre()) {
                    if (shouldSpeed || isInsideBlock()) {
                        if (!mc.thePlayer.isSneaking())
                            mc.thePlayer.lastReportedPosY = 0;
                        mc.thePlayer.lastReportedPitch = 999;
                        mc.thePlayer.onGround = false;
                        mc.thePlayer.noClip = true;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        if (mc.gameSettings.keyBindJump.isPressed() && mc.thePlayer.posY == (int) mc.thePlayer.posY)
                            mc.thePlayer.jump();
                        mc.thePlayer.jumpMovementFactor = 0;
                    }
                    rot1++;
                    if (rot1 < 3) {
                        if (rot1 == 1) {
                            pitch += 15;
                        } else {
                            pitch -= 15;
                        }
                    }
                    if (mc.gameSettings.keyBindSneak.isPressed()) {
                        mc.thePlayer.lastReportedPitch = 999;
                        double X = mc.thePlayer.posX;
                        double Y = mc.thePlayer.posY;
                        double Z = mc.thePlayer.posZ;
                        if (!mc.thePlayer.isMoving())
                            if (MoveUtils.isOnGround(0.001) && !isInsideBlock()) {
                                mc.thePlayer.lastReportedPosY = -99;
                                event.setY(Y - 1);
                                mc.thePlayer.setPosition(X, Y - 1, Z);
                                timer.reset();
                                mc.thePlayer.motionY = 0;
                            } else if (timer.delay(100) && mc.thePlayer.posY == (int) mc.thePlayer.posY) {
                                mc.thePlayer.setPosition(X, Y - 0.3, Z);
                            }
                    }
                    if (isInsideBlock() && rot1 >= 3) {
                        if (shouldSpeed) {
                            teleport(0.617);
                            float sin = (float) Math.sin(rot2) * 0.1f;
                            float cos = (float) Math.cos(rot2) * 0.1f;
                            mc.thePlayer.rotationYaw += sin;
                            mc.thePlayer.rotationPitch += cos;
                            rot2++;
                        } else {
                            teleport(0.031);
                        }
                    }
                }
                break;
        }
    }

    public boolean isInsideBlock() {
        for (int x = MathHelper.floor(mc.thePlayer.boundingBox.minX); x < MathHelper.floor(mc.thePlayer.boundingBox.maxX) + 1; x++) {
            for (int y = MathHelper.floor(mc.thePlayer.boundingBox.minY); y < MathHelper.floor(mc.thePlayer.boundingBox.maxY) + 1; y++) {
                for (int z = MathHelper.floor(mc.thePlayer.boundingBox.minZ); z < MathHelper.floor(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if ((block != null) && (!(block instanceof BlockAir))) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld.getBlockState(new BlockPos(x, y, z)), mc.theWorld, new BlockPos(x, y, z));
                        if ((block instanceof BlockHopper)) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }
                        if (boundingBox != null) {
                            if (mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
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
    private void teleport(double dist) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += (forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += (forward > 0.0D ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1;
            } else if (forward < 0.0D) {
                forward = -1;
            }
        }
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        double xspeed = forward * dist * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * dist * Math.sin(Math.toRadians(yaw + 90.0F));
        double zspeed = forward * dist * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * dist * Math.cos(Math.toRadians(yaw + 90.0F));
        mc.thePlayer.setPosition(x + xspeed, y, z + zspeed);
    }
}
