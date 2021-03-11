package zyx.existent.module.modules.misc;

import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextFormatting;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.*;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.timer.Timer;
import java.util.ArrayList;

public class AutoL extends Module {
    private final Timer LTimer = new Timer();
    public ArrayList<String> sendQueue = new ArrayList<>();
    public static final String MESSAGE = "MESSAGE";
    private final String MODE = "MODE";
    private final String SEND = "SEND";
    private final String DELAY = "DELAY";

    public AutoL(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Custom", new String[]{"Custom", "Other"}), "AutoL method"));
        settings.put(SEND, new Setting(SEND, new Options("Send Mode", "Global", new String[]{"Global", "Direct", "Normal"}), "AutoL method"));
        settings.put(MESSAGE, new Setting(MESSAGE, "L", "AutoL Text"));
        settings.put(DELAY, new Setting(DELAY, 2, "LDelay", 1, 1, 5));
    }

    @EventTarget
    public void onTick(EventTick event) {
        String currentSend = ((Options) settings.get(SEND).getValue()).getSelected();
        String[] message = null;

        if (!this.sendQueue.isEmpty() && this.LTimer.delay(((Number) settings.get(DELAY).getValue()).floatValue() * 1000)) {
            String user = this.sendQueue.get(0);
            if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Custom")) {
                if (((Options) settings.get(SEND).getValue()).getSelected().equalsIgnoreCase("Direct")) {
                    message = new String[]{(String) settings.get(MESSAGE).getValue()};
                } else {
                    message = new String[]{user + " " + settings.get(MESSAGE).getValue()};
                }
            } else if (((Options) settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Other")) {
                message = new String[]{
                        "ᴇxɪsᴛᴇɴᴛ Client > " + user,
                        user + "のAimはᴇxɪsᴛᴇɴᴛクライアントより弱い！？",
                        user + "、お前のaim死んでるの？",
                        user + " 嫉妬乙！",
                        "ハック? いいえ、" + user + "は頭が弱いみたいですね。ᴇxɪsᴛᴇɴᴛ Clientの性能ですよ！",
                        "大変！ " + user + "はアトピーです！直ちに寄付を！ https://sellix.io/Existent",
                        user + "のping頭悪くね??? Wifi変えろやｗｗ",
                        "この" + user + "って人私に負けてます！弱すぎです！",
                        "ᴇxɪsᴛᴇɴᴛクライアントによって" + user + "は倒されました。",
                        user + "は https://sellix.io/Existent ここからᴇxɪsᴛᴇɴᴛを購入します。",
                        user + "が死にました！" + user + "ってどうせ/rpするんだろ。",
                        "ᴇxɪsᴛᴇɴᴛクライアントは" + user + "を超越するクライアントです。 https://sellix.io/Existent",
                        user + "は殺したらLとオールチャットで言います。これがイキりです。",
                };
            }

            if (message != null) {
                sendMessage(user, currentSend, message[KillAura.randomNumber(0, message.length)]);
                this.sendQueue.remove(0);
                this.LTimer.reset();
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());
            if (message != null && message.length() > 0) {
                String[] text = message.split(" ");
                if ((text[1].equalsIgnoreCase("killed") || text[1].equalsIgnoreCase("shot")) && text[0].startsWith(mc.thePlayer.getName() + "(")) {
                    String user = text[2].replaceAll("\\(.+?\\)", "");
                    this.sendQueue.add(user);
                }
            }
        }
    }

    private void sendMessage(String user, String mode, String message) {
        if (mode.equalsIgnoreCase("Global")) {
            mc.thePlayer.sendChatMessage("!" + message);
        } else if (mode.equalsIgnoreCase("Direct")) {
            mc.thePlayer.sendChatMessage("@" + user + " " + message);
        } else if (mode.equalsIgnoreCase("Normal")) {
            mc.thePlayer.sendChatMessage(message);
        }
    }
}
