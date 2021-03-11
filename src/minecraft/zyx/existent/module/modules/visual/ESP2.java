package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
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
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.module.modules.movement.Sneak;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ESP2 extends Module {
    public final List<Entity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private int color = Color.WHITE.getRGB();
    private final int backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
    private final int black = Color.BLACK.getRGB();
    public Random fontRandom = new Random();

    private String MODE = "MODE";
    private String MODE2D = "2DMODE";
    private String COLOR = "COLOR";
    private String PLAYERS = "PLAYERS";
    private String ANIMALS = "ANIMALS";
    private String MONSTERS = "MONSTERS";
    private String INVISIBLES = "INVISIBLES";
    private String ITEMS = "ITEMS";
    private String TAGS = "TAGS";
    private String BORDER = "BORDER";
    private String ARMOR = "ARMOR";
    private String HEALTH = "HEALTH";

    public ESP2(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(PLAYERS, new Setting(PLAYERS, true, ""));
        settings.put(ANIMALS, new Setting(ANIMALS, true, ""));
        settings.put(MONSTERS, new Setting(MONSTERS, true, ""));
        settings.put(INVISIBLES, new Setting(INVISIBLES, true, ""));
        settings.put(ITEMS, new Setting(ITEMS, true, ""));
        settings.put(TAGS, new Setting(TAGS, true, ""));
        settings.put(ARMOR, new Setting(ARMOR, true, ""));
        settings.put(HEALTH, new Setting(HEALTH, true, ""));
        settings.put(BORDER, new Setting(BORDER, true, ""));
        settings.put(MODE2D, new Setting(MODE2D, new Options("Border Mode", "Box", new String[] {"Box", "Corner", "Apex"}), "ESP method."));
        settings.put(COLOR, new Setting(COLOR, new Options("Color", "Rainbow", new String[] {"Rainbow", "Team", "Shotbow"}), "ESP method."));
        settings.put(MODE, new Setting(MODE, new Options("Mode", "2D", new String[] {"2D", "Box"}), "ESP method."));
    }

    @EventTarget
    public void onRender2D(EventRender2D render) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();
        String colormode = ((Options) settings.get(COLOR).getValue()).getSelected();

        switch (currentmode) {
            case "2D":
                GL11.glPushMatrix();
                collectEntities();
                float partialTicks = render.getPartialTicks();
                ScaledResolution scaledResolution = render.getResolution();
                int scaleFactor = scaledResolution.getScaleFactor();
                double scaling = scaleFactor / Math.pow(scaleFactor, 2.0D);
                GL11.glScaled(scaling, scaling, scaling);
                int black = this.black;
                int color = this.color;
                int background = this.backgroundColor;
                float scale = 0.65F;
                float upscale = 1.0F / scale;
                FontRenderer fr = mc.fontRendererObj;
                CFontRenderer font1 = Fonts.default12;
                CFontRenderer font2 = Fonts.default20;
                CFontRenderer font3 = Fonts.default10;
                RenderManager renderMng = mc.getRenderManager();
                EntityRenderer entityRenderer = mc.entityRenderer;
                boolean tag = (Boolean) settings.get(TAGS).getValue();
                boolean outline = (Boolean) settings.get(BORDER).getValue();
                boolean health = (Boolean) settings.get(HEALTH).getValue();
                boolean armor = (Boolean) settings.get(ARMOR).getValue();
                String mode = ((Options) settings.get(MODE2D).getValue()).getSelected();
                List<Entity> collectedEntities = this.collectedEntities;

                for (int i = 0, collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; i++) {
                    Entity entity = collectedEntities.get(i);
                    if (isValid(entity) && RenderingUtils.isInViewFrustrum(entity)) {
                        double x = RenderingUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                        double y = RenderingUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                        double z = RenderingUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                        double width = entity.width / 1.5D;
                        double height = entity.height + ((entity.isSneaking() || (entity == mc.thePlayer && Existent.getModuleManager().isEnabled(Sneak.class))) ? -0.3D : 0.2D);
                        AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                        Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
                        entityRenderer.setupCameraTransform(partialTicks, 0);
                        Vector4d position = null;
                        for (Vector3d vector : vectors) {
                            vector = project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                            if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                                if (position == null)
                                    position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                                position.x = Math.min(vector.x, position.x);
                                position.y = Math.min(vector.y, position.y);
                                position.z = Math.max(vector.x, position.z);
                                position.w = Math.max(vector.y, position.w);
                            }
                        }

                        if (position != null) {
                            entityRenderer.setupOverlayRendering();
                            double posX = position.x;
                            double posY = position.y;
                            double endPosX = position.z;
                            double endPosY = position.w;
                            if (outline) {
                                switch (mode) {
                                    case "Box":
                                        RenderingUtils.drawRect(posX - 1.0D, posY, posX + 0.5D, endPosY + 0.5D, black);
                                        RenderingUtils.drawRect(posX - 1.0D, posY - 0.5D, endPosX + 0.5D, posY + 0.5D + 0.5D, black);
                                        RenderingUtils.drawRect(endPosX - 0.5D - 0.5D, posY, endPosX + 0.5D, endPosY + 0.5D, black);
                                        RenderingUtils.drawRect(posX - 1.0D, endPosY - 0.5D - 0.5D, endPosX + 0.5D, endPosY + 0.5D, black);
                                        RenderingUtils.drawRect(posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, color);
                                        RenderingUtils.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                                        RenderingUtils.drawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                                        RenderingUtils.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);
                                        break;
                                    case "Corner":
                                        RenderingUtils.drawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                                        RenderingUtils.drawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                                        RenderingUtils.drawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                                        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                                        RenderingUtils.drawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                                        RenderingUtils.drawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                                        RenderingUtils.drawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                                        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                                        RenderingUtils.drawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, color);
                                        RenderingUtils.drawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, color);
                                        RenderingUtils.drawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, color);
                                        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, color);
                                        RenderingUtils.drawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, color);
                                        RenderingUtils.drawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, color);
                                        RenderingUtils.drawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, color);
                                        RenderingUtils.drawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, color);
                                        break;
                                    case "Apex":
                                        RenderingUtils.drawRect(endPosX - .5 - .5, posY, endPosX + .5, endPosY + .5, black);
                                        RenderingUtils.drawRect(posX - 1, posY, posX + .5, endPosY + .5, black);

                                        RenderingUtils.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 4 + .5, endPosY + .5, black);
                                        RenderingUtils.drawRect(endPosX - 1, endPosY - 1, endPosX + (posX - endPosX) / 4 - .5, endPosY + .5, black);
                                        RenderingUtils.drawRect(posX - 1, posY - .5, posX + (endPosX - posX) / 4 + .5, posY + 1, black);
                                        RenderingUtils.drawRect(endPosX, posY - .5, endPosX + (posX - endPosX) / 4 - .5, posY + 1, black);

                                        RenderingUtils.drawRect(posX - .5, posY, posX + .5 - .5, endPosY, color);
                                        RenderingUtils.drawRect(endPosX - .5, posY, endPosX, endPosY, color);

                                        RenderingUtils.drawRect(posX, endPosY - .5, posX + (endPosX - posX) / 4, endPosY, color);
                                        RenderingUtils.drawRect(endPosX, endPosY - .5, endPosX + (posX - endPosX) / 4, endPosY, color);
                                        RenderingUtils.drawRect(posX - .5, posY, posX + (endPosX - posX) / 4, posY + .5, color);
                                        RenderingUtils.drawRect(endPosX, posY, endPosX + (posX - endPosX) / 4, posY + .5, color);
                                        break;
                                }
                            }
                            boolean living = entity instanceof EntityLivingBase;
                            if (living) {
                                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                                if (health) {
                                    float hp = entityLivingBase.getHealth();
                                    float maxHealth = entityLivingBase.getMaxHealth();
                                    if (hp > maxHealth)
                                        hp = maxHealth;
                                    double hpPercentage = (hp / maxHealth);
                                    double hpHeight = (endPosY - posY) * hpPercentage;
                                    RenderingUtils.drawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, background);
                                    if (hp > 0.0F) {
                                        float absorption = entityLivingBase.getAbsorptionAmount();
                                        int healthColor = Colors.getHealthColor(hp, maxHealth).getRGB();
                                        RenderingUtils.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - hpHeight, healthColor);
                                        if (mc.thePlayer.getDistanceToEntity(entityLivingBase) < 20)
                                            font3.drawStringWithShadow("" + MathHelper.floor(hp), posX - 8.0D, endPosY - hpHeight, healthColor);
                                        if (absorption > 0.0F)
                                            RenderingUtils.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - (endPosY - posY) / 6.0D * absorption / 2.0D, new Color(255, 215, 77, 150).getRGB());
                                    }
                                }
                            }
                            if (tag && !(Existent.getModuleManager().isEnabled(NameTags.class) || Existent.getModuleManager().isEnabled(NameTags2.class))) {
                                float scaledHeight = 10.0F;
                                int texcolor = -1;
                                String name = entity.getName();
                                if (entity instanceof EntityItem) {
                                    name = ((EntityItem) entity).getEntityItem().getDisplayName();
                                } else if ((Boolean) Existent.getModuleManager().getClazz(StreamerMode.class).getSetting(StreamerMode.SCRAMBLE).getValue()) {
                                    String newstr = "";
                                    char[] rdm = {'|', '|', '|', '|', '|', '|'};
                                    for(int c = 0; c < name.length(); c++){
                                        char ch = rdm[KillAura.randomNumber(rdm.length-1, 0)];
                                        newstr = newstr.concat(ch + "");
                                    }
                                    name = "§r" + newstr;
                                }
                                String prefix = "";
                                switch (colormode) {
                                    case "Rainbow":
                                        texcolor = Colors.rainbow(1400, 1.0F, 1.0F);
                                        break;
                                    case "Team":
                                        texcolor = MiscUtils.isTeams((EntityPlayer) entity) ? Colors.getColor(255, 60, 60) : Colors.getColor(60, 255, 60);
                                        break;
                                    case "Shotbow":
                                        if (mc.getCurrentServerData() != null)
                                            texcolor = Colors.getTeamColor(entity);
                                        break;
                                }
                                if (Existent.getFriendManager().isFriend(entity.getName())) {
                                    prefix = "§f[§aF§f] ";
                                }

                                double dif = (endPosX - posX) / 2.0D;
                                double textWidth = (font2.getStringWidth(name + " §7" + (int) mc.thePlayer.getDistanceToEntity(entity) + "m") * scale);
                                float tagX = (float) ((posX + dif - textWidth / 2.0D) * upscale);
                                float tagY = (float) (posY * upscale) - scaledHeight;
                                GL11.glPushMatrix();
                                GL11.glScalef(scale, scale, scale);
                                if (living)
                                    RenderingUtils.drawRect((tagX - 2.0F), (tagY - 2.0F), tagX + textWidth * upscale + 2.0D, (tagY + 9.0F), (new Color(0, 0, 0, 140)).getRGB());
                                font2.drawStringWithShadow(prefix + name + " §7" + (int) mc.thePlayer.getDistanceToEntity(entity) + "m", tagX, tagY, texcolor);
                                GL11.glPopMatrix();
                            }
                            if (armor) {
                                if (living) {
                                    if (entity instanceof EntityPlayer) {
                                        EntityPlayer player = (EntityPlayer) entity;
                                        double ydiff = (endPosY - posY) / 4;

                                        ItemStack stack = (player).getEquipmentInSlot(4);
                                        if (stack != null) {
                                            RenderingUtils.drawRect(endPosX + 3.5D, posY - 0.5D, endPosX + 1.5D, posY + ydiff, background);
                                            double diff1 = (posY + ydiff - 1) - (posY + 2);
                                            double percent = 1 - (double) stack.getItemDamage() / (double) stack.getMaxDamage();
                                            RenderingUtils.drawRect(endPosX + 3.0D, posY + ydiff, endPosX + 2.0D, posY + ydiff - 3.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                            String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack.getItem() instanceof ItemArmor) ? stack.getDisplayName() : stack.getMaxDamage() - stack.getItemDamage() + "";
                                            if (mc.thePlayer.getDistanceToEntity(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack, endPosX + 4, posY + ydiff - 1 - (diff1 / 2) - 18);
                                                font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) (posY + ydiff - 1 - (diff1 / 2)) - (font1.getStringHeight(stack.getMaxDamage() - stack.getItemDamage() + "") / 2), -1);
                                            }
                                        }
                                        ItemStack stack2 = (player).getEquipmentInSlot(3);
                                        if (stack2 != null) {
                                            RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff, endPosX + 1.5D, posY + ydiff * 2, background);
                                            double diff1 = (posY + ydiff * 2) - (posY + ydiff + 2);
                                            double percent = 1 - (double) stack2.getItemDamage() * 1 / (double) stack2.getMaxDamage();
                                            RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 2), endPosX + 2.0D, (posY + ydiff * 2) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                            String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack2.getItem() instanceof ItemArmor) ? stack2.getDisplayName() : stack2.getMaxDamage() - stack2.getItemDamage() + "";
                                            if (mc.thePlayer.getDistanceToEntity(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack2, endPosX + 4, (posY + ydiff * 2) - (diff1 / 2) - 18);
                                                font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 2) - (diff1 / 2)) - (font1.getStringHeight(stack2.getMaxDamage() - stack2.getItemDamage() + "") / 2), -1);
                                            }
                                        }
                                        ItemStack stack3 = (player).getEquipmentInSlot(2);
                                        if (stack3 != null) {
                                            RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff * 2, endPosX + 1.5D, posY + ydiff * 3, background);
                                            double diff1 = (posY + ydiff * 3) - (posY + ydiff * 2 + 2);
                                            double percent = 1 - (double) stack3.getItemDamage() * 1 / (double) stack3.getMaxDamage();
                                            RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 3), endPosX + 2.0D, (posY + ydiff * 3) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                            String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack3.getItem() instanceof ItemArmor) ? stack3.getDisplayName() : stack3.getMaxDamage() - stack3.getItemDamage() + "";
                                            if (mc.thePlayer.getDistanceToEntity(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack3, endPosX + 4, (posY + ydiff * 3) - (diff1 / 2) - 18);
                                                font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 3) - (diff1 / 2)) - (font1.getStringHeight(stack3.getMaxDamage() - stack3.getItemDamage() + "") / 2), -1);
                                            }
                                        }
                                        ItemStack stack4 = (player).getEquipmentInSlot(1);
                                        if (stack4 != null) {
                                            RenderingUtils.drawRect(endPosX + 3.5D, posY + ydiff * 3, endPosX + 1.5D, posY + ydiff * 4 + 0.5D, background);
                                            double diff1 = (posY + ydiff * 4) - (posY + ydiff * 3 + 2);
                                            double percent = 1 - (double) stack4.getItemDamage() * 1 / (double) stack4.getMaxDamage();
                                            RenderingUtils.drawRect(endPosX + 3.0D, (posY + ydiff * 4), endPosX + 2.0D, (posY + ydiff * 4) - 1.0D - (diff1 * percent), new Color(78, 206, 229).getRGB());

                                            String stackname = (stack.getDisplayName().equalsIgnoreCase("Air")) ? "0" : !(stack4.getItem() instanceof ItemArmor) ? stack4.getDisplayName() : stack4.getMaxDamage() - stack4.getItemDamage() + "";
                                            if (mc.thePlayer.getDistanceToEntity(player) < 20) {
//                                        mc.getRenderItem().renderItemIntoGUI(stack4, endPosX + 4, (posY + ydiff * 4) - (diff1 / 2) - 18);
                                                font1.drawStringWithShadow(stackname, (float) endPosX + 5, (float) ((posY + ydiff * 4) - (diff1 / 2)) - (font1.getStringHeight(stack4.getMaxDamage() - stack4.getItemDamage() + "") / 2), -1);
                                            }
                                        }
                                    }
                                } else if (entity instanceof EntityItem) {
                                    ItemStack itemStack = ((EntityItem) entity).getEntityItem();
                                    if (itemStack.isItemStackDamageable()) {
                                        int maxDamage = itemStack.getMaxDamage();
                                        float itemDurability = (maxDamage - itemStack.getItemDamage());
                                        double durabilityWidth = (endPosX - posX) * itemDurability / maxDamage;
                                        RenderingUtils.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, background);
                                        RenderingUtils.drawRect(posX, endPosY + 2.0D, posX + durabilityWidth, endPosY + 3.0D, 16777215);
                                    }
                                }
                            }
                        }
                    }
                }
                GL11.glPopMatrix();
                GlStateManager.enableBlend();
                entityRenderer.setupOverlayRendering();
                break;
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D render) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();
        String colormode = ((Options) settings.get(COLOR).getValue()).getSelected();

        switch (currentmode) {
            case "Box":
                for (Object obj : mc.theWorld.loadedEntityList) {
                    Entity entity = (Entity) obj;
                    if (isValid(entity)) {
                        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
                        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
                        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                        double widthX = (entity.boundingBox.maxX - entity.boundingBox.minX) / 2 + 0.05;
                        double widthZ = (entity.boundingBox.maxZ - entity.boundingBox.minZ) / 2 + 0.05;
                        double height = (entity.boundingBox.maxY - entity.boundingBox.minY);

                        switch (colormode) {
                            case "Rainbow":
                                this.color = Colors.rainbow(1400, 1.0F, 1.0F);
                                break;
                            case "Team":
                                this.color = MiscUtils.isTeams((EntityPlayer) entity) ? Colors.getColor(255, 60, 60) : Colors.getColor(60, 255, 60);
                                break;
                            case "Shotbow":
                                if (entity.getDisplayName().getUnformattedText().equalsIgnoreCase("§f[§cR§f]§c" + entity.getName())) {
                                    this.color = Colors.getColor(255, 60, 60);
                                } else if (entity.getDisplayName().getUnformattedText().equalsIgnoreCase("§f[§9B§f]§9" + entity.getName())) {
                                    this.color = Colors.getColor(60, 60, 255);
                                } else if (entity.getDisplayName().getUnformattedText().equalsIgnoreCase("§f[§eY§f]§e" + entity.getName())) {
                                    this.color = Colors.getColor(255, 255, 60);
                                } else if (entity.getDisplayName().getUnformattedText().equalsIgnoreCase("§f[§aG§f]§a" + entity.getName())) {
                                    this.color = Colors.getColor(60, 255, 60);
                                } else {
                                    this.color = Colors.getColor(255, 255, 255);
                                }
                                break;
                        }

                        if (entity instanceof EntityPlayer)
                            height *= 1.1;

                        RenderingUtils.pre3D();
                        RenderingUtils.glColor(this.color);
                        for (int i = 0; i < 2; i++) {
                            GL11.glLineWidth(1);
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex3d(x - widthX, y, z - widthZ);
                            GL11.glVertex3d(x - widthX, y, z - widthZ);
                            GL11.glVertex3d(x - widthX, y + height, z - widthZ);
                            GL11.glVertex3d(x + widthX, y + height, z - widthZ);
                            GL11.glVertex3d(x + widthX, y, z - widthZ);
                            GL11.glVertex3d(x - widthX, y, z - widthZ);
                            GL11.glVertex3d(x - widthX, y, z + widthZ);
                            GL11.glEnd();
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex3d(x + widthX, y, z + widthZ);
                            GL11.glVertex3d(x + widthX, y + height, z + widthZ);
                            GL11.glVertex3d(x - widthX, y + height, z + widthZ);
                            GL11.glVertex3d(x - widthX, y, z + widthZ);
                            GL11.glVertex3d(x + widthX, y, z + widthZ);
                            GL11.glVertex3d(x + widthX, y, z - widthZ);
                            GL11.glEnd();
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex3d(x + widthX, y + height, z + widthZ);
                            GL11.glVertex3d(x + widthX, y + height, z - widthZ);
                            GL11.glEnd();
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex3d(x - widthX, y + height, z + widthZ);
                            GL11.glVertex3d(x - widthX, y + height, z - widthZ);
                            GL11.glEnd();
                        }
                        RenderingUtils.post3D();
                    }
                }
                break;
        }
    }
    @EventTarget
    public void onRenderNameTags(EventNameTag event) {
        if ((Boolean) settings.get(TAGS).getValue() && ((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("2D")) {
            event.setCancelled(true);
        }
    }

    private void collectEntities() {
        this.collectedEntities.clear();
        List<Entity> playerEntities = mc.theWorld.loadedEntityList;
        for (int i = 0, playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; i++) {
            Entity entity = playerEntities.get(i);
            if (isValid(entity))
                this.collectedEntities.add(entity);
        }
    }
    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.vector))
            return new Vector3d((this.vector.get(0) / scaleFactor), ((Display.getHeight() - this.vector.get(1)) / scaleFactor), this.vector.get(2));
        return null;
    }
    private boolean isValid(Entity entity) {
        if (entity == mc.thePlayer && (mc.gameSettings.thirdPersonView == 0))
            return false;
        if (entity.isDead)
            return false;
        if (!(Boolean) settings.get(INVISIBLES).getValue() && entity.isInvisible())
            return false;
        if ((Boolean) settings.get(ITEMS).getValue() && entity instanceof EntityItem && mc.thePlayer.getDistanceToEntity(entity) < 10.0F)
            return true;
        if ((Boolean) settings.get(ANIMALS).getValue() && entity instanceof net.minecraft.entity.passive.EntityAnimal)
            return true;
        if ((Boolean) settings.get(PLAYERS).getValue() && entity instanceof EntityPlayer)
            return true;
        return ((Boolean) settings.get(MONSTERS).getValue() && (entity instanceof net.minecraft.entity.monster.EntityMob || entity instanceof net.minecraft.entity.monster.EntitySlime || entity instanceof net.minecraft.entity.boss.EntityDragon || entity instanceof net.minecraft.entity.monster.EntityGolem));
    }
}
