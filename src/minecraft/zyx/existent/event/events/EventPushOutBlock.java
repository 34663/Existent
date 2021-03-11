package zyx.existent.event.events;

import zyx.existent.event.Event;

public class EventPushOutBlock extends Event {
    public boolean isPre;

    public EventPushOutBlock(boolean pre) {
        isPre = pre;
    }

    public boolean isPre() {
        return isPre;
    }
    public boolean isPost() {
        return !isPre;
    }
}
