package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import net.minecraft.util.text.ITextComponent;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.Alt;
import zyx.existent.gui.altmanager.AltLoginThread;
import zyx.existent.gui.altmanager.AltManager;
import zyx.existent.gui.altmanager.GuiAltManager;
import zyx.existent.gui.altmanager.althening.GuiAlthening;
import zyx.existent.gui.altmanager.althening.api.AltService;
import zyx.existent.gui.altmanager.althening.api.api.TheAltening;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import zyx.existent.utils.timer.Timer;

import static com.mojang.authlib.Agent.MINECRAFT;
import static java.net.Proxy.NO_PROXY;

public class GuiDisconnected extends GuiScreen {
    private final String reason;
    private final ITextComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int textHeight;
    private AltLoginThread loginThread;
    private final CFontRenderer font = Fonts.default18;
    private final Timer timer = new Timer();

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiExButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")));

        this.buttonList.add(new GuiExButton(1, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 25, 100, 20, "Reconnect"));
        this.buttonList.add(new GuiExButton(2, this.width / 2, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 25, 100, 20, "Alt Manager"));
        this.buttonList.add(new GuiExButton(3, this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 45, 100, 20, "New Alt(MOJANG)"));
        this.buttonList.add(new GuiExButton(4, this.width / 2, this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 45, 100, 20, "New Alt(ALTHENING)"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
            case 1:
                try {
                    GuiMultiplayer gui = (GuiMultiplayer)this.parentScreen;
                    gui.connectToSelected();
                } catch (Exception ignored) {
                    ;
                }
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiAltManager());
                break;
            case 4:
                try {
                    TheAltening altening = new TheAltening((Existent.getAPI() != null) ? Existent.getAPI() : "error");

                    try {
                        GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);

                        YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, ""), MINECRAFT);
                        yggdrasilUserAuthentication.setUsername(altening.getAccountData().getToken());
                        yggdrasilUserAuthentication.setPassword("Password");

                        try {
                            yggdrasilUserAuthentication.logIn();
                            mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");
                            GuiAlthening.rank1 = altening.getAccountData().getInfo().getHypixelRank();
                            GuiAlthening.level1 = altening.getAccountData().getInfo().getHypixelLevel();
                            GuiAlthening.rank2 = altening.getAccountData().getInfo().getMineplexRank();
                            GuiAlthening.level2 = altening.getAccountData().getInfo().getMineplexLevel();

                            NotificationPublisher.queue("TheAlthening", "Logged in \247a" + yggdrasilUserAuthentication.getSelectedProfile().getName(), NotificationType.SUCCESS);
                            GuiMultiplayer gui = (GuiMultiplayer)this.parentScreen;
                            gui.connectToSelected();
                        } catch (AuthenticationException e) {
                            NotificationPublisher.queue("TheAlthening", "Switch AltService", NotificationType.ERROR);
                            GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);
                        }
                    } catch (Throwable throwable) {
                        NotificationPublisher.queue("TheAlthening", "Error2 :(", NotificationType.ERROR);
                        System.out.println("Error2 :(");
                    }
                } catch (Exception e) {
                    NotificationPublisher.queue("TheAlthening", "Error1 :(", NotificationType.ERROR);
                    System.out.println("Error1 :(");
                }
                break;
            case 3:
                ArrayList registry = AltManager.registry;
                Random random = new Random();
                Alt randomAlt = (Alt) registry.get(random.nextInt(AltManager.registry.size()));
                if (randomAlt != null) {
                    (this.loginThread = new AltLoginThread(randomAlt)).start();

                    NotificationPublisher.queue("Alt Manager", "Successfully logged in.", NotificationType.SUCCESS);
                    GuiMultiplayer gui = (GuiMultiplayer)this.parentScreen;
                    gui.connectToSelected();
                } else {
                    NotificationPublisher.queue("Alt Manager", "Alt not found.", NotificationType.ERROR);
                }
                break;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        font.drawCenteredString(this.reason, this.width / 2F, this.height / 2F - this.textHeight / 2F - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.textHeight / 2;

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                font.drawCenteredString(s, this.width / 2F, i, 16777215);
                i += font.getStringHeight(s);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
