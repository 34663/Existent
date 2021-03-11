package zyx.existent.event.events;

import net.minecraft.network.Packet;
import zyx.existent.event.Event;

public class EventPacketReceive extends Event {
    private Packet<?> packet;
    private boolean cancel;

    public EventPacketReceive(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet<?> packet) {
        this.packet = packet;
    }
}
