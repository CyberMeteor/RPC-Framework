package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1, "Kryo"),
    ;

    private final byte code;
    private final String desc;
}
