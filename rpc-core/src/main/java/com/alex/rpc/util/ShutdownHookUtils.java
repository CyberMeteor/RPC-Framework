package com.alex.rpc.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHookUtils {
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("System stopping running, clear all resources");
            ThreadPoolUtils.shutdownAll();
        }));
    }
}
