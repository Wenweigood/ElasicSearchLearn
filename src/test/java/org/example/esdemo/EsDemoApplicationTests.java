package org.example.esdemo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.Serializable;

@SpringBootTest()
@Slf4j
class EsDemoApplicationTests {
    @Resource
    private ElasticsearchClient esClient;

    @Test
    void creatingIndexTest() throws IOException {
        try {
            CreateIndexResponse products = esClient.indices().create(c -> c.index("products"));
            System.out.println(products);
        } catch (ElasticsearchException esException){
            System.out.println(esException.getMessage());
        }
    }

    @Test
    void creatingDocumentIndexTest() throws IOException {
        Product product = new Product("bk-1", "City bike", 123.0);
        try {
            IndexResponse response = esClient.index(i -> i
                    .index("products")
                    .id(product.getSku())
                    .document(product)
            );
            System.out.println("Indexed with version " + response.version());
        } catch (ElasticsearchException esException){
            System.out.println(esException.getMessage());
        }
    }

    @Test
    void getDocumentTest() throws IOException {
        GetResponse<Product> response = esClient.get(g -> g
                        .index("products")
                        .id("bk-1"),
                Product.class
        );

        if (response.found()) {
            Product product = response.source();
            System.out.println("Product type: " + product.getType());
        } else {
            System.out.println("Product not found");
        }
    }

    @Test
    void searchTest() throws IOException {
        String searchText = "bike";

        SearchResponse<Product> response = esClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .match(t -> t
                                        .field("type")
                                        .query(searchText)
                                )
                        ),
                Product.class
        );
        System.out.println("search result with specified field: " + response);

        response = esClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .multiMatch(t -> t
                                        .query(searchText)
                                )
                        ),
                Product.class
        );
        System.out.println("search result with all fields: " + response);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Product implements Serializable {
        private String sku;

        private String type;

        private double price;
    }
}