package zyx.existent.module;

import zyx.existent.Existent;
import zyx.existent.event.EventManager;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.data.SettingsMap;
import zyx.existent.utils.MCUtil;
import zyx.existent.utils.StringConversions;
import zyx.existent.utils.file.FileUtils;
import zyx.existent.utils.render.animate.Translate;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Module implements MCUtil {
    private static final File MODULE_DIR = FileUtils.getConfigFile("modules");
    private static final File SETTINGS_DIR = FileUtils.getConfigFile("settings");

    private final Translate translate = new Translate(0.0F, 0.0F);
    protected final SettingsMap settings = new SettingsMap();
    private String name, displayname, suffix;
    private final String description;
    private int keybind;
    private boolean enabled;
    public boolean isVisible;
    private final Category category;
    public String INVISIBLE = "INVISIBLE";

    public Module(String name, String desc, int keybind, Category category) {
        this.name = name;
        this.description = desc;
        this.keybind = keybind;
        this.category = category;

        loadStatus();
        loadSettings();
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isCategory(Category c) {
        return c == this.category;
    }

    public int getCategoryColor() {
        switch (category) {
            case Combat:
                return new Color(255, 120, 50, 255).getRGB();
            case Movement:
                return new Color(50, 200, 255, 255).getRGB();
            case Player:
                return new Color(50, 255, 50, 255).getRGB();
            case Visual:
                return new Color(255, 255, 60, 255).getRGB();
            case Misc:
                return new Color(255, 60, 255, 255).getRGB();
            case Other:
                return new Color(255, 50, 50, 255).getRGB();
        }
        return -1;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String displayName) {
        this.displayname = displayName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public SettingsMap getSettings() {
        return settings;
    }

    public static Setting getSetting(SettingsMap map, String settingText) {
        settingText = settingText.toUpperCase();
        if (map.containsKey(settingText)) {
            return map.get(settingText);
        } else {
            for (String key : map.keySet()) {
                if (key.startsWith(settingText)) {
                    return map.get(key);
                }
            }
        }
        return null;
    }

    public Setting getSetting(String key) {
        return settings.get(key);
    }

    public Translate getTranslate() {
        return translate;
    }

    public static void saveStatus() {
        List<String> fileContent = new ArrayList<>();
        for (Module module : Existent.getModuleManager().getModules()) {
            fileContent.add(String.format("%s:%s:%s:%s", module.getName().replaceAll(" ", ""), module.isEnabled(), module.getKeybind(), module.isVisible));
        }
        FileUtils.write(MODULE_DIR, fileContent, true);
        System.out.println("write Status");
    }

    public static void saveSettings() {
        List<String> fileContent = new ArrayList<>();
        for (Module module : Existent.getModuleManager().getModules()) {
            for (Setting setting : module.getSettings().values()) {
                if (!(setting.getValue() instanceof Options)) {
                    String displayName = module.getName().replaceAll(" ", "");
                    String settingName = setting.getName();
                    String settingValue = setting.getValue().toString();
                    fileContent.add(String.format("%s:%s:%s", displayName, settingName, settingValue));
                } else {
                    String displayName = module.getName().replaceAll(" ", "");
                    String settingName = setting.getName();
                    String settingValue = ((Options) setting.getValue()).getSelected();
                    fileContent.add(String.format("%s:%s:%s", displayName, settingName, settingValue));
                }
            }
        }
        FileUtils.write(SETTINGS_DIR, fileContent, true);
        System.out.println("write Settings");
    }

    public static void loadStatus() {
        try {
            List<String> fileContent = FileUtils.read(MODULE_DIR);
            for (String line : fileContent) {
                String[] split = line.split(":");
                String displayName = split[0];
                for (Module module : Existent.getModuleManager().getModules()) {
                    if (module.getName().replaceAll(" ", "").equalsIgnoreCase(displayName)) {
                        String strEnabled = split[1];
                        boolean enabled = Boolean.parseBoolean(strEnabled);
                        String key = split[2];
                        module.setKeybind(Integer.parseInt(key));
                        if (split.length == 4) {
                            module.isVisible = Boolean.parseBoolean(split[3]);
                        }
                        if (enabled && !module.isEnabled()) {
                            module.enabled = true;
                            EventManager.register(module);
                            module.onEnable();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSettings() {
        try {
            List<String> fileContent = FileUtils.read(SETTINGS_DIR);
            for (String line : fileContent) {
                String[] split = line.split(":");
                for (Module module : Existent.getModuleManager().getModules()) {
                    if (module.getName().replaceAll(" ", "").equalsIgnoreCase(split[0])) {
                        Setting setting = getSetting(module.getSettings(), split[1]);
                        String settingValue = split[2];
                        if (setting != null) {
                            if (setting.getValue() instanceof Number) {
                                Object newValue = (StringConversions.castNumber(settingValue, setting.getValue()));
                                if (newValue != null) {
                                    setting.setValue(newValue);
                                }
                            } // If the setting is supposed to be a string
                            else if (setting.getValue().getClass().equals(String.class)) {
                                String parsed = settingValue.replaceAll("_", " ");
                                setting.setValue(parsed);
                            } // If the setting is supposed to be a boolean
                            else if (setting.getValue().getClass().equals(Boolean.class)) {
                                setting.setValue(Boolean.parseBoolean(settingValue));
                            } else if (setting.getValue().getClass().equals(Options.class)) {
                                Options dank = ((Options) setting.getValue());
                                for (String str : dank.getOptions()) {
                                    if (str.equalsIgnoreCase(settingValue)) {
                                        dank.setSelected(settingValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
