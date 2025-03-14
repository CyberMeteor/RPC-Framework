package com.alex.rpc.serialize.impl;

import com.alex.rpc.serialize.Serializer;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(os);
            hessianOutput.writeObject(obj);

            log.info("==============Use Hessian Serializer ===============");
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(is);
            Object o = hessianInput.readObject();
            log.info("==============Use Hessian Deserializer ===============");
            return clazz.cast(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
