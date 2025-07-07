package net.qiyuesuo;

import net.qiyuesuo.config.JettyServer;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/FileServerApplication.class */
public class FileServerApplication {
    public static void main(String[] args) throws Exception {
        new JettyServer().run();
    }
}
