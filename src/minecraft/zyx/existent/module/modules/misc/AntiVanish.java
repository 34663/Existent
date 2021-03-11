package zyx.existent.module.modules.misc;

import net.minecraft.network.play.server.SPacketPlayerListItem;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.misc.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AntiVanish extends Module {
    private final List<String> list = new ArrayList<>();
    private final ArrayList<UUID> vanished = new ArrayList<>();

    public AntiVanish(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
        this.lists();
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        if (this.mc.theWorld != null && event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem packetPlayInPlayerListItem = (SPacketPlayerListItem) event.getPacket();
            if (packetPlayInPlayerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
                for (final SPacketPlayerListItem.AddPlayerData addPlayerData : packetPlayInPlayerListItem.getEntries()) {
                    if (this.mc.getConnection().getPlayerInfo(addPlayerData.getProfile().getId()) == null && !this.checkList(addPlayerData.getProfile().getId())) {
                        String name = this.getName(addPlayerData.getProfile().getId());
                        if (Objects.isNull(name)) {
                            name = "Player";
                        }
                        ChatUtils.printChat("\2477[\247cAntiVanish\2477] " + "§e" + name + " §chas Vanished");
                    }
                }
            }
        }
    }

    @EventTarget
    public void preTick(final EventTick tickEvent) {
        try {
            if (!this.mc.isSingleplayer()) {
                for (final UUID uuid : this.vanished) {
                    if (this.mc.getConnection().getPlayerInfo(uuid) != null) {
                        String name = this.getName(uuid);
                        ChatUtils.printChat("\2477[\247cAntiVanish\2477] " + "§e" + name + " §chas Vanished");
                        this.vanished.remove(uuid);
                    }
                }
            }
        } catch (Exception ignored) {
            ;
        }
    }

    public String getName(final UUID uuid) {
        return UUIDFetcher.getName(uuid);
    }
    private boolean checkList(final UUID uuid) {
        if (this.vanished.contains(uuid)) {
            this.vanished.remove(uuid);
            return true;
        }
        this.vanished.add(uuid);
        return false;
    }

    public void lists() {
        list.add("HighlifeTTU");
        list.add("Mistri");
        list.add("Navarr");
        list.add("DocCodeSharp");
        list.add("Galap");
        list.add("lazertester");
        list.add("Mosh_Von_Void");
        list.add("Rafiki2085");
        list.add("Robertthegoat");
        list.add("skillerfox3");
        list.add("storm345");
        list.add("ACrispyTortilla");
        list.add("AlecJ");
        list.add("ArsMagia");
        list.add("Carrots386");
        list.add("DJ_Pedro");
        list.add("dLeMoNb");
        list.add("ImbC");
        list.add("InstantLightning");
        list.add("JACOBSMILE");
        list.add("JTGangsterLP6");
        list.add("JACOBSMILE");
        list.add("Marine_PvP");
        list.add("MissHilevi");
        list.add("noobfan");
        list.add("PokeTheEye");
        list.add("Sevy13");
        list.add("Wayvernia");
        list.add("windex_07");
        list.add("gunso_");
        list.add("Hughzaz");
        list.add("Kingey");
        list.add("Murgatron");
        list.add("SaxaphoneWalrus");
        list.add("_Ahri");
        list.add("SakuraWolfVeghetto");
        list.add("jiren74");
        list.add("Dange");
        list.add("Tatre");
        list.add("Pichu2002");
        list.add("LegendaryAlex");
        list.add("LaukNLoad");
        list.add("M4bi");
        list.add("HellionX2");
        list.add("Ktrompfl");
        list.add("Bupin");
    }
}
