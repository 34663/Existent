package zyx.existent.module.modules.visual;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class FullBright extends Module {
    private final String MODE = "MODE";
    private float oldBrightness;

    public FullBright(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Gamma", new String[]{"Gamma", "Potion"}), "Fullbright method"));
    }

    @Override
    public void onEnable() {
        this.oldBrightness = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.oldBrightness;
        this.mc.thePlayer.removePotionEffect(Potion.getPotionById(16));
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "Gamma":
                mc.gameSettings.gammaSetting = 10F;
                break;
            case "Potion":
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 5200, 1));
                break;
        }
    }
}
