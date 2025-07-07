package net.cactus.utils.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class FileTransformUtils {
    public static byte[] InputToBytes(InputStream input) throws IOException {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (true) {
                int len = input.read(buffer);
                if (len == -1) {
                    break;
                }
                output.write(buffer, 0, len);
            }
            bytes = output.toByteArray();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] fileToBytes(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToByteArray(file);
    }
}
