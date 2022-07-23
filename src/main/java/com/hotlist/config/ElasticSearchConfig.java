package com.hotlist.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private int port;

    public static final String INDEXES = "hot-list-dev0";

    public static final int size = 30;


    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port)).build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
