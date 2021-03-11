package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import zyx.existent.gui.screen.GuiExMainMenu;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiIngameMenu extends GuiScreen {
    private final CFontRenderer font = Fonts.default18;
    private int saveStep;
    private int visibleTime;

    public void initGui() {
        this.saveStep = 0;
        this.buttonList.clear();
        int i = -16;
        int j = 98;
        this.buttonList.add(new GuiExButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning()) {
            (this.buttonList.get(0)).displayString = I18n.format("menu.disconnect");
        }

        this.buttonList.add(new GuiExButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiExButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options")));
        GuiButton guibutton = this.addButton(new GuiExButton(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan")));
        guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
        this.buttonList.add(new GuiExButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.advancements")));
        this.buttonList.add(new GuiExButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats")));
        this.buttonList.add(new GuiExButton(8, this.width / 2 - 100, this.height / 4 + 72 + -16, "Copy IGN"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                boolean flag = this.mc.isIntegratedServerRunning();
                boolean flag1 = this.mc.isConnectedToRealms();
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient) null);

                if (flag) {
                    this.mc.displayGuiScreen(new GuiExMainMenu());
                } else if (flag1) {
                    RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiExMainMenu());
                } else {
                    this.mc.displayGuiScreen(new GuiMultiplayer(new GuiExMainMenu()));
                }

            case 2:
            case 3:
            default:
                break;
            case 4:
                this.mc.displayGuiScreen((GuiScreen) null);
                this.mc.setIngameFocus();
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiScreenAdvancements(this.mc.thePlayer.connection.func_191982_f()));
                break;
            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            case 7:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;
            case 8:
                MiscUtils.copyToClipboad(this.mc.thePlayer.getName());
        }
    }



    public void updateScreen() {
        super.updateScreen();
        ++this.visibleTime;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        }
        font.drawCenteredString(I18n.format("menu.game"), this.width / 2F, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
