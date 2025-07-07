package net.cactus.utils.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static String getContentByPath(String filePath) throws IOException {
        StringBuilder result = new StringBuilder();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (InputStream is = new FileInputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                result.append(new String(buf, 0, len));
            }
        }
        return result.toString();
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static void writeByteArrayToFile(String filePath, byte[] data) throws IOException {
        Files.write(Paths.get(filePath), data);
    }
}
