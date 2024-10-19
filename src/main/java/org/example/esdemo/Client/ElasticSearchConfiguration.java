package org.example.esdemo.Client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.Resource;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {

    @Value("${es.server.address}")
    private String serverUrl;

    @Value("${es.api.key}")
    private String apiKey;

    // Create the low-level client
    @Bean
    RestClient getRestClient(){
        return RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
    }

    // Create the transport with a Jackson mapper
    @Bean
    ElasticsearchTransport t(RestClient restClient){
        return new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    }

    // And create the API client
    @Bean
    ElasticsearchClient getEsClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
