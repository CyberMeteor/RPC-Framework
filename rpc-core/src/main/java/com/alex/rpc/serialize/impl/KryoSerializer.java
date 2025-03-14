package com.alex.rpc.serialize.impl;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class KryoSerializer implements Serializer {
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcReq.class);
        kryo.register(RpcResp.class);

        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream();
             Output output = new Output(oos)) {

            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output, obj);
            output.flush();

            log.info("==============Use Kryo Serializer ===============");

            return oos.toByteArray();
        } catch (Exception e) {
            log.error("Kryo serialize failed", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Input input = new Input(is)) {

            Kryo kryo = KRYO_THREAD_LOCAL.get();
            log.info("==============Use Kryo Deserializer ===============");
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            log.error("Kryo deserialize failed", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
