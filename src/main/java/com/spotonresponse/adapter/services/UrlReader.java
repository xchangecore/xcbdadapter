package com.spotonresponse.adapter.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlReader {

    private static Logger logger = LoggerFactory.getLogger(UrlReader.class);

    private String content = null;

    public UrlReader(String urlName) {

        BufferedReader reader;
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlName);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // int status = con.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer sb = new StringBuffer();
            logger.info("start read URL: [{}]", urlName);
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            logger.info("done with URL: [{}]", urlName);
            reader.close();
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

    }

    public String getContent() {

        return content;
    }
}
