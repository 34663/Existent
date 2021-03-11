package zyx.existent.module.data;

import net.minecraft.client.gui.FontRenderer;
import zyx.existent.module.Module;
import zyx.existent.module.modules.hud.HUD;
import zyx.existent.utils.MCUtil;

import java.util.Comparator;

public class ModuleComparator implements Comparator<Module>, MCUtil {
    private FontRenderer mcfont = mc.fontRendererObj;

    @Override
    public int compare(Module o1, Module o2) {
        return Integer.compare(mcfont.getStringWidth(o2.getDisplayName()), mcfont.getStringWidth(o1.getDisplayName()));
    }
}