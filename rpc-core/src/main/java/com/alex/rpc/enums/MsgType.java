package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum MsgType {
    HEARTBEAT_REQ((byte) 1, "heartbeat request"),
    HEARTBEAT_RESP((byte) 2, "heartbeat response"),
    RPC_REQ((byte) 3, "rpc request"),
    RPC_RESP((byte) 4, "rpc response"),
    ;

    private final byte code;
    private final String desc;

    public boolean isHeartbeat() {
        return this == HEARTBEAT_REQ || this == HEARTBEAT_RESP;
    }

    public boolean isReq() {
        return this == RPC_REQ || this == HEARTBEAT_REQ;
    }

    public static  MsgType from(byte code) {
        return Arrays.stream(values())
                .filter(msgType -> msgType.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Code error: " + code));
    }
}
