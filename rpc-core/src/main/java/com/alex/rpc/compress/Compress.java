package com.alex.rpc.compress;

public interface Compress {
    byte[] compress(byte[] data);

    byte[] decompress(byte[] data);
}
