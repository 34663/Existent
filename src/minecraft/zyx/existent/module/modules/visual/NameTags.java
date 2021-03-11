package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
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
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.RotationUtils;
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

public class NameTags extends Module {
    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap();

    public static String ARMOR = "ARMOR";
    public static String HEALTH = "HEALTH";
    private final String INVISIBLES = "INVISIBLES";

    public NameTags(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(ARMOR, new Setting(ARMOR, true, "Show armor when not hovering."));
        settings.put(HEALTH, new Setting(HEALTH, false, "Show health when not hovering."));
        settings.put(INVISIBLES, new Setting(INVISIBLES, false, "Show invisibles."));
    }

    @EventTarget
    public void onNametag(EventNameTag eventNameTag) {
        eventNameTag.setCancelled(true);
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
    public void onRender2D(EventRender2D render) {
        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(mc);

        for (Entity ent : entityPositions.keySet()) {
            if (ent != mc.thePlayer && ((Boolean) settings.get(INVISIBLES).getValue()) || !ent.isInvisible()) {
                GlStateManager.pushMatrix();
                if ((ent instanceof EntityPlayer)) {
                    String str = ent.getDisplayName().getFormattedText();
                    String name = ent.getName();
                    str = str.replace(ent.getDisplayName().getFormattedText(), "\247f" + ent.getDisplayName().getFormattedText());
                    if (Existent.getFriendManager().isFriend(ent.getName())) {
                        str = str.replace(ent.getDisplayName().getFormattedText(), "\247f[\247bF\247f] " + ent.getDisplayName().getFormattedText());
                    }

                    double[] renderPositions = entityPositions.get(ent);
                    if ((renderPositions[3] < 0.0D) || (renderPositions[3] >= 1.0D)) {
                        GlStateManager.popMatrix();
                        continue;
                    }

                    CFontRenderer font = Fonts.default18;
                    GlStateManager.translate(renderPositions[0] / scaledRes.getScaleFactor(), renderPositions[1] / scaledRes.getScaleFactor(), 0.0D);
                    scale();
                    String healthInfo = (int) ((EntityLivingBase) ent).getHealth() + "";
                    GlStateManager.translate(0.0D, -2.5D, 0.0D);
                    float strWidth = font.getStringWidth(str);
                    float strWidth2 = font.getStringWidth(healthInfo);
                    RenderingUtils.drawRect(-strWidth / 2 - 1, -10.0D, strWidth / 2 + 1, 3, Colors.getColor(0, 130));
                    int x3 = ((int) (renderPositions[0] + -strWidth / 2 - 3) / 2) - 26;
                    int x4 = ((int) (renderPositions[0] + strWidth / 2 + 3) / 2) + 20;
                    int y1 = ((int) (renderPositions[1] + -30) / 2);
                    int y2 = ((int) (renderPositions[1] + 11) / 2);
                    int mouseY = (render.getResolution().getScaledHeight() / 2);
                    int mouseX = (render.getResolution().getScaledWidth() / 2);
                    font.drawStringWithShadow(str, -strWidth / 2, -7.0F, Colors.getColor(255, 50, 50));
                    boolean hovered = x3 < mouseX && mouseX < x4 && y1 < mouseY && mouseY < y2;
                    if (((Boolean) settings.get(HEALTH).getValue()) || hovered) {
                        float health = ((EntityPlayer) ent).getHealth();
                        float[] fractions = new float[]{0f, 0.5f, 1f};
                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        float progress = (health * 5) * 0.01f;
                        Color customColor = Colors.blendColors(fractions, colors, progress).brighter();
                        try {
                            RenderingUtils.drawRect(strWidth / 2 + 1, -10.0D, strWidth / 2 + 2 + strWidth2, 3, Colors.getColor(0, 130));
                            font.drawStringWithShadow(healthInfo, strWidth / 2 + 2, (int) -7.0D, customColor.getRGB());
                        } catch (Exception ignored) {
                            ;
                        }
                    }
                    if (hovered || ((Boolean) settings.get(ARMOR).getValue())) {
                        ArrayList<ItemStack> itemsToRender = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            ItemStack stack = ((EntityPlayer) ent).getEquipmentInSlot(i);
                            if (stack != null) {
                                itemsToRender.add(stack);
                            }
                        }
                        int x = -(itemsToRender.size() * 9);
                        for (ItemStack stack : itemsToRender) {
                            RenderHelper.enableGUIStandardItemLighting();
                            mc.getRenderItem().renderItemIntoGUI(stack, x, -27);
                            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, -27);
                            x += 3;
                            RenderHelper.disableStandardItemLighting();
                            if (stack != null) {
                                int y = 21;
                                int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack);
                                int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), stack);
                                int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(19), stack);
                                if (sLevel > 0) {
                                    drawEnchantTag("Sh" + getColor(sLevel) + sLevel, x, y);
                                    y -= 9;
                                }
                                if (fLevel > 0) {
                                    drawEnchantTag("Fir" + getColor(fLevel) + fLevel, x, y);
                                    y -= 9;
                                }
                                if (kLevel > 0) {
                                    drawEnchantTag("Kb" + getColor(kLevel) + kLevel, x, y);
                                } else if ((stack.getItem() instanceof ItemArmor)) {
                                    int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(0), stack);
                                    int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(7), stack);
                                    int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack);
                                    if (pLevel > 0) {
                                        drawEnchantTag("P" + getColor(pLevel) + pLevel, x, y);
                                        y -= 9;
                                    }
                                    if (tLevel > 0) {
                                        drawEnchantTag("Th" + getColor(tLevel) + tLevel, x, y);
                                        y -= 9;
                                    }
                                    if (uLevel > 0) {
                                        drawEnchantTag("Unb" + getColor(uLevel) + uLevel, x, y);
                                    }
                                } else if ((stack.getItem() instanceof ItemBow)) {
                                    int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(48), stack);
                                    int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(49), stack);
                                    int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(50), stack);
                                    if (powLevel > 0) {
                                        drawEnchantTag("Pow" + getColor(powLevel) + powLevel, x, y);
                                        y -= 9;
                                    }
                                    if (punLevel > 0) {
                                        drawEnchantTag("Pun" + getColor(punLevel) + punLevel, x, y);
                                        y -= 9;
                                    }
                                    if (fireLevel > 0) {
                                        drawEnchantTag("Fir" + getColor(fireLevel) + fireLevel, x, y);
                                    }
                                } else if (stack.getRarity() == EnumRarity.EPIC) {
                                    drawEnchantTag("\2476\247lGod", x, y);
                                }
                                int var7 = (int) Math.round(255.0D - (double) stack.getItemDamage() * 255.0D / (double) stack.getMaxDamage());
                                int var10 = 255 - var7 << 16 | var7 << 8;
                                Color customColor = new Color(var10).brighter();

                                float x2 = (float) (x * 1.75D);
                                if ((stack.getMaxDamage() - stack.getItemDamage()) > 0) {
                                    GlStateManager.pushMatrix();
                                    GlStateManager.disableDepth();
                                    GL11.glScalef(0.57F, 0.57F, 0.57F);
                                    font.drawStringWithShadow("" + (stack.getMaxDamage() - stack.getItemDamage()), x2, -54, customColor.getRGB());
                                    GlStateManager.enableDepth();
                                    GlStateManager.popMatrix();
                                }
                                y = -20 - 33;
//                                for (Object o : ((EntityPlayer) ent).getActivePotionEffects()) {
//                                    PotionEffect pot = (PotionEffect) o;
//                                    String potName = StringUtils.capitalize(pot.getEffectName().substring(pot.getEffectName().lastIndexOf(".") + 1));
//                                    int XD = pot.getDuration() / 20;
//                                    SimpleDateFormat df = new SimpleDateFormat("m:ss");
//                                    String time = df.format(XD * 1000);
//                                    font.drawStringWithShadow((XD > 0 ? potName + " " + time : ""), -30, y, -1);
//                                    y -= 8;
//                                }
                                x += 12;
                            }
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    private String getColor(int level) {
        if (level == 1) {

        } else if (level == 2) {
            return "\247a";
        } else if (level == 3) {
            return "\2473";
        } else if (level == 4) {
            return "\2474";
        } else if (level >= 5) {
            return "\2476";
        }
        return "\247f";
    }
    private static void drawEnchantTag(String text, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        x = (int) (x * 1.75D);
        y -= 4;
        GL11.glScalef(0.57F, 0.57F, 0.57F);
        CFontRenderer font = Fonts.default18;
        font.drawStringWithShadow(text, x, -30 - y, Colors.getColor(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    private void scale() {
        float scale = 1.0F;
        scale *= ((mc.currentScreen == null) && (GameSettings.isKeyDown(mc.gameSettings.ofKeyBindZoom)) ? 2 : 1);
        GlStateManager.scale(scale, scale, scale);
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
}
