package net.qiyuesuo.utils;

import java.util.UUID;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/utils/RandomUtils.class */
public class RandomUtils {
    public static String getRandomFileKey() {
        return UUID.randomUUID().toString();
    }
}
