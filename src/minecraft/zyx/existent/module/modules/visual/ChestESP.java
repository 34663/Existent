package zyx.existent.module.modules.visual;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.player.ChestStealer;
import zyx.existent.utils.render.OutlineUtils;
import zyx.existent.utils.render.RenderingUtils;

import java.awt.*;
import java.util.Iterator;

public class ChestESP extends Module {
    public static String MODE = "MODE";

    public ChestESP(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Fill", new String[]{"Fill", "Wall", "Wall2", "Box", "Twilight", "Outline"}), "ChestESP method"));
    }

    @EventTarget
    public void onRender3D(EventRender3D render) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Box":
                for (final Object o : mc.theWorld.loadedTileEntityList) {
                    if (o instanceof TileEntityChest) {
                        final TileEntityLockable storage = (TileEntityLockable) o;
                        this.drawESPOnStorage(storage, storage.getPos().getX(), storage.getPos().getY(), storage.getPos().getZ());
                    }
                }
                break;
            case "Twilight":
                for (TileEntity tileEntityChest : mc.theWorld.loadedTileEntityList) {
                    if (tileEntityChest instanceof TileEntityChest) {
                        TileEntityChest tileEntityChest1 = (TileEntityChest) tileEntityChest;
                        Block block1 = mc.theWorld.getBlockState(tileEntityChest1.getPos()).getBlock();
                        Block block2 = mc.theWorld.getBlockState(new BlockPos(tileEntityChest1.getPos().getX(), tileEntityChest1.getPos().getY(), tileEntityChest1.getPos().getZ() - 1)).getBlock();
                        Block block3 = mc.theWorld.getBlockState(new BlockPos(tileEntityChest1.getPos().getX(), tileEntityChest1.getPos().getY(), tileEntityChest1.getPos().getZ() + 1)).getBlock();
                        Block block4 = mc.theWorld.getBlockState(new BlockPos(tileEntityChest1.getPos().getX() - 1, tileEntityChest1.getPos().getY(), tileEntityChest1.getPos().getZ())).getBlock();
                        Block block5 = mc.theWorld.getBlockState(new BlockPos(tileEntityChest1.getPos().getX() + 1, tileEntityChest1.getPos().getY(), tileEntityChest1.getPos().getZ())).getBlock();
                        double d1 = tileEntityChest1.getPos().getX() - (mc.getRenderManager()).viewerPosX;
                        double d2 = tileEntityChest1.getPos().getY() - (mc.getRenderManager()).viewerPosY;
                        double d3 = tileEntityChest1.getPos().getZ() - (mc.getRenderManager()).viewerPosZ;
                        GL11.glPushMatrix();
                        RenderHelper.disableStandardItemLighting();
                        if (block1 == Blocks.CHEST) {
                            if (block2 != Blocks.CHEST) {
                                if (block3 == Blocks.CHEST) {
                                    draw(Blocks.CHEST, d1, d2, d3, 1.0D, 1.0D, 2.0D);
                                } else if (block5 == Blocks.CHEST) {
                                    draw(Blocks.CHEST, d1, d2, d3, 2.0D, 1.0D, 1.0D);
                                } else if (block5 == Blocks.CHEST) {
                                    draw(Blocks.CHEST, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                                } else if (block2 != Blocks.CHEST && block3 != Blocks.CHEST && block4 != Blocks.CHEST && block5 != Blocks.CHEST) {
                                    draw(Blocks.CHEST, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                                }
                            }
                        } else if (block1 == Blocks.TRAPPED_CHEST && block2 != Blocks.TRAPPED_CHEST) {
                            if (block3 == Blocks.TRAPPED_CHEST) {
                                draw(Blocks.TRAPPED_CHEST, d1, d2, d3, 1.0D, 1.0D, 2.0D);
                            } else if (block5 == Blocks.TRAPPED_CHEST) {
                                draw(Blocks.TRAPPED_CHEST, d1, d2, d3, 2.0D, 1.0D, 1.0D);
                            } else if (block5 == Blocks.TRAPPED_CHEST) {
                                draw(Blocks.TRAPPED_CHEST, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                            } else if (block2 != Blocks.TRAPPED_CHEST && block3 != Blocks.TRAPPED_CHEST && block4 != Blocks.TRAPPED_CHEST && block5 != Blocks.TRAPPED_CHEST) {
                                draw(Blocks.TRAPPED_CHEST, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                            }
                        }

                        RenderHelper.enableStandardItemLighting();
                        GL11.glPopMatrix();
                        continue;
                    }

                    if (tileEntityChest instanceof TileEntityEnderChest) {
                        TileEntityEnderChest tileEntityEnderChest = (TileEntityEnderChest) tileEntityChest;
                        double d1 = tileEntityEnderChest.getPos().getX() - (mc.getRenderManager()).viewerPosX;
                        double d2 = tileEntityEnderChest.getPos().getY() - (mc.getRenderManager()).viewerPosY;
                        double d3 = tileEntityEnderChest.getPos().getZ() - (mc.getRenderManager()).viewerPosZ;
                        GL11.glPushMatrix();
                        RenderHelper.disableStandardItemLighting();
                        draw(Blocks.ENDER_CHEST, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                        RenderHelper.enableStandardItemLighting();
                        GL11.glPopMatrix();
                        continue;
                    }
                    if (tileEntityChest instanceof TileEntityFurnace) {
                        TileEntityFurnace tileEntityFurnace = (TileEntityFurnace) tileEntityChest;
                        double d1 = tileEntityFurnace.getPos().getX() - (mc.getRenderManager()).viewerPosX;
                        double d2 = tileEntityFurnace.getPos().getY() - (mc.getRenderManager()).viewerPosY;
                        double d3 = tileEntityFurnace.getPos().getZ() - (mc.getRenderManager()).viewerPosZ;
                        GL11.glPushMatrix();
                        RenderHelper.disableStandardItemLighting();
                        if (tileEntityFurnace.getBlockType() == Blocks.LIT_FURNACE) {
                            draw(Blocks.LIT_FURNACE, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                        } else {
                            draw(Blocks.FURNACE, d1, d2, d3, 1.0D, 1.0D, 1.0D);
                        }

                        RenderHelper.enableStandardItemLighting();
                        GL11.glPopMatrix();
                        continue;
                    }
                }
                break;
            case "Outline":
                for (final TileEntity entity : mc.theWorld.loadedTileEntityList) {
                    if (entity instanceof TileEntityChest || entity instanceof TileEntityEnderChest) {
                        OutlineUtils.renderOne(2.0F);
                        TileEntityRendererDispatcher.instance.renderTileEntity(entity, render.getRenderPartialTicks(), -1);
                        OutlineUtils.renderTwo();
                        TileEntityRendererDispatcher.instance.renderTileEntity(entity, render.getRenderPartialTicks(), -1);
                        OutlineUtils.renderThree();
                        TileEntityRendererDispatcher.instance.renderTileEntity(entity, render.getRenderPartialTicks(), -1);
                        OutlineUtils.renderFour(Color.white);
                        TileEntityRendererDispatcher.instance.renderTileEntity(entity, render.getRenderPartialTicks(), -1);
                        OutlineUtils.renderFive();
                        OutlineUtils.setColor(Color.white);
                    }
                }
                break;
        }
    }

    public void drawESPOnStorage(TileEntityLockable storage, double x, double y, double z) {
        assert !storage.isLocked();
        TileEntityChest chest = (TileEntityChest) storage;
        Vec3d vec;
        Vec3d vec2;
        if (chest.adjacentChestZNeg != null) {
            vec = new Vec3d(x + 0.0625, y, z - 0.9375);
            vec2 = new Vec3d(x + 0.9375, y + 0.875, z + 0.9375);
        } else if (chest.adjacentChestXNeg != null) {
            vec = new Vec3d(x + 0.9375, y, z + 0.0625);
            vec2 = new Vec3d(x - 0.9375, y + 0.875, z + 0.9375);
        } else if (chest.adjacentChestXPos == null && chest.adjacentChestZPos == null) {
            vec = new Vec3d(x + 0.0625, y, z + 0.0625);
            vec2 = new Vec3d(x + 0.9375, y + 0.875, z + 0.9375);
        } else {
            return;
        }
        GL11.glPushMatrix();
        RenderingUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        if (chest.getChestType() == BlockChest.Type.TRAP) {
            GL11.glColor4d(0.7, 0.1, 0.1, 0.3);
        } else if (chest.isEmpty && Existent.getModuleManager().isEnabled(ChestStealer.class)) {
            GL11.glColor4d(0.4, 0.2, 0.2, 0.3);
        } else {
            GL11.glColor4d(1, 0.2, 0.2, 0.3);
        }
        RenderingUtils.drawBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtils.post3D();
        GL11.glPopMatrix();
    }
    public void draw(Block paramBlock, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glLineWidth(0.3F);
        if (paramBlock == Blocks.ENDER_CHEST) {
            GL11.glColor4f(0.4F, 0.2F, 1.0F, 1.0F);
            paramDouble1 += 0.05000000074505806D;
            paramDouble2 += 0.0D;
            paramDouble3 += 0.05000000074505806D;
            paramDouble4 -= 0.10000000149011612D;
            paramDouble5 -= 0.10000000149011612D;
            paramDouble6 -= 0.10000000149011612D;
        } else if (paramBlock == Blocks.CHEST) {
            GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
            paramDouble1 += 0.05000000074505806D;
            paramDouble2 += 0.0D;
            paramDouble3 += 0.05000000074505806D;
            paramDouble4 -= 0.10000000149011612D;
            paramDouble5 -= 0.10000000149011612D;
            paramDouble6 -= 0.10000000149011612D;
        } else if (paramBlock == Blocks.TRAPPED_CHEST) {
            GL11.glColor4f(1.0F, 0.6F, 0.0F, 1.0F);
            paramDouble1 += 0.05000000074505806D;
            paramDouble2 += 0.0D;
            paramDouble3 += 0.05000000074505806D;
            paramDouble4 -= 0.10000000149011612D;
            paramDouble5 -= 0.10000000149011612D;
            paramDouble6 -= 0.10000000149011612D;
        } else if (paramBlock == Blocks.FURNACE) {
            GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
        } else if (paramBlock == Blocks.LIT_FURNACE) {
            GL11.glColor4f(1.0F, 0.4F, 0.0F, 1.0F);
        }

        RenderingUtils.drawOutlinedBoundingBox(new AxisAlignedBB(paramDouble1, paramDouble2, paramDouble3, paramDouble1 + paramDouble4, paramDouble2 + paramDouble5, paramDouble3 + paramDouble6));
        if (paramBlock == Blocks.ENDER_CHEST) {
            GL11.glColor4f(0.4F, 0.2F, 1.0F, 0.11F);
        } else if (paramBlock == Blocks.CHEST) {
            GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.11F);
        } else if (paramBlock == Blocks.TRAPPED_CHEST) {
            GL11.glColor4f(1.0F, 0.6F, 0.0F, 0.11F);
        } else if (paramBlock == Blocks.FURNACE) {
            GL11.glColor4f(0.6F, 0.6F, 0.6F, 0.11F);
        } else if (paramBlock == Blocks.LIT_FURNACE) {
            GL11.glColor4f(1.0F, 0.4F, 0.0F, 0.11F);
        }

        RenderingUtils.drawFilledBox(new AxisAlignedBB(paramDouble1, paramDouble2, paramDouble3, paramDouble1 + paramDouble4, paramDouble2 + paramDouble5, paramDouble3 + paramDouble6));
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
