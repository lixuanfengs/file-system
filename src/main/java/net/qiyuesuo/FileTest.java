package net.qiyuesuo;

import java.io.IOException;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/FileTest.class */
public class FileTest {
    public static void main(String[] args) throws IOException {
        testUploadFile();
    }

    public static void testUploadFile() throws IOException {
        String msg = HttpClient.sendUploadFileRequest("C:\\信息安全策略.doc", "信息安全策略.doc");
        System.out.println(msg);
    }

    public static void testGetInfo() throws IOException {
        String msg = HttpClient.sendGetInfoRquest("d7e0fc12-ce35-454a-a856-f97380be3484");
        System.out.println(msg);
    }

    public static void testDownLoadFile() throws IOException {
        HttpClient.sendDownloadFileRequest("d7e0fc12-ce35-454a-a856-f97380be3484");
    }

    public static void testDeleteFile() throws IOException {
        HttpClient.sendDeleteFileRequest("d7e0fc12-ce35-454a-a856-f97380be3484");
    }
}
