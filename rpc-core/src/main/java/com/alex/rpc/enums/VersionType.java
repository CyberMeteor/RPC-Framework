package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum VersionType {
    VERSION1((byte)1,"Version1");

    private final byte code;
    private final String desc;
}
