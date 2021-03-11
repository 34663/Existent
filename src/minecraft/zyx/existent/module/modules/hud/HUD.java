package zyx.existent.module.modules.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import optifine.CustomColors;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.module.modules.movement.Scaffold;
import zyx.existent.module.modules.visual.Cosmetics;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MathUtils;
import zyx.existent.utils.misc.LoginUtils;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.Translate;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class HUD extends Module {
    private double lastPosX = Double.NaN;
    private double lastPosZ = Double.NaN;
    private final ArrayList<Double> distances = new ArrayList<Double>();

    public final static String CLIENTTEXT = "CLIENTNAME";
    private final String BACKGROUND = "BACKGROUND";
    private final String OPACITY = "OPACITY";
    private final String INVERSION = "INVERSION";
    private final String OUTLINE = "OUTLINE";
    private final String HOTBAR = "HOTBAR";
    private final String COLOR = "COLOR";
    private final String INFO = "INFO";

    private final CFontRenderer font = Fonts.default18;
    private final CFontRenderer font2 = Fonts.default20;
    private final CFontRenderer font3 = Fonts.elliot18;

    public static boolean hotbar;

    public HUD(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(COLOR, new Setting(COLOR, new Options("Color", "Pulsing", new String[]{"Default", "Astolfo", "Rainbow", "Pulsing", "Category", "Flower", "Candy"}), "HUD method"));
        settings.put(CLIENTTEXT, new Setting(CLIENTTEXT, Existent.CLIENT_NAME, "ClientName"));
        settings.put(BACKGROUND, new Setting(BACKGROUND, true, ""));
        settings.put(OUTLINE, new Setting(OUTLINE, true, ""));
        settings.put(INFO, new Setting(INFO, true, "DrawInfo"));
        settings.put(INVERSION, new Setting(INVERSION, false, "Inversion Color"));
        settings.put(HOTBAR, new Setting(HOTBAR, false, "Black Hotbar"));
        settings.put(OPACITY, new Setting(OPACITY, 255, "HudOpacity", 1, 1, 255));
    }

    @EventTarget
    public void onRender2D(EventRender2D render) {
        float height = 10;
        String name = (String) settings.get(CLIENTTEXT).getValue();
        boolean info = (Boolean) settings.get(INFO).getValue();
        hotbar = (Boolean) settings.get(HOTBAR).getValue();
        String fps = "FPS: \2477" + mc.getDebugFPS();
        String ping = "PING: \2477" + ((mc.getCurrentServerData() != null) ? mc.getCurrentServerData().pingToServer : 0);
        String coord = "XYZ: \2477" + MathHelper.floor(this.mc.thePlayer.posX) + " / " + MathHelper.floor(this.mc.thePlayer.posY) + " / " + MathHelper.floor(this.mc.thePlayer.posZ);
        String blockps = "Blocks P/s: \2477" + String.valueOf(MiscUtils.round(getDistTraveled(), 2));
        String uid = "UID: \2477#" + LoginUtils.code;
        String build = "Build: \2477" + Existent.CLIENT_BUILD;
        name = name.substring(0, 1).replaceAll(name.substring(0, 1), "\247c" + name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));

        if (!mc.gameSettings.showDebugInfo) {
            font2.drawStringWithShadow(name, 3, 4, new Color(255, 50, 50).getRGB());
            if (mc.currentScreen instanceof GuiChat) {
                height += 14;
            }
            if (info) {
                font.drawStringWithShadow(fps, 3, render.getResolution().getScaledHeight() - height, -1);
                font.drawStringWithShadow(coord, font.getStringWidth(fps) + 6, render.getResolution().getScaledHeight() - height, -1);
                font.drawStringWithShadow(ping, 3, render.getResolution().getScaledHeight() - height - font.getHeight() - 2, -1);
                font.drawStringWithShadow(blockps, font.getStringWidth(ping) + 6, render.getResolution().getScaledHeight() - height - font.getHeight() - 2, -1);
            }
            font.drawStringWithShadow(uid, render.getResolution().getScaledWidth() - font.getStringWidth(uid) - 2, render.getResolution().getScaledHeight() - height, -1);
            font.drawStringWithShadow(build, render.getResolution().getScaledWidth() - font.getStringWidth(uid) - font.getStringWidth(build) - 6, render.getResolution().getScaledHeight() - height, -1);

            this.drawPotionStatus(render.getResolution());
            this.drawGaeHud(render.getResolution());
        }

        Display.setTitle((String) settings.get(CLIENTTEXT).getValue());
    }

    private void drawGaeHud(ScaledResolution sr) {
        boolean inversion = (Boolean) settings.get(INVERSION).getValue();
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        ArrayList<Module> sortedList = getSortedModules(font3);
        int listOffset = 10, y = 1;
        int[] counter = {1};

        GL11.glEnable(3042);
        for (int i = 0, sortedListSize = sortedList.size(); i < sortedListSize; i++) {
            Module module = sortedList.get(i);
            Translate translate = module.getTranslate();

            module.setDisplayName(inversion ? "\247f" + module.getName() : module.getName());
            for (Setting setting : module.getSettings().values()) {
                if (module.getSuffix() != null) {
                    module.setDisplayName(inversion ? ("\247f" + module.getName() + " \247r" + module.getSuffix()) : (module.getName() + " \247f" + module.getSuffix()));
                } else if (setting.getValue() instanceof Options && !(module instanceof HUD || module instanceof StreamerMode || module instanceof Cosmetics || module instanceof Scaffold || module instanceof TabGui)) {
                    String settingValue = ((Options) setting.getValue()).getSelected();
                    module.setDisplayName(inversion ? ("\247f" + module.getName() + " \247r" + settingValue) : (module.getName() + " \247f" + settingValue));
                }
            }
            String moduleLabel = module.getDisplayName();
            float length = font3.getStringWidth(moduleLabel);
            float featureX = width - length - 3.0F;
            boolean enable = module.isEnabled();
            if (enable) {
                translate.interpolate(featureX, y, 7);
            } else {
                translate.interpolate(width + 3, y, 7);
            }
            double translateX = translate.getX();
            double translateY = translate.getY();
            boolean visible = ((translateX > -listOffset));
            if (visible) {
                int color = -1;
                switch (((Options) settings.get(COLOR).getValue()).getSelected()) {
                    case "Default":
                        color = new Color(255, 50, 50).getRGB();
                        break;
                    case "Rainbow":
                        color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
                        break;
                    case "Pulsing":
                        color = TwoColoreffect(new Color(255, 50, 50), new Color(79, 9, 9), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                    case "Category":
                        color = module.getCategoryColor();
                        break;
                    case "Candy":
                        color = TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                        break;
                    case "Flower":
                        color = new Color(20, 255, 20).getRGB();
                        break;
                    case "Astolfo":
                        float speed = 6000f;
                        float hue = (System.currentTimeMillis() % (int) speed) + (y * 20F);
                        while (hue > speed) {
                            hue -= speed;
                        }
                        hue /= speed;
                        if (hue > 0.5) {
                            hue = 0.5F - (hue - 0.5f);
                        }
                        hue += 0.5F;
                        color = Color.HSBtoRGB(hue, 0.6F, 1F);
                        break;
                }
                int nextIndex = sortedList.indexOf(module) + 1;
                Module nextModule = null;
                if (sortedList.size() > nextIndex)
                    nextModule = getNextEnabledModule(sortedList, nextIndex);
                if ((Boolean) settings.get(BACKGROUND).getValue())
                    RenderingUtils.drawRect(translateX - 2.0D, translateY - 1.0D, width, translateY + listOffset - 1.0D, Colors.getColor(0, ((Number) settings.get(OPACITY).getValue()).intValue()));
                if ((Boolean) settings.get(OUTLINE).getValue()) {
                    RenderingUtils.drawRect(translateX - 2.6D, translateY - 1.0D, translateX - 2.0D, translateY + listOffset - 1.0D, color);
                    double offsetY = listOffset;
                    if (nextModule != null) {
                        double dif = (length - font3.getStringWidth(nextModule.getDisplayName()));
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, translateX - 2.6D + dif, translateY + offsetY - 0.5D, color);
                    } else {
                        RenderingUtils.drawRect(translateX - 2.6D, translateY + offsetY - 1.0D, width, translateY + offsetY - 0.6D, color);
                    }
                }

                font3.drawStringWithShadow(moduleLabel, (float) translateX, (float) translateY + 2, color);

                if (module.isEnabled()) {
                    y += listOffset;
                    counter[0] -= 1F;
                }
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosZ)) {
            double differenceX = Math.abs(lastPosX - mc.thePlayer.posX);
            double differenceZ = Math.abs(lastPosZ - mc.thePlayer.posZ);
            double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ) * 2;

            distances.add(distance);
            if (distances.size() > 20)
                distances.remove(0);
        }

        lastPosX = mc.thePlayer.posX;
        lastPosZ = mc.thePlayer.posZ;
    }

    private void drawPotionStatus(ScaledResolution sr) {
        float pY = (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat)) ? -26 : -12;
        List<PotionEffect> potions = new ArrayList<>();

        for (Object o : mc.thePlayer.getActivePotionEffects())
            potions.add((PotionEffect) o);
        potions.sort(Comparator.comparingDouble(effect -> -mc.fontRendererObj.getStringWidth(I18n.format(Potion.getPotionById(CustomColors.getPotionId(effect.getEffectName())).getName()))));

        for (PotionEffect effect : potions) {
            Potion potion = Potion.getPotionById(CustomColors.getPotionId(effect.getEffectName()));
            String name = I18n.format(potion.getName());
            String PType = "";
            if (effect.getAmplifier() == 1) {
                name = name + " II";
            } else if (effect.getAmplifier() == 2) {
                name = name + " III";
            } else if (effect.getAmplifier() == 3) {
                name = name + " IV";
            }
            if ((effect.getDuration() < 600) && (effect.getDuration() > 300)) {
                PType = PType + "\2476 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = PType + "\247c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = PType + "\2477 " + Potion.getDurationString(effect);
            }
            font.drawStringWithShadow(name, sr.getScaledWidth() - font.getStringWidth(name + PType) - 3, sr.getScaledHeight() - 9 + pY, potion.getLiquidColor());
            font.drawStringWithShadow(PType, sr.getScaledWidth() - font.getStringWidth(PType) - 2, sr.getScaledHeight() - 9 + pY, -1);
            pY -= 9;
        }
    }

    private Module getNextEnabledModule(ArrayList<Module> modules, int startingIndex) {
        for (int i = startingIndex, modulesSize = modules.size(); i < modulesSize; i++) {
            Module module = modules.get(i);
            if (module.isEnabled())
                return module;
        }
        return null;
    }
    private ArrayList<Module> getSortedModules(CFontRenderer fr) {
        ArrayList<Module> sortedList = new ArrayList<>(Existent.getModuleManager().getModules());
        sortedList.removeIf(Module::isVisible);
        sortedList.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(e.getDisplayName())));
        return sortedList;
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
    public double getDistTraveled() {
        double total = 0;
        for (double d : distances) {
            total += d;
        }
        return total;
    }
}
