package com.alex.rpc.spi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.alex.rpc.serialize.Serializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomLoader<T> {
    private static final String BASE_PATH = "META-INF/RPC-Framework/";
    
    private final Class<T> type;
    private final Map<String, Class<T>> clazzCache = new ConcurrentHashMap<>();
    private final Map<String, Holder<T>> objCache = new ConcurrentHashMap<>();

    private static final Map<Class<?>, CustomLoader<?>> LOAD_CACHE = new ConcurrentHashMap<>();

    public CustomLoader(Class<T> type) {
        this.type = type;
    }

    public static <V> CustomLoader<V> getLoader(Class<V> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz can not be null");
        }

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("clazz is not interface");
        }

        return (CustomLoader<V>) LOAD_CACHE.computeIfAbsent(clazz, __ -> new CustomLoader<>(clazz));
    }
    
    public T get(String name) {
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }

        Holder<T> holder = objCache.computeIfAbsent(name, __ -> new Holder<>());

        T t = holder.get();
        if (t == null) {
            synchronized (holder) {
                t = holder.get();
                if (t == null) {
                    t = createObj(name);
                    holder.set(t);
                }
            }
        }
        
        return t;
    }

    @SneakyThrows
    private T createObj(String name) {
        if (CollUtil.isEmpty(clazzCache)) {
            loadDir();
        }

        Class<T> clazz = clazzCache.get(name);
        return clazz.newInstance();
    }

    @SneakyThrows
    private void loadDir() {
        String path = BASE_PATH + type.getName();

        ClassLoader classLoader = CustomLoader.class.getClassLoader();
        Enumeration<URL> urls = classLoader.getResources(path);

        if (CollUtil.isEmpty(urls)) {
            throw new RuntimeException("File does not exist: " + path);
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            loadResource(classLoader, url);
        }
    }

    @SneakyThrows
    private void loadResource(ClassLoader classLoader, URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))){
            String line;

            while ((line = reader.readLine()) != null) {
                Pair<String, Class<T>> pair = handleLine(classLoader, line);
                if (pair == null) {
                    continue;
                }

                clazzCache.put(pair.getKey(), pair.getValue());
            }
        }
    }

    @SneakyThrows
    private Pair<String, Class<T>> handleLine(ClassLoader classLoader, String line) {
        line = line.trim();

        if  (StrUtil.isBlank(line)) {
            return  null;
        }

        String[] split = line.split("=");
        if (split.length != 2) {
            throw new RuntimeException("Invalid line");
        }

        Class<T> clazz = (Class<T>) classLoader.loadClass(split[1]);
        return new Pair<>(split[0], clazz);
    }

    public static void main(String[] args) {
        CustomLoader<Serializer> loader = CustomLoader.getLoader(Serializer.class);
        Serializer serializer = loader.get("protostuff");

        System.out.println(serializer);
    }
}
