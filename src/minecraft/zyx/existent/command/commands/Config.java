package zyx.existent.command.commands;

import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.StringConversions;
import zyx.existent.utils.file.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config extends Command {
    ArrayList<String> basicConfigs = new ArrayList<String>() {{
    }};

    public Config(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args == null || args.length > 2) {
            printUsage();
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("load")) {
                ChatUtils.printChatprefix("Config : -config load <ConfigName>");
            } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("new") || args[0].equalsIgnoreCase("save")) {
                ChatUtils.printChatprefix("Config : -config create <ConfigName>");
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("rem")) {
                ChatUtils.printChatprefix("Config : -config remove <ConfigName>");
            } else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("removeall")) {
                clearConfigs();
            } else if (args[0].equalsIgnoreCase("list")) {
                ArrayList<String> list = getConfigList();
                int lenght = list.size();
                StringBuilder send = new StringBuilder("Config list : ");
                for (int i = 0; i < lenght; i++) {
                    if (basicConfigs.contains(list.get(i))) {
                        if (i == lenght - 1)
                            send.append("\247a").append(list.get(i)).append("\2477.");
                        else
                            send.append("\247a").append(list.get(i)).append("\2477, ");
                    } else {
                        if (i == lenght - 1)
                            send.append("\2477").append(list.get(i)).append(".");
                        else
                            send.append("\2477").append(list.get(i)).append(", ");
                    }
                }
                ChatUtils.printChatprefix(send.toString());
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("load")) {
                String config = args[1];
                loadConfig(config);
            } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("new") || args[0].equalsIgnoreCase("save")) {
                String config = args[1];
                saveConfig(config);
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("rem")) {
                String config = args[1];
                removeConfig(config);
            }
        }
    }

    @Override
    public String getUsage() {
        return null;
    }

    public void loadConfig(String config) {
        String filePath = "configs/" + config + ".config";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        List<String> readContent = new ArrayList<String>();
        File configF = FileUtils.getConfig(config);
        if (!basicConfigs.contains(config.toLowerCase())) {
            if (configF == null) {
                ChatUtils.printChat("\247b" + config + " \247rconfig does not exist !");
                return;
            }
            readContent = FileUtils.read(configF);
        } else {
            if (inputStream != null) {
                try (BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line = null;
                    while ((line = bufferedInputStream.readLine()) != null) {
                        readContent.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ChatUtils.printChat("Error while loading \247b" + config + " \247rconfig.");
                }
            } else {
                ChatUtils.printChat("\247b" + config + " \247rconfig does not exist !");
            }
        }
        if (!readContent.isEmpty()) {
            for (String line : readContent) {
                String[] split = line.split(":");
                for (Module module : Existent.getModuleManager().getModules()) {
                    if (split[0].equalsIgnoreCase("DISABLE") && split[1].equalsIgnoreCase(module.getName().replaceAll(" ", "")) && module.isEnabled()) {
                        module.toggle();
                    } else if (split[0].equalsIgnoreCase("ENABLE") && split[1].equalsIgnoreCase(module.getName().replaceAll(" ", "")) && !module.isEnabled()) {
                        module.toggle();
                    } else if (module.getName().replaceAll(" ", "").equalsIgnoreCase(split[0])) {
                        Setting setting = Module.getSetting(module.getSettings(), split[1]);
                        String settingValue = split[2];
                        if (setting != null) {
                            if (setting.getValue() instanceof Number) {
                                Object newValue = (StringConversions.castNumber(settingValue, setting.getValue()));
                                if (newValue != null) {
                                    setting.setValue(newValue);
                                }
                            } else if (setting.getValue().getClass().equals(String.class)) {
                                String parsed = settingValue.replaceAll("_", " ");
                                setting.setValue(parsed);
                            } else if (setting.getValue().getClass().equals(Boolean.class)) {
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
            ChatUtils.printChat("\247b" + config + " \247rconfig has been loaded!");
        } else {
            ChatUtils.printChat("\247b" + config + " \247rconfig is empty !");
        }
    }
    public void saveConfig(String config) {
        if (basicConfigs.contains(config.toLowerCase())) {
            ChatUtils.printChat("You can not change this config !");
            return;
        }
        if (!config.matches("^[a-zA-Z0-9_]+$")) {
            ChatUtils.printChat("\247b" + config + "\247r is invalid !");
            return;
        }
        File dir = Existent.getDataDirectory();
        File[] directoryListing = dir.listFiles();
        boolean shouldCreate = true;
        for (File child : directoryListing) {
            String fileName = child.getName();
            if (fileName.equalsIgnoreCase("Configs")) {
                shouldCreate = false;
            }
        }
        if (shouldCreate) {
            File file = new File(Existent.getDataDirectory().toString() + "/Configs");
            try {
                file.mkdir();
                directoryListing = dir.listFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File configDir = new File(Existent.getDataDirectory().toString() + "/Configs");
        directoryListing = configDir.listFiles();
        File configFile = FileUtils.getConfigFile("Configs/" + config);
        for (File child : directoryListing) {
            String fileName = child.getName();
            if (fileName.endsWith(".txt")) {
                try {
                    String str[] = fileName.split(".txt");
                    fileName = str[0];
                    if (fileName.equalsIgnoreCase(config)) {
                        ChatUtils.printChat("\247b" + config + "\247r config already exists !");
                        return;
                    }
                } catch (Exception e) {
                    ChatUtils.printChat("§c" + e);
                }
            }
        }
        List<String> fileContent = new ArrayList<>();
        for (Module module : Existent.getModuleManager().getModules()) {
            if (module.getCategory() == Category.Visual)
                continue;
            String displayName = module.getName().replaceAll(" ", "");
            if (!displayName.equals(""))
                if (module.isEnabled()) {
                    fileContent.add(String.format("ENABLE:%s", displayName));
                } else {
                    fileContent.add(String.format("DISABLE:%s", displayName));
                }
            for (Setting setting : module.getSettings().values()) {
                if (!(setting.getValue() instanceof Options)) {
                    String settingName = setting.getName();
                    String settingValue = setting.getValue().toString();
                    fileContent.add(String.format("%s:%s:%s", displayName, settingName, settingValue));
                } else {
                    String settingName = setting.getName();
                    String settingValue = ((Options) setting.getValue()).getSelected();
                    fileContent.add(String.format("%s:%s:%s", displayName, settingName, settingValue));
                }
            }
        }
        FileUtils.write(configFile, fileContent, true);
        ChatUtils.printChat("\247b" + config + "\247r config has been saved !");
    }
    public ArrayList<String> getConfigList() {
        ArrayList<String> list = new ArrayList(basicConfigs);
        File dir = new File(Existent.getDataDirectory().getName() + "\\Configs");
        File[] directoryListing = dir.listFiles();
        if (!dir.exists()) {
            dir.mkdir();
        } else
            for (File child : directoryListing) {
                String fileName = child.getName().split("\\.")[0];
                list.add(fileName);
            }
        return list;
    }
    void clearConfigs(){
        File dir = new File(Existent.getDataDirectory().getName() + "\\Configs");
        File[] directoryListing = dir.listFiles();
        if(!dir.exists() || directoryListing.length == 0){
            ChatUtils.printChat("You have no config saved !");
            return;
        }
        for(File child : directoryListing){
            child.delete();
        }
        String str = directoryListing.length > 1? "\247b" + directoryListing.length + "\247r configs have been removed" : "\247b" + directoryListing.length + "\247r config has been removed" ;
        ChatUtils.printChat(str);
    }
    void removeConfig(String config) {
        File dir = new File(Existent.getDataDirectory().getName() + "\\Configs");
        File[] directoryListing = dir.listFiles();
        if (basicConfigs.contains(config.toLowerCase())) {
            ChatUtils.printChat("You can not remove this config !");
            return;
        }

        if (!dir.exists() || directoryListing.length == 0) {
            ChatUtils.printChat("\247b" + config + " \247rconfig does not exist !");
            return;
        }
        for (File child : directoryListing) {
            String fileName = child.getName();
            String[] split = fileName.split("\\.");

            if (split[0].equalsIgnoreCase(config)) {
                child.delete();
                ChatUtils.printChat("\247b" + config + " \247rconfig has been removed");
                return;
            }
        }
        ChatUtils.printChat("\247b" + config + " \247rconfig does not exist !");
    }
}
