package zyx.existent.gui.screen;

import net.minecraft.client.gui.*;
import zyx.existent.Existent;
import zyx.existent.gui.altmanager.GuiAltManager;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.changelog.ChangeLog;
import zyx.existent.utils.misc.LoginUtils;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import java.awt.*;
import java.io.IOException;

public class GuiExMainMenu extends GuiScreen {
    private int width;
    private int height;
    private final CFontRenderer font = Fonts.Tahoma14;
    private final CFontRenderer font2 = Fonts.Tahoma24;
    private final CFontRenderer font3 = Fonts.default18;
    private final CFontRenderer Main = Fonts.Helvetica45;

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();
        this.buttonList.add(new GuiExButton(0, (this.width / 2) - 100, this.height / 2 - 50, "Singleplayer"));
        this.buttonList.add(new GuiExButton(1, this.width / 2 - 100, this.height / 2 - 25, "Multiplayer"));
        this.buttonList.add(new GuiExButton(2, this.width / 2 - 100, this.height / 2, "Alt Manager"));
        this.buttonList.add(new GuiExButton(3, this.width / 2 - 100, this.height / 2 + 25, "Settings"));
        this.buttonList.add(new GuiExButton(4, this.width / 2 - 100, this.height / 2 + 50, "Exit"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        ScaledResolution sr = new ScaledResolution(mc);
        int y = 20;
        int x = 6;

        Main.drawStringWithShadow(Existent.CLIENT_NAME, (sr.getScaledWidth() / 2F) - (Main.getStringWidth(Existent.CLIENT_NAME) / 2F), sr.getScaledHeight() / 2F - 100, -1);
        font3.drawStringWithShadow("Build : \2477" + Existent.CLIENT_BUILD, 4, sr.getScaledHeight() - font3.getHeight() - 1, -1);
        font3.drawStringWithShadow("User : \2477" + LoginUtils.name, 4, sr.getScaledHeight() - (font3.getHeight() * 2) - 2, -1);
        font3.drawStringWithShadow("Copyright Mojang AB. Do not distribute!", sr.getScaledWidth() - font3.getStringWidth("Copyright Mojang AB. Do not distribute!") - 2, sr.getScaledHeight() - font3.getHeight() - 1, -1);
        font2.drawStringWithShadow("\247cC\247fhangeLog", 5, 6, -1);
        for (ChangeLog log : Existent.getChangeLogManager().getChangeLogs()) {
            if (Existent.getChangeLogManager().getChangeLogs() != null) {
                font.drawStringWithShadow(log.getChangeName(), x, y, -1);
                y += 8;
            } else {
                font.drawStringWithShadow("Cannot load Changelogs", x, y, -1);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiAltManager());
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 4:
                System.exit(0);
                break;
        }
        super.actionPerformed(button);
    }

    public static Color effect(long offset, float brightness, int speed) {
        float hue = (float) (System.nanoTime() + (offset * speed)) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, brightness, 1F)), 16);
        Color c = new Color((int) color);
        return new Color(c.getGreen() / 255.0f, c.getBlue() / 255.0F, c.getRed() / 255.0F, c.getAlpha() / 255.0F);
    }
}
