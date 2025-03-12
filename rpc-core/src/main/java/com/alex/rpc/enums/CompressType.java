package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum CompressType {
    GZIP((byte) 1, "gzip"),
    ;

    private final byte code;
    private final String desc;

    public static  CompressType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Code error: " + code));
    }
}
