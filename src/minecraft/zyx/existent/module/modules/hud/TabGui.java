package zyx.existent.module.modules.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventKey;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.ModuleComparator;
import zyx.existent.module.data.ModuleComparator2;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MathUtils;
import zyx.existent.utils.StringConversions;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.AnimationUtil;
import zyx.existent.utils.render.animate.Translate;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import zyx.existent.utils.timer.Timer;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;
import static org.lwjgl.opengl.GL11.*;

public class TabGui extends Module {
    private final String COLOR = "COLOR";
    ArrayList<Module> modules = new ArrayList<>();
    List<Setting> settingList = new CopyOnWriteArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    Timer timer = new Timer();
    private final CFontRenderer font = Fonts.default18;
    private final CFontRenderer icon = Fonts.ICON;
    public static float categoryW = 55 * 1.4F, moduleW = 55 * 1.4F, settingW = 55 * 1.4F, insetW = 10 * 1.4F, boxH = 12.5f * 1.4F, x = categoryW, descX = categoryW, X = 0.0F;
    int y1 = 0, y2 = 0, y3 = 0, y4 = 0, opa = 0;
    int col = 1;
    float h;

    public TabGui(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(COLOR, new Setting(COLOR, new Options("Color", "Red", new String[]{"Red", "Blue", "Green", "Rainbow"}), "HUD method"));
        categories.addAll(Arrays.asList(Category.values()));
    }

    public static Translate opacity = new Translate(0, 0);
    private Translate category = new Translate(0, 0);
    private Translate module = new Translate(0, 0);
    private Translate setting = new Translate(0, 0);
    private Translate inset = new Translate(0, 0);
    private Translate collum = new Translate(0, 0);
    private Translate desc = new Translate(0, 0);
    private Translate catest = new Translate(0, 0);

    @EventTarget
    public void onRender2D(EventRender2D render) {
        if (!mc.gameSettings.showDebugInfo) {
            drawNigaTab(render);
        }
    }
    @EventTarget
    public void onTick(EventTick eventTick) {
        updateModuleWidth();
        updateSetWidth();
        if (col == 1) {
            x = categoryW;
        } else if (col == 2) {
            x = categoryW + moduleW;
        } else if (col == 3) {
            x = categoryW + moduleW + settingW;
        }
    }
    @EventTarget
    public void onKeyPress(EventKey ek) {
        int key = ek.getKey();
        if (keyCheck(key)) {
            timer.reset();
        }
        if (key == Keyboard.KEY_DOWN) {
            if (col == 1) {
                y1 += 1;
                if (y1 > categories.size() - 1) {
                    y1 = 0;
                }
            } else if (col == 2) {
                if (modules.size() > 1)
                    desc = new Translate((moduleW), 0);
                y2 += 1;
                if (y2 > modules.size() - 1) {
                    y2 = 0;
                }
            } else if (col == 3) {
                if (settingList.size() > 1)
                    desc = new Translate((moduleW + settingW), 0);
                y3 += 1;
                if (y3 > settingList.size() - 1) {
                    y3 = 0;
                }
            }
        } else if (key == Keyboard.KEY_UP) {
            if (col == 1) {
                y1 -= 1;
                if (y1 < 0) {
                    y1 = categories.size() - 1;
                }
            } else if (col == 2) {
                if (modules.size() > 1)
                    desc = new Translate((moduleW), 0);
                y2 -= 1;
                if (y2 < 0) {
                    y2 = modules.size() - 1;
                }
            } else if (col == 3) {
                if (settingList.size() > 1)
                    desc = new Translate((moduleW + settingW), 0);
                y3 -= 1;
                if (y3 < 0) {
                    y3 = settingList.size() - 1;
                }
            }
        } else if (key == Keyboard.KEY_LEFT) {
            if (col > 1) {
                col--;

                if (col == 1) {
                    x -= moduleW;
                } else if (col == 2) {
                    x -= settingW;
                } else if (col == 3) {
                    x -= insetW;
                }
            }
        } else if (key == Keyboard.KEY_RIGHT) {
            if (col < 4) {
                if (col == 1) {
                    module = new Translate(0, 3);
                    y2 = 0;
                    Category cat = Category.values()[y1];
                    modules = Existent.getModuleManager().getModuleByCategory(cat);
                } else if (col == 2) {
                    setting = new Translate(0, 3);
                    y3 = 0;
                    Category cat = Category.values()[y1];
                    Module mod = Existent.getModuleManager().getModuleByCategory(cat).get(y2);

                    if (mod != null) {
                        List<Setting> add = getSettings(mod);
                        settingList.clear();
                        if (add != null && add.size() > 0) {
                            desc = new Translate(0, 0);
                            settingList.addAll(add);
                        }
                    }
                }
                if (col != 3) {
                    updateModuleWidth();
                    updateSetWidth();
                }
                if (col == 1) {
                    x += moduleW;
                    col++;
                } else if (col == 2 && settingList.size() > 0) {
                    x += settingW;
                    col++;
                } else if (col == 3) {
                    x += insetW;
                    y4 = y3;
                    col++;
                }
            }
        } else if (key == Keyboard.KEY_RETURN) {
            if (col == 2) {
                Module mod = Existent.getModuleManager().getModuleByCategory(Category.values()[y1]).get(y2);
                mod.toggle();
            }
        }
        if (col == 4) {
            if (ek.getKey() == Keyboard.KEY_LEFT) {
                ;
            } else if (ek.getKey() == Keyboard.KEY_UP) {
                Category cat = Category.values()[y1];
                Module mod = Existent.getModuleManager().getModuleByCategory(cat).get(y2);
                Setting set = getSettings(mod).get(y3);
                String WIDTH = set.getName();
                if (set.getValue() instanceof Number) {
                    double increment = (set.getInc());
                    String str = MathUtils.isInteger(MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() + increment),
                            increment)) ? (MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() + increment), increment) + "")
                            .replace(".0", "")
                            : MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() + increment), increment)
                            + "";
                    if (Double.parseDouble(str) > set.getMax() && set.getInc() != 0) {
                        return;
                    }
                    Object newValue = (StringConversions.castNumber(str, increment));
                    if (newValue != null) {
                        set.setValue(newValue);
                    }
                } else if (set.getValue().getClass().equals(Boolean.class)) {
                    boolean xd = ((Boolean) set.getValue());
                    set.setValue(!xd);
                } else if (set.getValue() instanceof Options) {
                    List<String> options = new CopyOnWriteArrayList<>();
                    Collections.addAll(options, ((Options) set.getValue()).getOptions());
                    for (int i = 0; i <= options.size() - 1; i++) {
                        if (options.get(i).equalsIgnoreCase(((Options) set.getValue()).getSelected())) {
                            if (i + 1 > options.size() - 1) {
                                ((Options) set.getValue()).setSelected(options.get(0));
                            } else {
                                ((Options) set.getValue()).setSelected(options.get(i + 1));
                            }
                            break;
                        }
                    }
                }
            } else if (ek.getKey() == Keyboard.KEY_DOWN) {
                Category cat = Category.values()[y1];
                Module mod = Existent.getModuleManager().getModuleByCategory(cat).get(y2);
                Setting set = getSettings(mod).get(y3);
                String WIDTH = set.getName();
                if (set.getValue() instanceof Number) {
                    double increment = (set.getInc());

                    String str = MathUtils.isInteger(MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment),
                            increment)) ? (MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment), increment) + "")
                            .replace(".0", "")
                            : MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment), increment)
                            + "";
                    if (Double.parseDouble(str) < set.getMin() && increment != 0) {
                        return;
                    }
                    Object newValue = (StringConversions.castNumber(str, increment));
                    if (newValue != null) {
                        set.setValue(newValue);
                        Module.saveSettings();
                        return;
                    }
                } else if (set.getValue().getClass().equals(Boolean.class)) {
                    boolean xd = ((Boolean) set.getValue()).booleanValue();
                    set.setValue(!xd);
                    Module.saveSettings();
                } else if (set.getValue() instanceof Options) {
                    List<String> options = new CopyOnWriteArrayList<>();
                    Collections.addAll(options, ((Options) set.getValue()).getOptions());
                    for (int i = options.size() - 1; i >= 0; i--) {
                        if (options.get(i).equalsIgnoreCase(((Options) set.getValue()).getSelected())) {
                            if (i - 1 < 0) {
                                ((Options) set.getValue()).setSelected(options.get(options.size() - 1));
                            } else {
                                ((Options) set.getValue()).setSelected(options.get(i - 1));
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    void drawNigaTab(EventRender2D er) {
        ScaledResolution res = new ScaledResolution(mc);
        float rooty = (Existent.getModuleManager().isEnabled(HUD.class) || Existent.getModuleManager().isEnabled(FlowerHUD.class)) ? -16 : -28;
        float rootx = 0;
        boxH = 9 * 1.2F;
        category.interpolate(0, 3 + y1 * boxH, 5);
        module.interpolate(0, 3 + y2 * boxH, 5);
        setting.interpolate(0, 3 + y3 * boxH, 5);
        opacity.interpolate(opa, opa, 4);
        collum.interpolate(x * 2, 0, 4);
        desc.interpolate(descX, 0, 3);
        if (col == 2) {
            descX = categoryW + moduleW + getStringWidth(modules.get(y2).getDescription()) + 7;
        } else if (col == 3) {
            descX = categoryW + moduleW + settingW + getStringWidth(settingList.get(y3).getDesc());
        } else if (col == 4) {
            descX = categoryW + moduleW + settingW + insetW + getStringWidth(settingList.get(y3).getDesc()) + 10;
        }

        if (timer.delay(4500) || (collum.getX() != x * 2 && col != 4 && !(col == 3 && collum.getX() > x * 2))) {
            descX = col * 10;
        }
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        int s = res.getScaleFactor();
        glScissor(0, 0, (int) collum.getX() / 2 * s, 1000 * s);
        glEnable(GL_SCISSOR_TEST);
        double translateY = 29;
        double translateX = -1.8;
        GL11.glTranslated(translateX, translateY, 0);

        int moreDank = 150;
        int alpha = 200;
        int rDank = Math.max(255 - moreDank, 0);
        int gDank = 0;
        int bDank = 0;
        int dank = Colors.getColor(rDank, gDank, bDank, alpha);

        h += 0.2f;
        if (h > 255) {
            h = 0;
        }

        int hud = -1;

        switch (((Options) settings.get(COLOR).getValue()).getSelected()) {
            case "Red":
                hud = new Color(230, 20, 20, 255).getRGB();
                dank = new Color(180, 10, 10, 255).getRGB();
                break;
            case "Blue":
                hud = new Color(20, 20, 230, 255).getRGB();
                dank = new Color(10, 10, 180, 255).getRGB();
                break;
            case "Green":
                hud = new Color(20, 230, 20, 255).getRGB();
                dank = new Color(10, 180, 10, 255).getRGB();
                break;
            case "Rainbow":
                hud = Colors.rainbow(3000, 0.8f, 1.0f);
                dank = Colors.rainbow(3000, 0.8f, 0.7f);
                break;
        }

        RenderingUtils.drawRect(rootx + 3.3, rooty + 2.3, rootx + categoryW, rooty + categories.size() * boxH + 3.5, Colors.getColor(20, 240));
        RenderingUtils.drawRect(rootx + categoryW + 2.8, rooty + 2.3, rootx + categoryW + moduleW - 0.5, rooty + modules.size() * boxH + 3.5, Colors.getColor(20, 240));
        RenderingUtils.drawRect(rootx + categoryW + moduleW + 2.8, rooty + 2.3, rootx + categoryW + moduleW + settingW - 1.4, rooty + settingList.size() * boxH + 3.2, Colors.getColor(20, 240));
        RenderingUtils.drawFilledTriangle(rootx + categoryW + moduleW + settingW + 4f, rooty + y4 * boxH + 2.8f + boxH / 2, 8, dank, hud);

        RenderingUtils.drawRoundedRect(rootx + 4, rooty + category.getY() + 0.4, rootx + 5.5, rooty + category.getY() + boxH - 0.4, 1, hud);
        RenderingUtils.drawRect(rootx + 3.5 + categoryW, rooty + module.getY(), rootx + categoryW + moduleW - 0.5, rooty + module.getY() + boxH, hud);
        RenderingUtils.drawRect(rootx + 3.3 + categoryW + moduleW, rooty + setting.getY(), rootx + categoryW + moduleW + settingW - 1.5, rooty + setting.getY() + boxH - 0.4, hud);
        RenderingUtils.drawBorderRect(rootx + 3.7, rooty + 2.7, rootx + categoryW, rooty + categories.size() * boxH + 3.2, Colors.getColor(10, 155), 1);
        RenderingUtils.drawBorderRect(rootx + categoryW + 3.3, rooty + 2.6, rootx + categoryW + moduleW - 1, rooty + modules.size() * boxH + 3, Colors.getColor(10, 155), 1);
        RenderingUtils.drawBorderRect(rootx + categoryW + moduleW + 3.3, rooty + 2.6, rootx + categoryW + moduleW + settingW - 1.4, rooty + settingList.size() * boxH + 2.8, Colors.getColor(10, 155), 1);
        float Y = 5;

        int strBright = 255;
        if (!categories.isEmpty()) {
            for (Category category : categories) {
                X = (y1 == category.ordinal()) ? 2 : 0;
                GlStateManager.pushMatrix();
                GlStateManager.bindTexture(0);
                GlStateManager.enableBlend();
                switch (category) {
                    case Combat:
                        icon.drawString("a", categoryW - 12, rooty + Y + 1, -1);
                        break;
                    case Movement:
                        icon.drawString("b", categoryW - 12, rooty + Y + 1, -1);
                        break;
                    case Player:
                        icon.drawString("c", categoryW - 12, rooty + Y + 1, -1);
                        break;
                    case Visual:
                        icon.drawString("d", categoryW - 12, rooty + Y + 1, -1);
                        break;
                    case Misc:
                        icon.drawString("e", categoryW - 12, rooty + Y + 1, -1);
                        break;
                    case Other:
                        icon.drawString("f", categoryW - 12, rooty + Y + 1, -1);
                        break;
                }
                drawString(category.name(), rootx + 6 + X, rooty + Y, Colors.getColor(strBright, 255));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                Y += boxH;
            }
        }
        Y = 5;
        Existent.getModuleManager().getModules().sort(new ModuleComparator2());
        if (!modules.isEmpty()) {
            for (Module mod : modules) {
                drawString(mod.getName(), rootx + 4 + categoryW, rooty + Y, Colors.getColor(mod.isEnabled() ? strBright : strBright - 90, 255));
                Y += boxH;
            }
        }

        Y = 5;
        if (settingList != null && !modules.isEmpty() && modules != null && settingList.size() > 0) {
            for (Setting setting : settingList) {
                String xd = setting.getName().charAt(0) + setting.getName().toLowerCase().substring(1);
                String fagniger = setting.getValue() instanceof Options ? ((Options) setting.getValue()).getSelected() : setting.getValue().toString();
                drawString(xd + ": " + fagniger, rootx + 5 + categoryW + moduleW, rooty + Y, Colors.getColor(strBright, 255));
                Y += boxH;
            }
        }

        glDisable(GL_SCISSOR_TEST);
        GL11.glTranslated(translateX, -translateY, 0);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private boolean keyCheck(int key) {
        boolean active = false;
        switch (key) {
            case Keyboard.KEY_DOWN:
                active = true;
                break;
            case Keyboard.KEY_UP:
                active = true;
                break;
            case Keyboard.KEY_RETURN:
                active = true;
                break;
            case Keyboard.KEY_LEFT:
                active = true;
                break;
            case Keyboard.KEY_RIGHT:
                active = true;
                break;
            default:
                break;
        }
        return active;
    }
    private void updateModuleWidth() {
        float maxW = 0;
        if (!modules.isEmpty()) {
            for (Module module : modules) {
                if (getStringWidth(module.getName()) > maxW) {
                    maxW = getStringWidth(module.getName());
                }
            }
            moduleW = maxW + 7;
        }
    }
    private void updateSetWidth() {
        float maxW = 0;
        if (settingList != null && settingList.size() > 0) {
            for (Setting set : settingList) {
                if (set.getValue() instanceof Number) {
                    double increment = set.getInc();
                    String str = MathUtils.isInteger(MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment),
                            increment)) ? (MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment), increment) + "")
                            .replace(".0", "")
                            : MathUtils.getIncremental(
                            (((Number) (set.getValue())).doubleValue() - increment), increment)
                            + "";
                    Object newValue = (StringConversions.castNumber(str, increment));
                    if (newValue != null) {
                        if (getStringWidth(set.getName() + " " + str) > maxW) {
                            maxW = getStringWidth(set.getName() + " " + str);
                        }
                    }
                } else if (set.getValue().getClass().equals(Boolean.class)) {
                    boolean xd = ((Boolean) set.getValue()).booleanValue();
                    if (getStringWidth(set.getName() + " " + xd) > maxW) {
                        maxW = getStringWidth(set.getName() + " " + xd);
                    }
                } else if (set.getValue() instanceof Options) {
                    List<String> options = new CopyOnWriteArrayList<>();
                    Collections.addAll(options, ((Options) set.getValue()).getOptions());
                    for (int i = 0; i <= options.size() - 1; i++) {
                        if (getStringWidth(set.getName() + " " + options.get(i)) > maxW) {
                            maxW = getStringWidth(set.getName() + " " + options.get(i));
                        }
                    }
                }
            }
            settingW = maxW + 7;
        }
    }
    private int getStringWidth(String text) {
        return font.getStringWidth(text);
    }
    private void drawString(String text, float x, float y, int col) {
        font.drawString(text, x+1, y, col);
    }
    private List<Setting> getSettings(Module mod) {
        List<Setting> settings = new CopyOnWriteArrayList<>();
        settings.addAll(mod.getSettings().values());
        for (Setting setting : settings) {
            if (setting.getValue().getClass().equals(String.class)) {
                settings.remove(setting);
            }
        }
        if (settings.isEmpty()) {
            return null;
        }
        settings.sort(Comparator.comparing(Setting::getName));
        return settings;
    }
}
