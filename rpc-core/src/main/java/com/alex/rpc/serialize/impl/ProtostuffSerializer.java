package com.alex.rpc.serialize.impl;

import com.alex.rpc.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object obj) {
        Class<?> aClass = obj.getClass();

        Schema schema = RuntimeSchema.getSchema(aClass);
        try {
            log.info("===============Use Protostuff Serializer ===============");
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);

        T t = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, t, schema);
        log.info("===============Use Protostuff Deserializer ===============");
        return t;
    }
}
