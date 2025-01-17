package io.github.alersrt.plugins.dependent.config;

import io.github.alersrt.plugins.dependent.domain.DependentRepository;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.data.client.osc.OpenSearchTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_ADDRESS;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_PASSWORD;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_SKIP_SSL_VERIFICATION;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_USERNAME;

@EnableElasticsearchRepositories(basePackageClasses = DependentRepository.class)
@Configuration
public class RootConfig {

    @Bean
    public OpenSearchClient openSearchClient(
            @Value("${" + PROPERTY_OPENSEARCH_ADDRESS + "}") String address,
            @Value("${" + PROPERTY_OPENSEARCH_USERNAME + "}") String username,
            @Value("${" + PROPERTY_OPENSEARCH_PASSWORD + "}") String password,
            @Value("${" + PROPERTY_OPENSEARCH_SKIP_SSL_VERIFICATION + "}") Boolean skipSslVerification
    ) {
        final HttpHost host = HttpHost.create(address);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        final RestClient restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    if (skipSslVerification) {
                        final SSLContextBuilder sslContext = new SSLContextBuilder();
                        try {
                            sslContext.loadTrustMaterial(null, new TrustAllStrategy());
                            httpAsyncClientBuilder.setSSLContext(sslContext.build());
                            httpAsyncClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                            throw new RuntimeException("Can't disable SSL verification", e);
                        }
                    }
                    return httpAsyncClientBuilder;
                })
                .build();

        var transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }

    @Bean(name = "elasticsearchTemplate")
    public OpenSearchTemplate openSearchTemplate(OpenSearchClient openSearchClient) {
        return new OpenSearchTemplate(openSearchClient);
    }
}
