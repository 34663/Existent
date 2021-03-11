package zyx.existent.utils.changelog;

import java.util.ArrayList;

public class ChangeLogManager {
    public static ArrayList<ChangeLog> changeLogs = new ArrayList<>();

    public void setChangeLogs() {
        changeLogs.add(new ChangeLog("Beta 1", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("MainMenu", ChangelogType.NEW));
        changeLogs.add(new ChangeLog("Outline", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Crosshair", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Chams", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Inventory Manager", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("2020/06/20", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("Hurtcam", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("ChestESP", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Regen", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Item Physic", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("HUD", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("2020/06/21", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("UI", ChangelogType.NEW));
        changeLogs.add(new ChangeLog("Help Command", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Full bright", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Plugin Checker", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Item Physic", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("FontRenderer", ChangelogType.FIXED));
        changeLogs.add(new ChangeLog("Speed (Hypixel)", ChangelogType.FIXED));
        changeLogs.add(new ChangeLog("HypixelFly", ChangelogType.PROTOTYPE));
        changeLogs.add(new ChangeLog("2020/06/23", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("No Title", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Scoreboard", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("2020/06/25", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("AutoTool", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("AntiVanish", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("AutoRespawn", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Movement", ChangelogType.FIXED));
        changeLogs.add(new ChangeLog("2020/06/26", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("BoxESP", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("TwilightESP (ChestESP)", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("2020/06/29", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("Potion Saver", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("HitBox", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("BoxESP", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Criticals", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("2020/07/02", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("FileManager", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("2020/07/08", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("Freecam", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("EffectRemover", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("ExtendedReach", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("NoFall", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("2020/07/21", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("BigUpdate xd", ChangelogType.ADD));
    }

    public ArrayList<ChangeLog> getChangeLogs() {
        return changeLogs;
    }
}
