package zyx.existent.module;

import org.lwjgl.input.Keyboard;
import zyx.existent.module.modules.combat.*;
import zyx.existent.module.modules.hud.*;
import zyx.existent.module.modules.misc.*;
import zyx.existent.module.modules.movement.*;
import zyx.existent.module.modules.other.Debug;
import zyx.existent.module.modules.other.LagCheck;
import zyx.existent.module.modules.player.*;
import zyx.existent.module.modules.visual.*;

import java.util.ArrayList;

public class ModuleManager {
    private final ArrayList<Module> modules = new ArrayList<Module>();
    private boolean isSetup;

    public void setup() {
        add(new Criticals("Criticals", "", 0, Category.Combat));
        add(new Sprint("Sprint", "AutoSprinting", 0, Category.Movement));
        add(new NameTags("NameTags", "", 0, Category.Visual));
        add(new NoFall("NoFall", "", 0, Category.Player));
        add(new ESP2("ESP", "", 0, Category.Visual));
        add(new WTap("WTap", "", 0, Category.Combat));
        add(new AutoArmor("Auto Armor", "", 0, Category.Player));
        add(new InvMove("Inventory Move", "", 0, Category.Player));
        add(new KeepSprint("Keep Sprint", "", 0, Category.Movement));
        add(new NoSlowDown("No Slow Down", "", 0, Category.Movement));
        add(new KillAura("Kill Aura", "", Keyboard.KEY_R, Category.Combat));
        add(new Step("Step", "", 0, Category.Movement));
        add(new ShowInvis("Show Invis", "", 0, Category.Visual));
        add(new PingSpoof("Ping Spoof", "", 0, Category.Misc));
        add(new Speed("Speed", "", Keyboard.KEY_M, Category.Movement));
        add(new Glide("Glide", "", Keyboard.KEY_V, Category.Movement));
        add(new AntiBot("Anti Bot", "", 0, Category.Combat));
        add(new TextureStatus("Texture Status", "", 0, Category.Misc));
        add(new AntiVoid("Anti Void", "", 0, Category.Movement));
        add(new TargetStrafe("Target Strafe", "", 0, Category.Movement));
        add(new Jesus("Jesus", "", 0, Category.Movement));
        add(new AutoSword("Auto Sword", "", 0, Category.Combat));
        add(new Velocity("Velocity", "", 0, Category.Combat));
        add(new ChatFilter("Chat Filter", "", 0, Category.Misc));
        add(new Flight("Flight", "You can Fly!!", Keyboard.KEY_F, Category.Movement));
        add(new ChestStealer("Chest Stealer", "", Keyboard.KEY_B, Category.Player));
        add(new Skeleton("Skeleton", "", 0, Category.Visual));
        add(new AntiDesync("Anti Desync", "No Rotation", 0, Category.Misc));
        add(new Ambience("Ambience", "", 0, Category.Visual));
        add(new Spammer("Spammer", "", 0, Category.Misc));
        add(new Scaffold("Scaffold", "", 0, Category.Movement));
        add(new TargetHUD("TargetHUD", "", 0, Category.Visual));
        add(new LongJump("Long Jump", "", 0, Category.Movement));
        add(new BetterSounds("Better Sounds", "", 0, Category.Misc));
        add(new Animations("Animations", "", 0, Category.Visual));
        add(new InventoryManager("Inventory Manager", "", 0, Category.Player));
        add(new Chams("Chams", "", 0, Category.Visual));
        add(new Croshair("Croshair", "", 0, Category.Visual));
        add(new Outline("Outline", "", 0, Category.Visual));
        add(new Disabler("Disabler", "", 0, Category.Misc));
        add(new SpeedMine("Speed Mine", "", 0, Category.Player));
        add(new ServerCrasher("Server Crasher", "", 0, Category.Misc));
        add(new Annoy("Annoy", "", 0, Category.Misc));
        add(new Crack("Crack", "", 0, Category.Visual));
        add(new AntiTabComplete("Anti TabComplete", "", 0, Category.Misc));
        add(new SkinFlash("Skin Flash", "", 0, Category.Player));
        add(new StreamerMode("StreamerMode", "", 0, Category.Misc));
        add(new Hurtcam("Hurtcam", "", 0, Category.Visual));
        add(new ChestESP("ChestESP", "", 0, Category.Visual));
        add(new Regen("Regen", "", 0, Category.Combat));
        add(new ItemPhysic("Item Physic", "", 0, Category.Visual));
        add(new Plugins("Plugin Checker", "", 0, Category.Misc));
        add(new FullBright("FullBright", "", 0, Category.Visual));
        add(new NoTitle("No Title", "Delete TitleMassage", 0, Category.Visual));
        add(new ScoreBoard("Scoreboard", "", 0, Category.Visual));
        add(new FastUse("FastUse", "", 0, Category.Player));
        add(new AntiVanish("Anti Vanish", "", 0, Category.Misc));
        add(new AutoTool("Auto Tool", "", 0, Category.Player));
        add(new AutoRespawn("Auto Respawn", "", 0, Category.Player));
        add(new Sneak("Sneak", "", 0, Category.Movement));
        add(new PotionSaver("Potion Saver", "", 0, Category.Misc));
        add(new HitBox("HitBox", "", 0, Category.Combat));
        add(new Blink("Blink", "", 0, Category.Misc));
        add(new ExtendedReach("Extended Reach", "", 0, Category.Combat));
        add(new Freecam("Freecam", "", 0, Category.Misc));
        add(new AntiDebuff("Anti Debuff", "", 0, Category.Misc));
        add(new LagCheck("LagCheck", "", 0, Category.Other));
        add(new Rader("Rader", "", 0, Category.Visual));
        add(new AutoL("AutoL", "", 0, Category.Misc));
        add(new SuperKB("SuperKB", "", 0, Category.Combat));
        add(new Cosmetics("Cosmetics", "", 0, Category.Visual));
        add(new NameTags2("NameTags2", "", 0, Category.Visual));
        add(new AdminChecker("Admin Checker", "", 0, Category.Misc));
        add(new Civbreak2("Civbreak", "", 0, Category.Misc));
        add(new WaterSpeed("WaterSpeed", "", 0, Category.Movement));
        add(new Projectiles("Projectiles", "", 0, Category.Visual));
        add(new AutoClicker("Auto Clicker", "", 0, Category.Combat));
        add(new AntiImmobilizer("Anti Immobilizer", "", 0, Category.Misc));
        add(new TerrainSpeed("TerrainSpeed", "", 0, Category.Movement));
        add(new KillCounter("Kill Counter", "", 0, Category.Visual));
        add(new ViewClip("ViewClip", "", 0, Category.Visual));
        add(new PointerESP("PointerESP", "", 0, Category.Visual));
        add(new Dicks("Dicks", "", 0, Category.Visual));
        add(new AimAssist("AimAssist", "", 0, Category.Combat));
        add(new Tracer("TeamTracer", "", 0, Category.Visual));
        add(new Phase("Phase", "", 0, Category.Movement));
        add(new HackerChecker("Hacker Checker", "", 0, Category.Misc));
        add(new DamageHit("HitIndicator", "", 0, Category.Visual));
        add(new HighJump("HighJump", "", 0, Category.Movement));

        add(new Debug("Debug", "", 0, Category.Other));
        add(new HUD("HUD", "Display the HUD on the screen.", 0, Category.Other));
        add(new FlowerHUD("FlowerHUD", "Test FlowerHUD.", 0, Category.Other));
        add(new ClickGui("ClickGui", "", Keyboard.KEY_RSHIFT, Category.Other));
        add(new TabGui("TabGui", "", 0, Category.Other));

        if (!isEnabled(HUD.class)) {
            getClazz(HUD.class).toggle();
        }
        if (!isEnabled(TabGui.class)) {
            getClazz(TabGui.class).toggle();
        }

        isSetup = true;
    }

    public void add(Module mod) {
        modules.add(mod);
    }

    public boolean isEnabled(Class<?> clazz) {
        Module module = getClazz(clazz);
        return (module != null && module.isEnabled());
    }

    public Module getClazz(Class<?> clazz) {
        try {
            for (Module feature : getModules()) {
                if (feature.getClass() == clazz)
                    return feature;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public Module getString(String clazz) {
        for (Module module : getModules()) {
            if (module.getName().toLowerCase().replaceAll(" ", "").equals(clazz.toLowerCase())) {
                return module;
            }
        }
        return null;
    }

    public ArrayList<Module> getModuleByCategory(Category cat) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module module : getModules()) {
            if (module.getCategory() == cat) {
                mods.add(module);
            }
        }
        return mods.isEmpty() ? null : mods;
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public boolean isSetup() {
        return isSetup;
    }
}
