package zyx.existent.module.data;

import zyx.existent.module.Module;
import zyx.existent.utils.MCUtil;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.util.Comparator;

public class ModuleComparator2 implements Comparator<Module>, MCUtil {
    private CFontRenderer mcfont = Fonts.default18;

    @Override
    public int compare(Module o1, Module o2) {
        return Integer.compare(mcfont.getStringWidth(o2.getName()), mcfont.getStringWidth(o1.getName()));
    }
}