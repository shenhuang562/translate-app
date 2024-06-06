package com.example.trans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TranslateHelper {
    // 百度翻译API的APP ID和密钥
    static String APPID = "20240422002032026";
    static String APPKEY = "oC4yw4ZNmhoCEPWT5qtm";

    public static void init(String appID, String secretKey) {
        APPID = appID;
        APPKEY = secretKey;
    }

    /**
     * 获取翻译结果
     *
     * @param content 要翻译的原文
     * @param to      目标语言
     * @param from    源语言
     * @return 返回翻译结果的 JSON 字符串
     */
    public static String get(String content, String to, String from) throws Exception {
        String salt = UUID.randomUUID().toString();
        String sign = encryptString(APPID + content + salt + APPKEY);
        String url = "https://api.fanyi.baidu.com/api/trans/vip/translate?";
        url += "q=" + URLEncoder.encode(content, "UTF-8");
        url += "&from=" + from;
        url += "&to=" + to;
        url += "&appid=" + APPID;
        url += "&salt=" + salt;
        url += "&sign=" + sign;

        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
        connection.setConnectTimeout(6000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    // 计算MD5值
    public static String encryptString(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(str.getBytes("UTF-8"));
        byte[] byteNew = md5.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : byteNew) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
