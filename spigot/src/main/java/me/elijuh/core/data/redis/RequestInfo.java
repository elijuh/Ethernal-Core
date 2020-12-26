package me.elijuh.core.data.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestInfo {
    private final String server;
    private final String requester;
    private final String request;
}
