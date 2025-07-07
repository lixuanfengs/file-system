package net.qiyuesuo.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.servlet.support.WebContentGenerator;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String getResponse(HttpURLConnection conn, String requestMethod) throws IOException {
        StringBuilder msg = new StringBuilder();
        try {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(3000);
            if ("GET".equals(requestMethod)) {
                conn.setRequestMethod("GET");
                conn.setUseCaches(true);
            } else {
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
            }
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                try (InputStream input = conn.getInputStream()) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = input.read(buf)) != -1) {
                        msg.append(new String(buf, 0, len));
                    }
                }
            } else {
                logger.error("HTTP请求失败，响应码: " + code);
            }
        } catch (IOException e) {
            logger.error("HTTP请求异常", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return msg.toString();
    }

    public static String upload(String strUrl, String filePath, String fileName) throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String prefix = "--" + boundary;
        StringBuilder msg = new StringBuilder();

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(strUrl).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Accept", "*/*");

            Path path = Paths.get(filePath);
            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                 FileInputStream input = new FileInputStream(path.toFile())) {

                StringBuilder sb = new StringBuilder();
                sb.append(prefix).append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
                sb.append("Content-Type: application/octet-stream\r\n\r\n");
                out.write(sb.toString().getBytes());

                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                out.write(("\r\n" + prefix + "--\r\n").getBytes());
                out.flush();
            }

            if (conn.getResponseCode() == 200) {
                try (InputStream inputStream = conn.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        msg.append(new String(buffer, 0, len));
                    }
                }
            }
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return msg.toString();
    }
}
