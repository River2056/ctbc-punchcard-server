package com.ctbc.punchcard.server.models;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum PunchTimeMode {
    START_TIME("/start"), END_TIME("/end");

    private String uri;

    private PunchTimeMode(String uri) {
        this.uri = uri;
    }

    public static PunchTimeMode getMode(String uri) {
        return Arrays.asList(PunchTimeMode.values()).stream().filter(item -> item.uri.equals(uri))
                .collect(Collectors.toList()).get(0);
    }
}
