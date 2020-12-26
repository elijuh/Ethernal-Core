package me.elijuh.core.data.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.elijuh.core.data.Punishment;

@Getter
@AllArgsConstructor
public class PunishmentInfo {
    private final Punishment type;
    private final boolean removal;
    private final long length;
    private final String reason;
    private final String executor;
    private final String punished;
    private final String executorDisplay;
    private final String punishedDisplay;

    public long getExpiration() {
        return System.currentTimeMillis() + length;
    }
}
