package com.alex.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RpcRespStatus {
    SUCCESS(0, "success"),
    FAILED(9999, "failed"),
    ;

    private final int code;
    private final String msg;

}
