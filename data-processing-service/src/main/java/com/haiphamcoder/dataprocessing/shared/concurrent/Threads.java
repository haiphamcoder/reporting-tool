package com.haiphamcoder.dataprocessing.shared.concurrent;

import java.util.concurrent.TimeUnit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Threads {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleep(long millis, TimeUnit unit) {
        try {
            unit.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
