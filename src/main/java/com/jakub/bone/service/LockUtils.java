package com.jakub.bone.service;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class LockUtils {

    public static <T> T executeWithLock(Lock lock, Supplier<T> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    public static void executeWithLock(Lock lock, Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
}
