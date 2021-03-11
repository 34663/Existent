package zyx.existent.gui.altmanager;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.althening.GuiAlthening;
import zyx.existent.gui.altmanager.althening.api.AltService;
import zyx.existent.gui.screen.GuiExMainMenu;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.file.impl.Alts;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import zyx.existent.utils.timer.Timer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GuiAltManager extends GuiScreen {
    private final Timer animtimer = new Timer();
    public static final AltService altService = new AltService();
    private GuiExButton login;
    private GuiExButton remove;
    private GuiExButton rename;
    private GuiExButton althening;
    private GuiExButton multi;
    private AltLoginThread loginThread;
    private double offset;
    public Alt selectedAlt = null;
    public String status;
    private GuiTextField seatchField;

    public GuiAltManager() {
        this.status = TextFormatting.GRAY + "Idle...";
    }

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                break;
            case 1:
                (this.loginThread = new AltLoginThread(this.selectedAlt)).start();
                break;
            case 2:
                if (this.loginThread != null) {
                    this.loginThread = null;
                }

                AltManager.registry.remove(this.selectedAlt);
                this.status = "§aRemoved.";

                try {
                    Existent.getFileManager().getFile(Alts.class).saveFile();
                } catch (Exception ignored) {
                    ;
                }

                this.selectedAlt = null;
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiAddAlt(this));
                break;
            case 4:
                this.mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            case 5:
                ArrayList registry = AltManager.registry;
                Random random = new Random();
                Alt randomAlt = (Alt) registry.get(random.nextInt(AltManager.registry.size()));
                (this.loginThread = new AltLoginThread(randomAlt)).start();
                break;
            case 6:
                this.mc.displayGuiScreen(new GuiRenameAlt(this));
                break;
            case 7:
                this.mc.displayGuiScreen(new GuiExMainMenu());
                break;
            case 8:
                try {
                    AltManager.registry.clear();
                    Existent.getFileManager().getFile(Alts.class).loadFile();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }

                this.status = "§bReloaded!";
                break;
            case 1919:
                this.mc.displayGuiScreen(new GuiAlthening(this));
                break;
            case 8931:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 4545:
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, new ServerData(I18n.format("selectServer.defaultName"), "play.hypixel.net", false)));
                break;
        }
    }

    public void drawScreen(int par1, int par2, float par3) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());

        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                this.offset += 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            } else if (wheel > 0) {
                this.offset -= 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }

        RenderingUtils.drawBorderedRect(this.width / 2F - 5, 31.0D, this.width - 10, this.height - 50, 2.0F, new Color(40, 40, 40, 255).getRGB(), new Color(50, 50, 50, 200).getRGB());
        FontRenderer fontRendererObj = this.fontRendererObj;
        CFontRenderer fonts = Fonts.default18;

        fonts.drawStringWithShadow(TextFormatting.GRAY + "= Account Info =", 10, 10, -1);
        fonts.drawStringWithShadow("Name: " + this.mc.session.getUsername(), 10, 20, 14540253);
        fonts.drawStringWithShadow("Service: " + altService.getCurrentService(), 10, 30, 14540253);
        fonts.drawStringWithShadow("HypixelLevel: " + GuiAlthening.level1, 10, 45, 14540253);
        fonts.drawStringWithShadow("HypixelRank: " + GuiAlthening.rank1, 10, 55, 14540253);
        fonts.drawStringWithShadow("MineplexLevel: " + GuiAlthening.level2, 10, 65, 14540253);
        fonts.drawStringWithShadow("MineplexRank: " + GuiAlthening.rank2, 10, 75, 14540253);

        StringBuilder sb = new StringBuilder("Account Manager - ");
        sb.append(AltManager.registry.size()).append(" alts").append(" | Banned: ").append(((ArrayList) AltManager.registry.stream().filter(o -> ((Alt) o).getStatus().equals(Alt.Status.Banned)).collect(Collectors.toList())).size());
        fonts.drawCenteredString(sb.toString(), this.width / 2F, 10, -1);
        fonts.drawCenteredString(this.loginThread == null ? this.status : this.loginThread.getStatus(), this.width / 2F, 20, -1);
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0F, 33.0F, (float) this.width, (float) (this.height - 50));
        GL11.glEnable(3089);
        int y = 38;
        int number = 0;
        Iterator<Alt> var8 = this.getAlts().iterator();

        while (true) {
            Alt alt;
            do {
                if (!var8.hasNext()) {
                    GL11.glDisable(3089);
                    GL11.glPopMatrix();
                    super.drawScreen(par1, par2, par3);
                    if (this.selectedAlt == null) {
                        this.login.enabled = false;
                        this.remove.enabled = false;
                        this.rename.enabled = false;
                    } else {
                        this.login.enabled = true;
                        this.remove.enabled = true;
                        this.rename.enabled = true;
                    }

                    if (Keyboard.isKeyDown(200)) {
                        this.offset -= 26;
                    } else if (Keyboard.isKeyDown(208)) {
                        this.offset += 26;
                    }

                    if (this.offset < 0) {
                        this.offset = 0;
                    }

                    this.seatchField.drawTextBox();
                    if (this.seatchField.getText().isEmpty() && !this.seatchField.isFocused()) {
                        this.drawString(this.mc.fontRendererObj, "Search Alt", this.width / 2 + 120, this.height - 18, Colors.getColor(180));
                    }
                    return;
                }

                alt = var8.next();
            } while (!this.isAltInArea(y));

            ++number;
            String name;
            if (alt.getMask().equals("")) {
                name = alt.getUsername();
            } else {
                name = alt.getMask();
            }

            if (name.equalsIgnoreCase(this.mc.session.getUsername())) {
                name = "§n" + name;
            }

            String prefix = alt.getStatus().equals(Alt.Status.Banned) ? "§c" : (alt.getStatus().equals(Alt.Status.NotWorking) ? "§m" : "");
            name = prefix + name + "§r §7| " + alt.getStatus().toFormatted();
            String pass;
            if (alt.getPassword().equals("")) {
                pass = "§cCracked";
            } else {
                pass = alt.getPassword().replaceAll(".", "*");
            }

            if (alt != this.selectedAlt) {
                if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                    RenderingUtils.drawBorderedRect(this.width / 2F, (y - this.offset - 4), (this.width - 52), (y - this.offset + 20), 1.0F, -Colors.getColor(145, 50), -2146101995);
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderingUtils.drawBorderedRect(this.width / 2F, (y - this.offset - 4), (this.width - 52), (y - this.offset + 20), 1.0F, Colors.getColor(145, 50), -2145180893);
                }
            } else {
                if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                    RenderingUtils.drawBorderedRect(this.width / 2F, (y - this.offset - 4), (this.width - 77), (y - this.offset + 20), 1.0F, Colors.getColor(145, 50), -2142943931);
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderingUtils.drawBorderedRect(this.width / 2F, (y - this.offset - 4), (this.width - 77), (y - this.offset + 20), 1.0F, Colors.getColor(145, 50), -2142088622);
                } else {
                    RenderingUtils.drawBorderedRect(this.width / 2F, (y - this.offset - 4), (this.width - 77), (y - this.offset + 20), 1.0F, Colors.getColor(145, 50), -2144259791);
                }

                boolean hovering = par1 >= this.width - 76 && par1 <= this.width - 52 && par2 >= y - this.offset - 4 && par2 <= y - this.offset + 20;
                RenderingUtils.drawBorderedRect((this.width - 76), (y - this.offset - 4), (this.width - 52), (y - this.offset + 20), 1.0F, Colors.getColor(80, 255), hovering ? -1 : Colors.getColor(20, 255));
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (this.width - 74 + 10), (float) (y - this.offset), 0.0F);
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                fonts.drawStringWithShadow("Change", 0 - fontRendererObj.getStringWidth("Change") / 2F, 0.0F, Colors.getColor(230, 255));
                fonts.drawStringWithShadow("Account", 0 - fontRendererObj.getStringWidth("Account") / 2F, 12.0F, Colors.getColor(230, 255));
                fonts.drawStringWithShadow("Status", 0 - fontRendererObj.getStringWidth("Status") / 2F, 24.0F, Colors.getColor(230, 255));
                GlStateManager.popMatrix();
            }

            String numberP = "§7" + number + ". §f";
            fonts.drawCenteredString(numberP + name, this.width / 1.5, y - this.offset, -1);
            fonts.drawCenteredString((alt.getStatus().equals(Alt.Status.NotWorking) ? "§m" : "") + pass, this.width / 1.5, y - this.offset + 10, Colors.getColor(110));
            y += 26;
        }
    }

    public void initGui() {
        this.seatchField = new GuiTextField(this.eventButton, this.mc.fontRendererObj, this.width / 2 + 116, this.height - 22, 72, 16);
        this.buttonList.add(this.login = new GuiExButton(1, this.width / 2 - 122, this.height - 48, 100, 20, "Login"));
        this.buttonList.add(this.remove = new GuiExButton(2, this.width / 2 - 40, this.height - 24, 70, 20, "Remove"));
        this.buttonList.add(new GuiExButton(3, this.width / 2 + 4 + 86, this.height - 48, 100, 20, "Add"));
        this.buttonList.add(new GuiExButton(4, this.width / 2 - 16, this.height - 48, 100, 20, "Direct Login"));
        this.buttonList.add(new GuiExButton(5, this.width / 2 - 122, this.height - 24, 78, 20, "Random"));
        this.buttonList.add(this.rename = new GuiExButton(6, this.width / 2 + 38, this.height - 24, 70, 20, "Edit"));
        this.buttonList.add(new GuiExButton(7, this.width / 2 - 190, this.height - 24, 60, 20, "Back"));
        this.buttonList.add(new GuiExButton(8, this.width / 2 - 190, this.height - 48, 60, 20, "Reload"));
        this.buttonList.add(new GuiExButton(1919, this.width / 2 + 195, this.height - 48, 100, 20, "TheAlthening"));
        this.buttonList.add(new GuiExButton(8931, this.width / 2 + 190, 5, 100, 20, "MultiPlayer"));
        this.buttonList.add(new GuiExButton(4545, this.width / 2 + 300, 5, 100, 20, "Connect Hypixel"));
        this.login.enabled = false;
        this.remove.enabled = false;
        this.rename.enabled = false;
    }

    protected void keyTyped(char par1, int par2) {
        this.seatchField.textboxKeyTyped(par1, par2);
        if ((par1 == '\t' || par1 == '\r') && this.seatchField.isFocused()) {
            this.seatchField.setFocused(!this.seatchField.isFocused());
        }

        try {
            super.keyTyped(par1, par2);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private boolean isAltInArea(int y) {
        return y - this.offset <= this.height - 50;
    }

    private boolean isMouseOverAlt(double x, double y, double y1) {
        return x >= this.width / 2 && y >= y1 - 4 && x <= this.width - 77 && y <= y1 + 20 && x >= 0 && y >= 33 && x <= this.width && y <= this.height - 50;
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        this.seatchField.mouseClicked(par1, par2, par3);
        if (this.offset < 0) {
            this.offset = 0;
        }

        double y = 38 - this.offset;

        for (Iterator<Alt> var5 = this.getAlts().iterator(); var5.hasNext(); y += 26) {
            Alt alt = var5.next();
            if (this.isMouseOverAlt(par1, par2, y)) {
                if (alt == this.selectedAlt) {
                    this.actionPerformed(login);
                    return;
                }
                this.selectedAlt = alt;
            }

            boolean hovering = par1 >= this.width - 76 && par1 <= this.width - 52 && par2 >= y - 4 && par2 <= y + 20;
            if (hovering && alt == this.selectedAlt) {
                switch (alt.getStatus()) {
                    case Unchecked:
                        alt.setStatus(Alt.Status.Working);
                        break;
                    case Working:
                        alt.setStatus(Alt.Status.Banned);
                        break;
                    case Banned:
                        alt.setStatus(Alt.Status.NotWorking);
                        break;
                    case NotWorking:
                        alt.setStatus(Alt.Status.Unchecked);
                }

                try {
                    Existent.getFileManager().getFile(Alts.class).saveFile();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }
        }

        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException var9) {
            var9.printStackTrace();
        }
    }

    private List<Alt> getAlts() {
        List<Alt> altList = new ArrayList<>();
        Iterator var2 = AltManager.registry.iterator();

        while (true) {
            Alt alt;
            do {
                if (!var2.hasNext()) {
                    return altList;
                }

                alt = (Alt) var2.next();
            } while (!this.seatchField.getText().isEmpty() && !alt.getMask().toLowerCase().contains(this.seatchField.getText().toLowerCase()) && !alt.getUsername().toLowerCase().contains(this.seatchField.getText().toLowerCase()));

            altList.add(alt);
        }
    }

    private void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(this.mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * (float) factor), (int) (((float) scale.getScaledHeight() - y2) * (float) factor), (int) ((x2 - x) * (float) factor), (int) ((y2 - y) * (float) factor));
    }
}
