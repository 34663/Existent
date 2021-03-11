package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventNameTag;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.AntiBot;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NameTags2 extends Module {
    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap();
    public Random fontRandom = new Random();

    private final String INVIS = "INVIS";
    private final String ARMOR = "ARMOR";

    public NameTags2(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(INVIS, new Setting(INVIS, true, ""));
        settings.put(ARMOR, new Setting(ARMOR, true, ""));
    }

    @EventTarget
    public void onRender3D(EventRender3D render) {
        try {
            updatePositions();
        } catch (Exception ignored) {
            ;
        }
    }
    @EventTarget
    public void onRender2D(EventRender2D event) {
        GlStateManager.pushMatrix();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        for (final Entity entity : entityPositions.keySet()) {
            if (entity != mc.thePlayer && ((Boolean) settings.get(INVIS).getValue() || !entity.isInvisible())) {
                GlStateManager.pushMatrix();
                if (entity instanceof EntityPlayer) {
                    final double[] array = entityPositions.get(entity);
                    if (array[3] < 0.0 || array[3] >= 1.0) {
                        GlStateManager.popMatrix();
                        continue;
                    }
                    final CFontRenderer wqy18 = Fonts.Tahoma18;
                    GlStateManager.translate(array[0] / scaledResolution.getScaleFactor(), array[1] / scaledResolution.getScaleFactor(), 0.0);
                    this.scale();
                    GlStateManager.translate(0.0, -2.5, 0.0);
                    final String string = "Health: " + Math.round(((EntityLivingBase) entity).getHealth() * 10.0f) / 10;
                    final String prefix = (Existent.getFriendManager().isFriend(entity.getName()) ? "\247a[F]" : "") + (AntiBot.getInvalid().contains(entity) ? "\2479[BOT]" : "") + (!MiscUtils.isTeams((EntityPlayer) entity) ? "\247b[TEAM]" : "") + "§r";
                    String name = entity.getDisplayName().getUnformattedText();
                    if ((Boolean) Existent.getModuleManager().getClazz(StreamerMode.class).getSetting(StreamerMode.SCRAMBLE).getValue()) {
                        String newstr = "";
                        char[] rdm = {'|', '|', '|', '|', '|', '|'};
                        for(int c = 0; c < name.length(); c++){
                            char ch = rdm[KillAura.randomNumber(rdm.length-1, 0)];
                            newstr = newstr.concat(ch + "");
                        }
                        name = "§r" + newstr;
                    }
                    final String string4 = prefix + name;
                    final float n = wqy18.getStringWidth(string4.replaceAll("§.", ""));
                    final float n2 = Fonts.comfortaa12.getStringWidth(string);
                    final float n3 = (Math.max(n, n2)) + 8.0f;
                    RenderingUtils.drawRect(-n3 / 2.0f, -25.0f, n3 / 2.0f, 0.0f, Colors.getColor(0, 130));
                    final int n4 = (int) (array[0] + -n3 / 2.0f - 3.0) / 2 - 26;
                    final int n5 = (int) (array[0] + n3 / 2.0f + 3.0) / 2 + 20;
                    final int n6 = (int) (array[1] - 30.0) / 2;
                    final int n7 = (int) (array[1] + 11.0) / 2;
                    final int n8 = scaledResolution.getScaledHeight() / 2;
                    final int n9 = scaledResolution.getScaledWidth() / 2;
                    wqy18.drawStringWithShadow(string4, -n3 / 2.0f + 4.0f, -22.0f, new Color(255, 255, 255, 255).getRGB());
                    Fonts.comfortaa12.drawString(string, -n3 / 2.0f + 4.0f, -10.0f, new Color(255, 255, 255, 255).getRGB());
                    final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                    RenderingUtils.drawRect(-n3 / 2.0f, -1.0f, n3 / 2.0f, 0.0f, Colors.getColor(0, 100));
                    RenderingUtils.drawRect(-n3 / 2.0f, -1.0f, n3 / 2.0f - n3 / 2.0f * (1.0f - (float) Math.ceil(entityLivingBase.getHealth() + entityLivingBase.getAbsorptionAmount()) / (entityLivingBase.getMaxHealth() + entityLivingBase.getAbsorptionAmount())) * 2.0f, 0.0f, Colors.getTeamColor(entity));
                    if ((Boolean) settings.get(ARMOR).getValue()) {
                        final ArrayList<ItemStack> list = new ArrayList<ItemStack>();
                        for (int i = 0; i < 5; ++i) {
                            final ItemStack getEquipmentInSlot = ((EntityPlayer) entity).getEquipmentInSlot(i);
                            if (getEquipmentInSlot != null) {
                                list.add(getEquipmentInSlot);
                            }
                        }
                        int n10 = -(list.size() * 9);
                        for (final ItemStack itemStack : list) {
                            RenderHelper.enableGUIStandardItemLighting();
                            mc.getRenderItem().renderItemIntoGUI(itemStack, n10 + 6, -42);
                            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack, n10, -42);
                            n10 += 3;
                            RenderHelper.disableStandardItemLighting();
                            if (itemStack != null) {
                                int n11 = 21;
                                int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), itemStack);
                                int getEnchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack);
                                int getEnchantmentLevel3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(19), itemStack);
                                if (getEnchantmentLevel > 0) {
                                    this.drawEnchantTag("Sh" + this.getColor(getEnchantmentLevel) + getEnchantmentLevel, n10, n11);
                                    n11 += 6;
                                }
                                if (getEnchantmentLevel2 > 0) {
                                    this.drawEnchantTag("Fir" + this.getColor(getEnchantmentLevel2) + getEnchantmentLevel2, n10, n11);
                                    n11 += 6;
                                }
                                if (getEnchantmentLevel3 > 0) {
                                    this.drawEnchantTag("Kb" + this.getColor(getEnchantmentLevel3) + getEnchantmentLevel3, n10, n11);
                                } else if (itemStack.getItem() instanceof ItemArmor) {
                                    int getEnchantmentLevel4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(0), itemStack);
                                    int getEnchantmentLevel5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(7), itemStack);
                                    int getEnchantmentLevel6 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), itemStack);
                                    if (getEnchantmentLevel4 > 0) {
                                        this.drawEnchantTag("P" + this.getColor(getEnchantmentLevel4) + getEnchantmentLevel4, n10, n11);
                                        n11 += 6;
                                    }
                                    if (getEnchantmentLevel5 > 0) {
                                        this.drawEnchantTag("Th" + this.getColor(getEnchantmentLevel5) + getEnchantmentLevel5, n10, n11);
                                        n11 += 6;
                                    }
                                    if (getEnchantmentLevel6 > 0) {
                                        this.drawEnchantTag("Unb" + this.getColor(getEnchantmentLevel6) + getEnchantmentLevel6, n10, n11);
                                    }
                                } else if (itemStack.getItem() instanceof ItemBow) {
                                    int getEnchantmentLevel7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(48), itemStack);
                                    int getEnchantmentLevel8 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(49), itemStack);
                                    int getEnchantmentLevel9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(50), itemStack);
                                    if (getEnchantmentLevel7 > 0) {
                                        this.drawEnchantTag("Pow" + this.getColor(getEnchantmentLevel7) + getEnchantmentLevel7, n10, n11);
                                        n11 += 6;
                                    }
                                    if (getEnchantmentLevel8 > 0) {
                                        this.drawEnchantTag("Pun" + this.getColor(getEnchantmentLevel8) + getEnchantmentLevel8, n10, n11);
                                        n11 += 6;
                                    }
                                    if (getEnchantmentLevel9 > 0) {
                                        this.drawEnchantTag("Fir" + this.getColor(getEnchantmentLevel9) + getEnchantmentLevel9, n10, n11);
                                    }
                                } else if (itemStack.getRarity() == EnumRarity.EPIC) {
                                    this.drawEnchantTag("§6§lGod", n10 - 2, n11);
                                }
                                final int n12 = (int) Math.round(255.0 - itemStack.getItemDamage() * 255.0 / itemStack.getMaxDamage());
                                new Color(255 - n12 << 16 | n12 << 8).brighter();
                                final float n13 = (float) (n10 * 1.05) - 2.0f;
                                if (itemStack.getMaxDamage() - itemStack.getItemDamage() > 0) {
                                    GlStateManager.pushMatrix();
                                    GlStateManager.disableDepth();
                                    GlStateManager.enableDepth();
                                    GlStateManager.popMatrix();
                                }
                                n10 += 12;
                            }
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }
    @EventTarget
    public void onNametag(EventNameTag eventNameTag) {
        // Cancel
        eventNameTag.setCancelled(true);
    }

    private void drawEnchantTag(final String text, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        n *= (int)1.05;
        n2 -= 6;
        CFontRenderer font = Fonts.comfortaa10;
        font.drawStringWithShadow(text, n + 9, -30 - n2, Colors.getColor(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private String getColor(final int n) {
        if (n != 1) {
            if (n == 2) {
                return "§a";
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§6";
            }
        }
        return "§f";
    }
    private void updatePositions() {
        entityPositions.clear();
        float pTicks = mc.timer.renderPartialTicks;
        for (Object o : mc.theWorld.loadedEntityList) {
            Entity ent = (Entity) o;
            if ((ent != mc.thePlayer) && ((ent instanceof EntityPlayer)) && ((!ent.isInvisible()))) {
                double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
                double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
                double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;
                y += ent.height + 0.2D;
                if ((convertTo2D(x, y, z)[2] >= 0.0D) && (convertTo2D(x, y, z)[2] < 1.0D)) {
                    entityPositions.put((EntityPlayer) ent, new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1], Math.abs(convertTo2D(x, y + 1.0D, z, ent)[1] - convertTo2D(x, y, z, ent)[1]), convertTo2D(x, y, z)[2]});
                }
            }
        }
    }
    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        float pTicks = mc.timer.renderPartialTicks;
        float prevYaw = mc.thePlayer.rotationYaw;
        float prevPrevYaw = mc.thePlayer.prevRotationYaw;
        float[] rotations = RotationUtils.getRotationFromPosition(
                ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
                ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
                ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D);
        mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
        mc.entityRenderer.setupCameraTransform(pTicks, 0);
        double[] convertedPoints = convertTo2D(x, y, z);
        mc.getRenderViewEntity().rotationYaw = prevYaw;
        mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
        mc.entityRenderer.setupCameraTransform(pTicks, 0);
        return convertedPoints;
    }
    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }
    private void scale() {
        final float n = 1.0f * (mc.gameSettings.smoothCamera ? 2.0f : 1.0f);
        GlStateManager.scale(n, n, n);
    }
}
