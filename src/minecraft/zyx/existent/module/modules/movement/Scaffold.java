package zyx.existent.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.GL11;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.event.events.EventSafeWalk;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.RayTraceUtil;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import zyx.existent.utils.timer.Timer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Scaffold extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.FLOWING_LAVA, Blocks.SAND, Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_SLAB, Blocks.WOODEN_SLAB, Blocks.STONE_SLAB2, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.ANVIL, Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.CACTUS, Blocks.LADDER, Blocks.WEB);
    private final List<Block> validBlocks = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA);
    private final BlockPos[] blockPositions = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 0)};
    private final EnumFacing[] facings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};
    private final Timer towertimer = new Timer();
    private float[] angles = new float[2];
    public static BlockData data;
    private int slot;
    public static boolean isSneaking;

    private final String ROTATIONMODE = "ROTATION MODE";
    private final String TOWER = "TOWER";
    private final String SWING = "NOSWING";
    private final String SAFEWALK = "SAFEWALK";
    private final String DOWN = "DOWNWARD";

    public Scaffold(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ROTATIONMODE, new Setting(ROTATIONMODE, new Options("Rotation Mode", "Under", new String[]{"Under", "BlockPos", "Intave", "Front"}), "Rotation method"));
        settings.put(TOWER, new Setting(TOWER, true, ""));
        settings.put(DOWN, new Setting(DOWN, true, ""));
        settings.put(SWING, new Setting(SWING, true, ""));
        settings.put(SAFEWALK, new Setting(SAFEWALK, true, ""));
    }

    @Override
    public void onEnable() {
        this.angles[0] = 999.0F;
        this.angles[1] = 999.0F;
        this.towertimer.reset();
        this.slot = mc.thePlayer.inventory.currentItem;
        this.data = null;
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.thePlayer.inventory.currentItem = this.slot;
        isSneaking = false;
        this.data = null;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        double yDif = 1.0D;
        double posY;

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && (Boolean) settings.get(DOWN).getValue()) {
            mc.gameSettings.keyBindSneak.pressed = false;
            isSneaking = true;
        } else {
            isSneaking = false;
        }

        for (posY = mc.thePlayer.posY - 1.0D; posY > 0.0D; posY--) {
            BlockPos blockPos = isSneaking ? new BlockPos(this.mc.thePlayer).add(0.0D, -0.75D, 0.0D).down() : new BlockPos(this.mc.thePlayer).add(0.0D, -0.75D, 0.0D);
            BlockData newData = getBlockData2(blockPos);

            if (newData != null) {
                yDif = mc.thePlayer.posY - posY;
                if (yDif <= 3.0D) {
                    data = newData;
                    break;
                }
            }
        }
        int slot = -1;
        int blockCount = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                int stackSize = itemStack.stackSize;

                if (isValidItem(itemStack.getItem()) && stackSize > blockCount) {
                    blockCount = stackSize;
                    slot = i;
                }
            }
        }

        if (data != null && slot != -1) {
            int last;
            BlockPos pos = data.pos;
            EnumFacing facing = data.face;
            Block block = mc.theWorld.getBlockState(pos.offset(data.face)).getBlock();
            Vec3d hitVec = getVec3(data);

            switch (((Options) settings.get(ROTATIONMODE).getValue()).getSelected()) {
                case "Under":
                    eventUpdate.setPitch(79.44F);
                    mc.thePlayer.rotationPitchHead = 79.44F;
                    break;
                case "BlockPos":
                    eventUpdate.setPitch(RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ())[1]);
                    eventUpdate.setYaw(RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ())[0]);
                    mc.thePlayer.rotationPitchHead = RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ())[1];
                    mc.thePlayer.rotationYawHead = RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ())[0];
                    mc.thePlayer.renderYawOffset = RotationUtils.getBlockRotations(pos.getX(), pos.getY(), pos.getZ())[0];
                    break;
                case "Intave":
                    float[] rot = RotationUtils.getIntaveRots(pos, facing);
                    eventUpdate.setPitch(82.500114F);
                    eventUpdate.setYaw((float) (rot[0] + MiscUtils.getDoubleRandom(-0.1D, 0.1D)));
                    mc.thePlayer.rotationPitchHead = 82.500114F;
                    mc.thePlayer.rotationYawHead = (float) (rot[0] + MiscUtils.getDoubleRandom(-0.1D, 0.1D));
                    mc.thePlayer.renderYawOffset = (float) (rot[0] + MiscUtils.getDoubleRandom(-0.1D, 0.1D));
                    break;
                case "Front":
                    float[] rot2 = RotationUtils.getIntaveRots(pos, facing);
                    eventUpdate.setPitch(79.44F);
                    eventUpdate.setYaw((float) (rot2[0] + 180 + MiscUtils.getDoubleRandom(-0.1D, 0.1D)));
                    mc.thePlayer.rotationPitchHead = 79.44F;
                    mc.thePlayer.rotationYawHead = (float) (rot2[0] + 180 + MiscUtils.getDoubleRandom(-0.1D, 0.1D));
                    mc.thePlayer.renderYawOffset = (float) (rot2[0] + 180 + MiscUtils.getDoubleRandom(-0.1D, 0.1D));
                    break;
            }

            if (!this.validBlocks.contains(block) || isBlockUnder(yDif)) {
                return;
            }

            if (eventUpdate.isPre()) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && (Boolean) settings.get(TOWER).getValue()) {
                    if (!mc.thePlayer.isMoving()) {
                        mc.thePlayer.motionX = 0.0D;
                        mc.thePlayer.motionY = 0.41982D;
                        mc.thePlayer.motionZ = 0.0D;
                        if (this.towertimer.delay(1500L)) {
                            mc.thePlayer.motionY = -0.28D;
                            this.towertimer.reset();
                        }
                    }
                }
                this.towertimer.reset();
            } else if (eventUpdate.isPost()) {
                last = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.processRightClickBlock(mc.thePlayer, mc.theWorld, pos, data.face, hitVec, EnumHand.MAIN_HAND);
                if ((Boolean) settings.get(SWING).getValue()) {
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                } else {
                    mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                }
                mc.thePlayer.inventory.currentItem = last;
            }
        }
    }
    @EventTarget
    public void onRender2D(EventRender2D render) {
        float width = render.getResolution().getScaledWidth();
        float height = render.getResolution().getScaledHeight();
        int color = new Color(255, 73, 73, 255).getRGB();

        if (this.getBlockCount() >= 64 && 128 > this.getBlockCount()) {
            color = new Color(255, 231, 77, 255).getRGB();
        } else if (this.getBlockCount() >= 128) {
            color = new Color(79, 255, 79, 255).getRGB();
        }

        GlStateManager.enableBlend();
        CFontRenderer font = Fonts.comfortaa18;
        font.drawStringWithShadow(this.getBlockCount() + " Blocks", width / 2F + 10, height / 2 - font.getStringHeight(this.getBlockCount() + " \247fBlocks") / 2F, -1);
    }
    @EventTarget
    public void onSafeWalk(EventSafeWalk event) {
        if ((Boolean) settings.get(SAFEWALK).getValue() && !isSneaking) {
            event.setCancelled(mc.thePlayer.onGround);
        }
    }

    private int getBlockCount() {
        int blockCount = 0;

        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                continue;
            }

            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();

            if (!isValidItem(item)) {
                continue;
            }
            blockCount += is.stackSize;
        }
        return blockCount;
    }
    private boolean isBlockUnder(double yOffset) {
        return !this.validBlocks.contains(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - yOffset, mc.thePlayer.posZ)).getBlock());
    }
    private boolean isValidItem(Item item) {
        if (item instanceof ItemBlock) {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !this.invalidBlocks.contains(block);
        }
        return false;
    }

    // BlockData
    public BlockData getBlockData(BlockPos pos, int i) {
        return (this.mc.theWorld.getBlockState(pos.add(0, 0, i)).getBlock() != Blocks.AIR) ? new BlockData(pos.add(0, 0, i), EnumFacing.NORTH) : ((this.mc.theWorld.getBlockState(pos.add(0, 0, -i)).getBlock() != Blocks.AIR) ? new BlockData(pos.add(0, 0, -i), EnumFacing.SOUTH) : ((this.mc.theWorld.getBlockState(pos.add(i, 0, 0)).getBlock() != Blocks.AIR) ? new BlockData(pos.add(i, 0, 0), EnumFacing.WEST) : ((this.mc.theWorld.getBlockState(pos.add(-i, 0, 0)).getBlock() != Blocks.AIR) ? new BlockData(pos.add(-i, 0, 0), EnumFacing.EAST) : ((this.mc.theWorld.getBlockState(pos.add(0, -i, 0)).getBlock() != Blocks.AIR) ? new BlockData(pos.add(0, -i, 0), EnumFacing.UP) : null))));
    }
    public BlockData getBlockData2(BlockPos pos) {
        BlockData blockData = null;
        int i = 0;
        while (blockData == null) {
            if (i >= 2) {
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, 0, 1))) {
                blockData = new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, 0, -1))) {
                blockData = new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 0, 0))) {
                blockData = new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 0, 0))) {
                blockData = new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, -1, 0))) {
                blockData = new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, 1, 0)) && isSneaking) {
                blockData = new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, 1, 1)) && isSneaking) {
                blockData = new BlockData(pos.add(0, 1, 1), EnumFacing.DOWN);
                break;
            }
            if (!this.isBlockPosAir(pos.add(0, 1, -1)) && isSneaking) {
                blockData = new BlockData(pos.add(0, 1, -1), EnumFacing.DOWN);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 1, 0)) && isSneaking) {
                blockData = new BlockData(pos.add(1, 1, 0), EnumFacing.DOWN);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 1, 0)) && isSneaking) {
                blockData = new BlockData(pos.add(-1, 1, 0), EnumFacing.DOWN);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 0, 1))) {
                blockData = new BlockData(pos.add(1, 0, 1), EnumFacing.NORTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 0, -1))) {
                blockData = new BlockData(pos.add(-1, 0, -1), EnumFacing.SOUTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 0, 1))) {
                blockData = new BlockData(pos.add(1, 0, 1), EnumFacing.WEST);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 0, -1))) {
                blockData = new BlockData(pos.add(-1, 0, -1), EnumFacing.EAST);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 0, 1))) {
                blockData = new BlockData(pos.add(-1, 0, 1), EnumFacing.NORTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 0, -1))) {
                blockData = new BlockData(pos.add(1, 0, -1), EnumFacing.SOUTH);
                break;
            }
            if (!this.isBlockPosAir(pos.add(1, 0, -1))) {
                blockData = new BlockData(pos.add(1, 0, -1), EnumFacing.WEST);
                break;
            }
            if (!this.isBlockPosAir(pos.add(-1, 0, 1))) {
                blockData = new BlockData(pos.add(-1, 0, 1), EnumFacing.EAST);
                break;
            }
            pos = pos.down();
            ++i;
        }
        return blockData;
    }

    private Vec3d getVec3(BlockData data) {
        BlockPos pos = data.pos;
        EnumFacing face = data.face;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        x += face.getFrontOffsetX() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3D, -0.3D);
            z += randomNumber(0.3D, -0.3D);
        } else {
            y += randomNumber(0.49D, 0.5D);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3D, -0.3D);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3D, -0.3D);
        }
        return new Vec3d(x, y, z);
    }
    public Vec3d getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3d offset = new Vec3d((double) facing.getDirectionVec().getX() / 2.0, (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3d point = new Vec3d((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5);
        return point.add(offset);
    }
    public boolean isBlockPosAir(final BlockPos blockPos) {
        return this.getBlockByPos(blockPos) == Blocks.AIR || this.getBlockByPos(blockPos) instanceof BlockLiquid;
    }
    public Block getBlockByPos(final BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }
    private double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    private static class BlockData {
        public final BlockPos pos;
        public final EnumFacing face;

        private BlockData(BlockPos pos, EnumFacing face) {
            this.pos = pos;
            this.face = face;
        }
    }
}
