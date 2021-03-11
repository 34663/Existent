package zyx.existent.module.modules.misc;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.RandomStringUtils;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.misc.UUIDFetcher;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;
import zyx.existent.utils.timer.Timer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class AdminChecker extends Module {
    private int lastAdmins;
    private final ArrayList<String> admins = new ArrayList<String>();
    private final Timer timer = new Timer();

    private final String TAB = "PRESSTAB";

    public AdminChecker(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(TAB, new Setting(TAB, false, "Press TabOnly"));
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        CFontRenderer font = Fonts.Tahoma12;
        int size = 80;
        float xOffset = (sr.getScaledWidth() / 2F) - (size / 2F);
        float yOffset = (Boolean) settings.get(TAB).getValue() ? GuiPlayerTabOverlay.tabHeight + 4 : 18;
        float Y = 0;

        if ((!(Boolean) settings.get(TAB).getValue() || mc.gameSettings.keyBindPlayerList.isKeyDown())) {
            RenderingUtils.rectangleBordered(xOffset + 2.0f, yOffset + 2.0f, xOffset + size - 2.0f, yOffset + (size / 6F) + 3.0f + ((font.getHeight() + 2.2f) * this.admins.size()), 0.5, Colors.getColor(90), Colors.getColor(0));
            RenderingUtils.rectangleBordered(xOffset + 3.0f, yOffset + 3.0f, xOffset + size - 3.0f, yOffset + (size / 6F) + 2.0f + ((font.getHeight() + 2.2f) * this.admins.size()), 0.5, Colors.getColor(27), Colors.getColor(61));
            RenderingUtils.drawRect(xOffset + 4.0f, yOffset + font.getHeight() + 8.5f, xOffset + size - 4.0f, yOffset + font.getHeight() + 8.8f, Colors.getColor(0));
            font.drawStringWithShadow("AdminList [" + admins.size() + "]", xOffset + 6.75f, yOffset + font.getHeight() + 2.8f, -1);
            for (final String admin2 : this.admins) {
                font.drawStringWithShadow(admin2, xOffset + 7.0f, yOffset + font.getHeight() + 12.0f + Y, new Color(255, 60, 60, 255).getRGB());
                Y += font.getHeight() + 2.2f;
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        setSuffix(String.valueOf(this.admins.size()));
        if (this.timer.delay(5000)) {
            this.timer.reset();
            mc.thePlayer.connection.sendPacket(new CPacketTabComplete("/vanishnopacket:vanish ", BlockPos.ORIGIN, false));
        }
    }
    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            SPacketTabComplete packet = (SPacketTabComplete) event.getPacket();
            this.admins.clear();
            String[] matches;
            for (int length = (matches = packet.getMatches()).length, i = 0; i < length; ++i) {
                String user = matches[i];
                String[] administrators;
                for (int length2 = (administrators = this.getAdministrators()).length, j = 0; j < length2; ++j) {
                    String admin = administrators[j];
                    if (user.equalsIgnoreCase(admin)) {
                        this.admins.add(user);
                    }
                }
            }
            this.lastAdmins = this.admins.size();
        } else if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem packetPlayInPlayerListItem = (SPacketPlayerListItem) event.getPacket();
            if (packetPlayInPlayerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
                for (final SPacketPlayerListItem.AddPlayerData addPlayerData : packetPlayInPlayerListItem.getEntries()) {
                    if (this.mc.getConnection().getPlayerInfo(addPlayerData.getProfile().getId()) == null) {
                        String name = this.getName(addPlayerData.getProfile().getId());

                        if (Objects.isNull(name)) {
                            this.checkList("NullPlayer");
                        } else if (Arrays.toString(this.getAdministrators()).contains(name)) {
                            this.checkList(name);
                        }
                    }
                }
            }
        }
    }

    public String[] getAdministrators() {
        return new String[] {
                "ACrispyTortilla",
                "ArcticStorm141",
                "ArsMagia",
                "Captainbenedict",
                "Carrots386",
                "DJ_Pedro",
                "DocCodeSharp",
                "Galap",
                "HighlifeTTU",
                "ImbC",
                "InstantLightning",
                "JTGangsterLP6",
                "Kevin_is_Panda",
                "Kingey",
                "Marine_PvP",
                "MissHilevi",
                "Mistri",
                "Mosh_Von_Void",
                "Navarr",
                "PokeTheEye",
                "Rafiki2085",
                "Robertthegoat",
                "Sevy13",
                "andrew323",
                "dLeMoNb",
                "lazertester",
                "noobfan",
                "skillerfox3",
                "storm345",
                "windex_07",
                "AlecJ",
                "JACOBSMILE",
                "Wayvernia",
                "gunso_",
                "Hughzaz",
                "Murgatron",
                "SaxaphoneWalrus",
                "_Ahri",
                "SakuraWolfVeghetto",
                "jiren74",
                "Dange",
                "Tatre",
                "Pichu2002",
                "LegendaryAlex",
                "LaukNLoad",
                "M4bi",
                "HellionX2",
                "Ktrompfl",
                "Bupin",
                "Murgatron",
                "Outra",
                "CoastinJosh",
        };
    }
    public ArrayList<String> getAdmins() {
        final ArrayList<String> admins = new ArrayList<String>();
        if (mc.getConnection().getPlayerInfoMap() != null) {
            for (final NetworkPlayerInfo player : mc.getConnection().getPlayerInfoMap()) {
                final String text = player.getGameProfile().getName();
                admins.add(text);
            }
        }
        return admins;
    }
    public String getName(final UUID uuid) {
        return UUIDFetcher.getName(uuid);
    }
    private void checkList(final String uuid) {
        if (this.admins.contains(uuid)) {
            return;
        }
        this.admins.add(uuid);
    }
}
