package com.hotlist.utils;

import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class HttpAccessUtil {

    static Map<String, String> headers = new HashMap<>(1);
    static HttpClient client;
    static Map<String, HttpRequest> requestMap = new HashMap<>();

    static {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
//        headers.put("Accept", "*");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
    }

    public static String get(String url, Map<String, String> header) {
        try {

            HttpRequest httpRequest = requestMap
                    .containsKey(url) ? requestMap.get(url) : createReq(url, header);
            return fetch(client, httpRequest).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static HttpRequest createReq(String url, Map<String, String> header) throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder(new URI(url))
                .timeout(Duration.ofSeconds(10))
                .GET();
        headers.forEach(builder::setHeader);
        if (!CollectionUtils.isEmpty(header)) header.forEach(builder::setHeader);
        return builder.build();
    }

    public static String get(String url) {
        return get(url, null);
    }

    private static HttpResponse<String> fetch(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }


}
