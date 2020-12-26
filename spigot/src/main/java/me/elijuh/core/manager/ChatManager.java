package me.elijuh.core.manager;

import lombok.Getter;
import lombok.Setter;
import me.elijuh.core.data.ChatMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ChatManager {
    private final Map<String, ChatMessage> lastMessages = new HashMap<>();
    private int cooldown;

    public ChatMessage getLastMessage(Player player) {
        return lastMessages.getOrDefault(player.getName(), null);
    }

    public void setLastMessage(Player player, String message) {
        lastMessages.put(player.getName(), new ChatMessage(player, message));
    }

    public String getPlayerCooldown(Player player) {
        double left = 0.0;
        if (getLastMessage(player) != null) {
            double seconds = System.currentTimeMillis() / 1000.0 - lastMessages.get(player.getName()).getTimestamp() / 1000.0;
            left = Math.max(Math.round((cooldown - seconds) * 10.0) / 10.0, 0);
        }
        return left + "s";
    }
}
