package com.alex.rpc.constant;

public class RpcConstant {
    public static final int SERVER_PORT = 8888;

    public static final String ZK_IP =  "127.0.0.1";
    public static final int ZK_PORT = 2181;
    public static final String ZK_RPC_ROOT_PATH = "/RPC-Framework";

    public static final String NETTY_RPC_KEY = "RpcResp";
    public static final byte[] RPC_MAGIC_CODE = new byte[]{(byte) 'r', (byte) 'p', (byte) 'c', (byte) 'f'};
    public static final int RPC_HEAD_LEN = 16;
    public static final int RPC_MAX_LEN = 1024 * 1024;
}
