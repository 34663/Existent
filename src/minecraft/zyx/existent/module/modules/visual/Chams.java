package zyx.existent.module.modules.visual;

import net.minecraft.client.renderer.GlStateManager;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventBodyRender;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class Chams extends Module {
    public static String MODE = "MODE";
    public static String ENTITY = "ENTITY";
    public static String HAND = "HAND";

    public Chams(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Fill", new String[] {"Fill", "Wall", "Wall2"}), "ESP method."));
        settings.put(ENTITY, new Setting(ENTITY, true, ""));
        settings.put(HAND, new Setting(HAND, false, ""));
    }

    @EventTarget
    public void onBodyRenderer(EventBodyRender event) {
        if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Wall2")) {
            if (event.ispre()) {
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(1.0f, -1100000.0f);
            } else if (event.ispost()) {
                GlStateManager.doPolygonOffset(1.0f, 1100000.0f);
                GlStateManager.disablePolygonOffset();
            }
        }
    }
}
