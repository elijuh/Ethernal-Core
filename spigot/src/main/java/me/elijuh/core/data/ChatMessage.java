package me.elijuh.core.data;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class ChatMessage {
    private final Player sender;
    private final String message;
    private final long timestamp;

    public ChatMessage(Player sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
