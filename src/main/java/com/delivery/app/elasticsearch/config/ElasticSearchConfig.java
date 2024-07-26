package com.delivery.app.elasticsearch.config;

import co.elastic.clients.transport.TransportUtils;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;


@Configuration
public class ElasticSearchConfig {

    public static final String ARTICLES = "articles";
    public static final String USERS = "users";
    public static final String COMMENTS = "comments";

    @Value("${elasticsearch.url}")
    private String serverUrl;

    @Value("${elasticsearch.apiKey}")
    private String apiKey;

    /**
     * Creates the ElasticsearchClient and the indexes needed
     *
     * @return a configured ElasticsearchClient
     */
    @Bean
    public ElasticsearchClient elasticRestClient() throws IOException {

        String fingerprint = "03:F8:3E:52:63:46:E5:67:C7:54:CE:4B:3E:E2:84:84:AE:51:5F:07:80:14:D5:42:0B:2B:B6:03:81:0F:2E:D0";

        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(fingerprint);

        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setHttpClientConfigCallback(hc -> hc.setSSLContext(sslContext))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        // Create the transport with the Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(mapper));

        // Create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        // Creating the indexes
        //createSimpleIndex(esClient, USERS);
        //createIndexWithDateMapping(esClient, ARTICLES);
        //createIndexWithDateMapping(esClient, COMMENTS);

        return esClient;

    }

    /**
     * Plain simple
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html">index</a>
     * creation with an
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html">
     * exists</a> check
     */
    private void createSimpleIndex(ElasticsearchClient esClient, String index) throws IOException {
        BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
        if (!indexRes.value()) {
            esClient.indices().create(c -> c
                    .index(index));
        }
    }

    /**
     * If no explicit mapping is defined, elasticsearch will dynamically map types when converting data to
     * the json
     * format. Adding explicit mapping to the date fields assures that no precision will be lost. More
     * information about
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-field-mapping.html">dynamic
     * field mapping</a>, more on <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format
     * .html">mapping date
     * format </a>
     */
    private void createIndexWithDateMapping(ElasticsearchClient esClient, String index) throws IOException {
        BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
        if (!indexRes.value()) {
            esClient.indices().create(c -> c
                    .index(index)
                    .mappings(m -> m
                            .properties("createdAt", p -> p
                                    .date(d -> d.format("strict_date_optional_time")))
                            .properties("updatedAt", p -> p
                                    .date(d -> d.format("strict_date_optional_time")))));

        }
    }

}
