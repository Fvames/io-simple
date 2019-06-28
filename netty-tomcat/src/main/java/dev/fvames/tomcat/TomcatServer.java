package dev.fvames.tomcat;

import dev.fvames.tomcat.http.Servlet;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @version 2019/6/28 17:27
 */

public class TomcatServer {

    private Map<String, Servlet> servletMap = new HashMap<>();

    public void start(int port) {

        String path = this.getClass().getResource("/").getPath();
        try {

            FileInputStream inputStream = new FileInputStream(path + "web.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            for (Object k : properties.keySet()) {

                String key = k.toString();
                if (key.endsWith(".url")) {

                    String url = properties.getProperty(key);
                    String serverName = key.replaceAll("\\.url", "");
                    String className = properties.getProperty(serverName + ".className");
                    Servlet servlet = (Servlet) Class.forName(className).newInstance();

                    servletMap.put(url, servlet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
