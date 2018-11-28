package com.xxl.conf.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class BaseHttpUtil {
    private static Logger logger = LoggerFactory.getLogger(BaseHttpUtil.class);

    /**
     * get
     *
     * @param url
     * @param timeout second
     * @return
     */
    public static String get(String url, int timeout) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // connection setting
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            // do connection
            connection.connect();

            //Map<String, List<String>> map = connection.getHeaderFields();
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/xxl-rpc-admin/registry/discovery?biz=xxl-rpc&env=test&key=service01";
        System.out.println(get(url, 3));
    }

}
