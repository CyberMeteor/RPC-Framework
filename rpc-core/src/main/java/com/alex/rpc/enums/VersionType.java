package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@Getter
@AllArgsConstructor
public enum VersionType {
    VERSION1((byte)1,"Version1");

    private final byte code;
    private final String desc;

    public static  VersionType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Code error: " + code));
    }
}
