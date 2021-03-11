package zyx.existent.gui.altmanager.althening;

import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.GuiAltManager;
import zyx.existent.gui.altmanager.PasswordField;
import zyx.existent.gui.altmanager.althening.api.AltService;
import zyx.existent.gui.altmanager.althening.api.api.TheAltening;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

import static com.mojang.authlib.Agent.MINECRAFT;
import static java.net.Proxy.NO_PROXY;

public class GuiAlthening extends GuiScreen {
    private final GuiAltManager manager;
    public static String rank1 = "none";
    public static String rank2 = "none";
    public static int level1 = 0;
    public static int level2 = 0;
    private GuiExButton login;
    private GuiExButton generate;
    private PasswordField apikeyField;
    private GuiTextField tokenField;
    public String status = "";

    public GuiAlthening(GuiScreen manager) {
        this.manager = (GuiAltManager) manager;
    }

    public void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(manager);
                break;
            case 1:
                String api = apikeyField.getText();
                final TheAltening theAltening = new TheAltening(api.contains("api") ? api : "");
                Existent.setAPI(api.contains("api") ? api : "");

                try {
                    status = "\247cLogging in...";
                    GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
                    final UserAuthentication authentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, UUID.randomUUID().toString()), MINECRAFT);
                    authentication.setUsername(theAltening.getAccountData().getToken());
                    authentication.setPassword("Existent");

                    authentication.logIn();
                    mc.session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "legacy");
                    rank1 = theAltening.getAccountData().getInfo().getHypixelRank();
                    level1 = theAltening.getAccountData().getInfo().getHypixelLevel();
                    rank2 = theAltening.getAccountData().getInfo().getMineplexRank();
                    level2 = theAltening.getAccountData().getInfo().getMineplexLevel();

                    manager.status = "\247fYour name is now \247a" + authentication.getSelectedProfile().getName() + "\247f.";
                    mc.displayGuiScreen(manager);
                } catch (Throwable e) {
                    status = "\247cFailed Login";
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
                    status = "\247cLogging in...";
                    YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, ""), MINECRAFT);
                    yggdrasilUserAuthentication.setUsername(tokenField.getText());
                    yggdrasilUserAuthentication.setPassword("Existent");

                    yggdrasilUserAuthentication.logIn();
                    mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");

                    manager.status = "\247fYour name is now \247a" + yggdrasilUserAuthentication.getSelectedProfile().getName() + "f.";
                    mc.displayGuiScreen(manager);
                } catch (Throwable t) {
                    t.printStackTrace();
                    status = "\247cThat Token cannot be used.";
                }
                break;
            case 3:
                MiscUtils.showURL("https://thealtening.com/");
                break;
        }
        super.actionPerformed(button);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        CFontRenderer fonts = Fonts.default18;
        fonts.drawCenteredString("TheAltening", width / 2F, 6, 0xffffff);
        fonts.drawCenteredString(status, width / 2F, 18, 0xffffff);

        apikeyField.drawTextBox();
        tokenField.drawTextBox();
        if (this.apikeyField.getText().isEmpty() && !this.apikeyField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Api-Key", this.width / 2 - 96, 156, -7829368);
        }
        if (this.tokenField.getText().isEmpty() && !this.tokenField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Token", this.width / 2 - 96, 86, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        FontRenderer font = mc.fontRendererObj;
        Keyboard.enableRepeatEvents(true);

        login = new GuiExButton(2, width / 2 - 100, 105, "Login");
        buttonList.add(login);
        generate = new GuiExButton(1, width / 2 - 100, 175, "Generate");
        buttonList.add(generate);
        buttonList.add(new GuiExButton(3, width / 2 - 100, height - 83, "Buy"));
        buttonList.add(new GuiExButton(0, width / 2 - 100, height - 60, "Back"));
        tokenField = new GuiTextField(666, font, width / 2 - 100, 80, 200, 20);
        tokenField.maxStringLength = Integer.MAX_VALUE;
        apikeyField = new PasswordField(font, width / 2 - 100, 150, 200, 20);
        apikeyField.setText((Existent.getAPI() != null) ? Existent.getAPI() : "");
        apikeyField.maxStringLength = 18;
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Check if user want to escape from screen
        if (Keyboard.KEY_ESCAPE == keyCode) {
            // Send back to prev screen
            mc.displayGuiScreen(manager);
            return;
        }

        // Check if field is focused, then call key typed
        if (apikeyField.isFocused) apikeyField.textboxKeyTyped(typedChar, keyCode);
        if (tokenField.isFocused) tokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Call mouse clicked to field
        apikeyField.mouseClicked(mouseX, mouseY, mouseButton);
        tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        apikeyField.updateCursorCounter();
        tokenField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
}
