package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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
}
