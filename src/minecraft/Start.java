import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import net.minecraft.client.main.Main;

public class Start {

    public static void main(String[] args) {
        Main.main(concat(new String[]{"--version", "Existent", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void run() {
        File workingDirectory;
        String userHome = System.getProperty("user.home", ".");
        switch (Start.getPlatform()) {
            case LINUX: {
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            }
            case WINDOWS: {
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;
                workingDirectory = new File(folder, ".minecraft/");
                break;
            }
            case MACOS: {
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            }
            default: {
                workingDirectory = new File(userHome, "minecraft/");
            }
        }
        try {
            Main.main(new String[]{"--version", "Complex", "--accessToken", "0", "--assetIndex", "1.12", "--userProperties", "{}", "--gameDir", new File(workingDirectory, ".").getAbsolutePath(), "--assetsDir", new File(workingDirectory, "assets/").getAbsolutePath()});
        } catch (Exception applicationData) {
            // empty catch block
        }
    }

    public static OS getPlatform() {
        String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? OS.WINDOWS : (s.contains("mac") ? OS.MACOS : (s.contains("solaris") ? OS.SOLARIS : (s.contains("sunos") ? OS.SOLARIS : (s.contains("linux") ? OS.LINUX : (s.contains("unix") ? OS.LINUX : OS.UNKNOWN)))));
    }

    public static enum OS {
        LINUX,
        SOLARIS,
        WINDOWS,
        MACOS,
        UNKNOWN;
    }
}