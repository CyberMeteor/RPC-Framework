package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1, "Kryo"),
    ;

    private final byte code;
    private final String desc;

    public static  SerializeType from(byte code) {
        return Arrays.stream(values())
                .filter(o -> o.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Code error: " + code));
    }
}
