package net.cactus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import net.cactus.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.WebContentGenerator;


public class HttpClient {
    private static Logger logger = LoggerFactory.getLogger((Class<?>) HttpClient.class);

    public static String sendUploadFileRequest(String filePath, String fileName) throws IOException {
        String response = HttpUtils.upload("http://localhost:8080/file/upload", filePath, fileName);
        logger.info("服务器相应：" + response);
        return response;
    }

    public static String sendGetInfoRquest(String fileKey) throws IOException {
        URL url = new URL("http://localhost:8080/file/getInfo?fileKey=" + fileKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return HttpUtils.getResponse(conn, WebContentGenerator.METHOD_GET);
    }

    public static String sendDeleteFileRequest(String fileKey) throws IOException {
        URL url = new URL("http://localhost:8080/file/delete?fileKey=" + fileKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return HttpUtils.getResponse(conn, WebContentGenerator.METHOD_POST);
    }

    public static byte[] sendDownloadFileRequest(String fileKey) throws IOException {
        URL url = new URL("http://localhost:8080/file/download?fileKey=" + fileKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return HttpUtils.getResponse(conn, WebContentGenerator.METHOD_GET).getBytes();
    }
}
