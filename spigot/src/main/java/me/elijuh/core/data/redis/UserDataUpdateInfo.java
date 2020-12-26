package me.elijuh.core.data.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDataUpdateInfo {
    private final String uuid;
    private final String path;
    private final Object value;
}
