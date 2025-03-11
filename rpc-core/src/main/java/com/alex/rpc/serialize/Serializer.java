package com.alex.rpc.serialize;

public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
