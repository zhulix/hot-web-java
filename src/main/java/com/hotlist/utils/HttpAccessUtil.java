package com.hotlist.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpAccessUtil {

    static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("Accept", "*");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
    }

    public static String get(String url, Map<String, String> header) {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url)).GET();
            headers.forEach(builder::setHeader);
            header.forEach(builder::setHeader);
            return fetch(client, builder.build()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String get(String url) {
        return get(url, new HashMap<>(1));
    }

    private static HttpResponse<String> fetch(HttpClient client, HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
