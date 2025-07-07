package net.cactus.utils;

import java.util.UUID;


public class RandomUtils {
    public static String getRandomFileKey() {
        return UUID.randomUUID().toString();
    }
}
