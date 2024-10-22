package io.github.alersrt.plugins.dependent;

import io.github.alersrt.plugins.dependent.domain.Dependent;
import io.github.alersrt.plugins.dependent.mapper.DependencyMapper;
import io.github.alersrt.plugins.dependent.mapper.DependencyMapperImpl;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.transport.rest_client.RestClientTransport;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

@Mojo(name = "dependent-tracker")
public class DependentTrackerMojo extends AbstractMojo {

    private final DependencyMapper mapper = new DependencyMapperImpl();

    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject project;
    @Parameter(property = "address", required = true, readonly = false)
    String address;
    @Parameter(property = "username", required = true, readonly = false)
    String username;
    @Parameter(property = "password", required = true, readonly = false)
    String password;
    @Parameter(property = "password", required = true, readonly = false)
    String index;
    @Parameter(property = "skipSslVerification", required = false, readonly = false)
    Boolean skipSslVerification = false;

    public void execute() throws MojoExecutionException {
        final OpenSearchClient openSearchClient = buildOpenSearchClient(address, username, password, skipSslVerification);

        var dependent = mapper.toDependent(project.getModel());
        dependent.setDependencies(project.getModel().getDependencies().stream().map(mapper::toDependency).toList());

        var indexRequest = new IndexRequest.Builder<Dependent>()
                .index(index)
                .document(dependent)
                .build();

        try {
            openSearchClient.index(indexRequest);
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    private OpenSearchClient buildOpenSearchClient(String address,
                                                   String username,
                                                   String password,
                                                   Boolean skipSslVerification) {
        final HttpHost host = HttpHost.create(address);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(host),
                new UsernamePasswordCredentials(username, password)
        );
        final RestClient restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

                    if (skipSslVerification) {
                        SSLContext sslContext = null;
                        try {
                            sslContext = SSLContextBuilder
                                    .create()
                                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                                    .build();
                        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                            throw new RuntimeException(e);
                        }

                        httpAsyncClientBuilder.setSSLContext(sslContext);
                        httpAsyncClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    }

                    return httpAsyncClientBuilder;
                })
                .build();

        var transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new OpenSearchClient(transport);
    }
}
