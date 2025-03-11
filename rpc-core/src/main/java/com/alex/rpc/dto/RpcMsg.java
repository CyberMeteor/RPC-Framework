package com.alex.rpc.dto;

import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer reqId;
    private VersionType version;
    private MsgType msgType;
    private SerializeType serializeType;
    private CompressType compressType;
    private Object data;
}
