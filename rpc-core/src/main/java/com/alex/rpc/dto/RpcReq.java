package com.alex.rpc.dto;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private  String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;
    private String version;
    private String group;


    public String rpcServiceName() {
        return getInterfaceName()
                + StrUtil.blankToDefault(getVersion(), StrUtil.EMPTY)
                +  StrUtil.blankToDefault(getGroup(), StrUtil.EMPTY);
    }
}
