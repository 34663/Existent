package zyx.existent;

import net.minecraft.client.renderer.entity.RenderPlayer;
import zyx.existent.command.CommandManager;
import zyx.existent.friend.FriendManager;
import zyx.existent.module.Module;
import zyx.existent.module.ModuleManager;
import zyx.existent.utils.MCUtil;
import zyx.existent.utils.changelog.ChangeLogManager;
import zyx.existent.utils.file.FileManager;
import zyx.existent.utils.render.cosmetic.CosmeticRender;
import zyx.existent.utils.render.cosmetic.impl.DragonWing;
import java.io.File;

public enum Existent implements MCUtil {
    instance;

    public static String CLIENT_NAME = "Existent";
    public static String CLIENT_BUILD = "080920";
    private static String API;
    private File dataDirectory;

    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ChangeLogManager changeLogManager;
    private FriendManager friendManager;
    private FileManager fileManager;

    public void setup() {
        (moduleManager = new ModuleManager()).setup();
        (commandManager = new CommandManager()).setup();
        (fileManager = new FileManager()).loadFiles();
        (changeLogManager = new ChangeLogManager()).setChangeLogs();
        friendManager = new FriendManager();
        this.dataDirectory = new File("existent");
        new DragonWing();
        for (RenderPlayer render : mc.getRenderManager().getSkinMap().values()) {
            render.addLayer(new CosmeticRender(render));
        }
        System.out.println("ready");
    }

    public void shutdown() {
        Module.saveStatus();
        Module.saveSettings();
        (fileManager = new FileManager()).saveFiles();
    }

    public static ModuleManager getModuleManager() {
        return instance.moduleManager;
    }
    public static CommandManager getCommandManager() {
        return instance.commandManager;
    }
    public static ChangeLogManager getChangeLogManager() {
        return instance.changeLogManager;
    }
    public static FileManager getFileManager() {
        return instance.fileManager;
    }
    public static FriendManager getFriendManager() {
        return instance.friendManager;
    }
    public static File getDataDirectory() {
        return instance.dataDirectory;
    }

    public static String getClientPrefix(String name) {
        return "\2477[\247cEx\2478-\247cUser\2477] \247f" + name;
    }

    public static String getAPI() {
        return API;
    }
    public static void setAPI(String API) {
        Existent.API = API;
    }
}
