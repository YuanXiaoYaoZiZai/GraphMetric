package com.fsc.metric.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Created by Frank.Huang on 2016/6/15.
 */
public class HttpTools {


    private static final Logger logger = LoggerFactory.getLogger(HttpTools.class);

    public static String getRemoteHost(HttpServletRequest request) {
        String remoteHost = null;
        //ipAddress = this.getRequest().getRemoteAddr();
        remoteHost = request.getHeader("x-forwarded-for");
        if (remoteHost == null || remoteHost.length() == 0 || "unknown".equalsIgnoreCase(remoteHost)) {
            remoteHost = request.getHeader("CF-Connecting-IP");
        }
        if (remoteHost == null || remoteHost.length() == 0 || "unknown".equalsIgnoreCase(remoteHost)) {
            remoteHost = request.getHeader("Proxy-Client-IP");
        }
        if (remoteHost == null || remoteHost.length() == 0 || "unknown".equalsIgnoreCase(remoteHost)) {
            remoteHost = request.getHeader("WL-Proxy-Client-IP");
        }
        if (remoteHost == null || remoteHost.length() == 0 || "unknown".equalsIgnoreCase(remoteHost)) {
            remoteHost = request.getRemoteAddr();
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (remoteHost != null && remoteHost.length() > 15) { //"***.***.***.***".length() = 15
            if (remoteHost.indexOf(",") > 0) {
                remoteHost = remoteHost.substring(0, remoteHost.indexOf(","));
            }
        }

        if (StringUtils.isEmpty(remoteHost)) {
            remoteHost = "unknown host";
        }

        return remoteHost;
    }


    public static byte[] executeQuery(String url) {
        int count = 0;
        while (count < 3) {
            try {
                byte[] content = getHttpInputStream(url);
                if (null == content) {
                    count++;
                    continue;
                } else {
                    return content;
                }
            } catch (Exception e) {
                count++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                logger.error("Execute Query error. [{}]", url, e);
            }
        }
        return null;
    }

    private static byte[] getHttpInputStream(String url) throws Exception {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = getHttpURLConnection(url);
            inputStream = connection.getInputStream();
            int retCode = connection.getResponseCode();
            if (retCode >= 200 && retCode < 400) {
                return IOUtils.toByteArray(inputStream);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeInputStreamQuietly(inputStream);
            closeConnectionQuietly(connection);
        }
    }

    public static HttpURLConnection getHttpURLConnection(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(2_000);
        connection.setReadTimeout(2_000);
        return connection;
    }

    private static void closeConnectionQuietly(HttpURLConnection connection) {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeInputStreamQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
