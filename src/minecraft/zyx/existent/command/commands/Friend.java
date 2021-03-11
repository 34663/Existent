package zyx.existent.command.commands;

import joptsimple.internal.Strings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.Existent;
import zyx.existent.command.Command;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.utils.ChatUtils;

public class Friend extends Command {
    public Friend(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args.length == 0) {
            ChatUtils.printChatprefix("-friend add <UserName>");
            ChatUtils.printChatprefix("-friend del <UserName>");
            ChatUtils.printChatprefix("-friend clear");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("add")) {
                ChatUtils.printChatprefix("Friend : -friend add <UserName>");
            } else if (args[0].equalsIgnoreCase("del")) {
                ChatUtils.printChatprefix("Friend : -friend del <UserName>");
            } else if (args[0].equalsIgnoreCase("clear")) {
                Existent.getFriendManager().getFriends().clear();
                NotificationPublisher.queue("FriendManager", "Clear Friends", NotificationType.WARNING);
            } else if (args[0].equalsIgnoreCase("list")) {
                ChatUtils.printChatprefix("Friend" + "\2477[\247f" + Existent.getFriendManager().getFriends().size() + "\2477]\247f" + " : \247a" + Existent.getFriendManager().getFriendsName());
            }
        } else if (args.length == 2) {
            String nick = args[1];

            if (nick.equalsIgnoreCase(mc.thePlayer.getName())) {
                NotificationPublisher.queue("FriendManager", "You cannot add yourself.", NotificationType.ERROR);
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                Existent.getFriendManager().add(nick);
                NotificationPublisher.queue("FriendManager", "Add Friend: " + nick, NotificationType.SUCCESS);
            } else if (args[0].equalsIgnoreCase("del")) {
                Existent.getFriendManager().remove(nick);
                NotificationPublisher.queue("FriendManager", "Delete Friend: " + nick, NotificationType.SUCCESS);
            }
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
