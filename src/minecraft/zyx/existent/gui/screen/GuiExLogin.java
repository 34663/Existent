package zyx.existent.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.misc.LoginUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.io.IOException;

public class GuiExLogin extends GuiScreen {
    private GuiTextField authfield;
    private String status;

    public GuiExLogin() {
        this.status = TextFormatting.GRAY + "Idle...";
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiExButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiExButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Shutdown"));
        this.authfield = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 90, 200, 20);
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                this.mc.shutdown();
                break;
            case 0:
                String pass = authfield.getText();
                LoginUtils loginUtils = new LoginUtils();

                if (pass.length() == 0 || pass == null) {
                    status = TextFormatting.RED + "The information is not entered.";
                    return;
                }
                try {
                    if (loginUtils.auth(pass)) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiExMainMenu());
                    } else {
                        status = TextFormatting.RED + "LoginFailed.";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    status = TextFormatting.RED + "Error.";
                }
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        CFontRenderer font1 = Fonts.default18;
        CFontRenderer font2 = Fonts.comfortaa12;

        this.authfield.drawTextBox();
        font2.drawStringWithShadow("Existent Securety by YAMAMO", 3, this.height - font2.getHeight() - 2, Colors.getColor(130));
        font1.drawCenteredString("Login", this.width / 2F, 20, -1);
        if (this.authfield.getText().isEmpty() && !this.authfield.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Code", this.width / 2 - 96, 96, -7829368);
        }
        font1.drawCenteredString(this.status, this.width / 2F, 30, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void keyTyped(char par1, int par2) {
        this.authfield.textboxKeyTyped(par1, par2);
        if (par1 == '\t' && this.authfield.isFocused()) {
            this.authfield.setFocused(!this.authfield.isFocused());
        }

        if (par1 == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        this.authfield.mouseClicked(par1, par2, par3);
    }
}
