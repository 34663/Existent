package zyx.existent.gui.altmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.io.IOException;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.althening.api.AltService;
import zyx.existent.utils.file.impl.Alts;

public final class AltLoginThread extends Thread {
   private Alt alt;
   private String status;
   private Minecraft mc = Minecraft.getMinecraft();

   public AltLoginThread(Alt alt) {
      super("Alt Login Thread");
      this.alt = alt;
      this.status = "§7Waiting...";
   }

   private Session createSession(String username, String password) {
      try {
         GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);

         YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
         auth.setUsername(username);
         auth.setPassword(password);
         long var5 = System.currentTimeMillis();

         try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
         } catch (AuthenticationException var8) {
            var8.printStackTrace();
            return null;
         }
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public String getStatus() {
      return this.status;
   }

   public void run() {
      if (this.alt.getPassword().equals("")) {
         this.mc.session = new Session(this.alt.getUsername(), "", "", "mojang");
         this.status = "§aLogged in. (" + this.alt.getUsername() + " - offline name)";
      } else {
         this.status = "§bLogging in...";
         Session auth = this.createSession(this.alt.getUsername(), this.alt.getPassword());
         if (auth == null) {
            this.status = "§cLogin failed!";
            if (this.alt.getStatus().equals(Alt.Status.Unchecked)) {
               this.alt.setStatus(Alt.Status.NotWorking);
            }
         } else {
            AltManager.lastAlt = new Alt(this.alt.getUsername(), this.alt.getPassword());
            this.status = "§aLogged in. (" + auth.getUsername() + ")";
            this.alt.setMask(auth.getUsername());
            this.mc.session = auth;
            if (this.alt.getStatus().equals(Alt.Status.Unchecked)) {
               this.alt.setStatus(Alt.Status.Working);
            }

            try {
               Existent.getFileManager().getFile(Alts.class).saveFile();
            } catch (IOException var3) {
               var3.printStackTrace();
            }
         }
      }
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
