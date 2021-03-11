package zyx.existent.gui.click.autumn.panel;

import zyx.existent.module.Module;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.util.Comparator;

public class PanelSort implements Comparator<Module> {
    private final CFontRenderer font = Fonts.default16;

    @Override
    public int compare(Module o1, Module o2) {
        return Integer.compare(font.getStringWidth(o2.getName()), font.getStringWidth(o1.getName()));
    }
}
