package me.elijuh.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BanInfo {
    private final String executor;
    private final String reason;
    private final long expiration;
    private final boolean removed;
    private final boolean ip;
}
