package com.alex.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResp<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private Integer code;
    private String msg;
    private T data;


    public static <T> RpcResp<T> success(T data) {
        RpcResp<T> resp = new RpcResp<T>();
        resp.setCode(0);
        resp.setData(data);

        return resp;
    }

    public static <T> RpcResp<T> fail(T data) {
        RpcResp<T> resp = new RpcResp<T>();
        resp.setCode(0);
        resp.setData(data);

        return resp;
    }

}
