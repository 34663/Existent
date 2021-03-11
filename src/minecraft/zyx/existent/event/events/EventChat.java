package zyx.existent.event.events;

import zyx.existent.event.Event;

public class EventChat extends Event {
    public String message;
    public boolean cancelled;

    public EventChat(String chat) {
        message = chat;
    }

    public String getMessage() {
        return message;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
