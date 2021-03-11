package zyx.existent.module.modules.hud;

import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.misc.StreamerMode;
import zyx.existent.module.modules.movement.Scaffold;
import zyx.existent.module.modules.visual.Cosmetics;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class FlowerHUD extends Module {
    public FlowerHUD(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onRender2D(EventRender2D render) {
        String NAME = "Flower \247fv4";
        CFontRenderer smartUI25 = Fonts.smartUI25;
        CFontRenderer modulefont = Fonts.smartUI18;
        Color flower = new Color(0, 230, 0, 255);
        ArrayList<Module> sortedList = getSortedModules(modulefont);
        int listOffset = 8, y = 1;
        int width = render.getResolution().getScaledWidth();

        if (!mc.gameSettings.showDebugInfo && !Existent.getModuleManager().isEnabled(HUD.class)) {
            smartUI25.drawStringWithShadow(NAME, 3, 5, flower.getRGB());

            GL11.glEnable(3042);
            for (int i = 0, sortedListSize = sortedList.size(); i < sortedListSize; i++) {
                Module module = sortedList.get(i);

                module.setDisplayName(module.getName());
                for (Setting setting : module.getSettings().values()) {
                    if (module.getSuffix() != null) {
                        module.setDisplayName(module.getName() + " \247f" + module.getSuffix());
                    } else if (setting.getValue() instanceof Options && !(module instanceof HUD || module instanceof StreamerMode || module instanceof Cosmetics || module instanceof Scaffold || module instanceof TabGui)) {
                        String settingValue = ((Options) setting.getValue()).getSelected();
                        module.setDisplayName(module.getName() + " \247f" + settingValue);
                    }
                }
                String moduleLabel = module.getDisplayName();
                float length = modulefont.getStringWidth(moduleLabel);
                float featureX = width - length - 2.5F;
                boolean enable = module.isEnabled();
                if (enable) {
                    modulefont.drawStringWithShadow(moduleLabel, featureX, y + 3, flower.getRGB());
                    y += modulefont.getHeight() + 4;
                }
            }
        }
    }

    private ArrayList<Module> getSortedModules(CFontRenderer fr) {
        ArrayList<Module> sortedList = new ArrayList<>(Existent.getModuleManager().getModules());
        sortedList.removeIf(Module::isVisible);
        sortedList.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(e.getDisplayName())));
        return sortedList;
    }
}
