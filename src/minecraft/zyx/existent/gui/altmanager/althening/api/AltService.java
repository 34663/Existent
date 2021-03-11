package zyx.existent.gui.altmanager.althening.api;

import zyx.existent.gui.altmanager.althening.api.utilities.ReflectionUtility;

import java.util.*;
import java.net.*;

public class AltService
{
    private final ReflectionUtility userAuthentication;
    private final ReflectionUtility minecraftSession;
    private EnumAltService currentService;
    
    public AltService() {
        this.userAuthentication = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
        this.minecraftSession = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
    }
    
    public void switchService(final EnumAltService v1) throws NoSuchFieldException, IllegalAccessException {
        if (this.currentService == v1) {
            return;
        }
        this.reflectionFields(v1.hostname);
        this.currentService = v1;
    }
    
    private void reflectionFields(final String v666) throws NoSuchFieldException, IllegalAccessException {
        final HashMap<String, URL> v2 = new HashMap<String, URL>();
        final String v3 = v666.contains("thealtening") ? "http" : "https";
        v2.put("ROUTE_AUTHENTICATE", this.constantURL(String.valueOf(v3) + "://authserver." + v666 + ".com/authenticate"));
        v2.put("ROUTE_INVALIDATE", this.constantURL(String.valueOf(v3) + "://authserver" + v666 + "com/invalidate"));
        v2.put("ROUTE_REFRESH", this.constantURL(String.valueOf(v3) + "://authserver." + v666 + ".com/refresh"));
        v2.put("ROUTE_VALIDATE", this.constantURL(String.valueOf(v3) + "://authserver." + v666 + ".com/validate"));
        v2.put("ROUTE_SIGNOUT", this.constantURL(String.valueOf(v3) + "://authserver." + v666 + ".com/signout"));
        v2.forEach((a2, v1) -> {
            try {
                this.userAuthentication.setStaticField(a2, v1);
            }
            catch (Exception v4) {
                v4.printStackTrace();
            }
            return;
        });
        this.userAuthentication.setStaticField("BASE_URL", String.valueOf(v3) + "://authserver." + v666 + ".com/");
        this.minecraftSession.setStaticField("BASE_URL", String.valueOf(v3) + "://sessionserver." + v666 + ".com/session/minecraft/");
        this.minecraftSession.setStaticField("JOIN_URL", this.constantURL(String.valueOf(v3) + "://sessionserver." + v666 + ".com/session/minecraft/join"));
        this.minecraftSession.setStaticField("CHECK_URL", this.constantURL(String.valueOf(v3) + "://sessionserver." + v666 + ".com/session/minecraft/hasJoined"));
        this.minecraftSession.setStaticField("WHITELISTED_DOMAINS", new String[] { ".minecraft.net", ".mojang.com", ".thealtening.com" });
    }
    
    private URL constantURL(final String v1) {
        try {
            return new URL(v1);
        }
        catch (MalformedURLException v2) {
            throw new Error("Couldn't create constant for " + v1, v2);
        }
    }
    
    public EnumAltService getCurrentService() {
        if (this.currentService == null) {
            this.currentService = EnumAltService.MOJANG;
        }
        return this.currentService;
    }
    
    public enum EnumAltService
    {
        MOJANG("MOJANG", 0, "mojang"), 
        THEALTENING("THEALTENING", 1, "thealtening");
        
        String hostname;
        
        private EnumAltService(final String s, final int n, final String a2) {
            this.hostname = a2;
        }
    }
}
