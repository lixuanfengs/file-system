package net.qiyuesuo.config;

import javax.servlet.MultipartConfigElement;
import net.qiyuesuo.utils.file.PropertiesUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/config/JettyServer.class */
public class JettyServer {
    private static final String CONTEXT_PATH = "/";
    private static final String MAPPING_URL = "/*";

    public void run() throws Exception {
        Server server = new Server();
        int port = Integer.parseInt(PropertiesUtils.getConfigProperty("serverport"));
        String host = PropertiesUtils.getConfigProperty("serverhost");
        ServerConnector http = new ServerConnector(server);
        http.setHost(host);
        http.setPort(port);
        http.setIdleTimeout(1200000L);
        server.addConnector(http);
        server.setHandler(servletContextHandler(webApplicationContext()));
        server.start();
        server.join();
    }

    private ServletContextHandler servletContextHandler(WebApplicationContext context) {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        ServletHolder holder = new ServletHolder(new DispatcherServlet(context));
        holder.getRegistration().setMultipartConfig(new MultipartConfigElement("", 209715200L, 1887436800L, Netty4ClientHttpRequestFactory.DEFAULT_MAX_RESPONSE_SIZE));
        handler.addServlet(holder, "/*");
        handler.addEventListener(new ContextLoaderListener(context));
        return handler;
    }

    private WebApplicationContext webApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfiguration.class);
        return context;
    }
}
