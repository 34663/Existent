package zyx.existent.gui.altmanager;

public final class Alt {
   private String mask;
   private final String username;
   private String password;
   private Status status;

   public Alt(String username, String password) {
      this(username, password, Status.Unchecked);
   }

   public Alt(String username, String password, Status status) {
      this(username, password, "", status);
   }

   public Alt(String username, String password, String mask, Status status) {
      this.mask = "";
      this.username = username;
      this.password = password;
      this.mask = mask;
      this.status = status;
   }

   public Status getStatus() {
      return this.status;
   }

   public String getMask() {
      return this.mask;
   }

   public String getPassword() {
      return this.password;
   }

   public String getUsername() {
      return this.username;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public void setMask(String mask) {
      this.mask = mask;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public static enum Status {
      Working("§aWorking"),
      Banned("§cBanned"),
      Unchecked("§eUnchecked"),
      NotWorking("§4Not Working");

      private final String formatted;
      private Status(String string) {
         this.formatted = string;
      }
      public String toFormatted() {
         return this.formatted;
      }
   }
}
