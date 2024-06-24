package org.student.plugins.dependent;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.student.plugins.dependent.domain.Dependent;
import org.student.plugins.dependent.mapper.DependencyMapper;
import org.student.plugins.dependent.mapper.DependencyMapperImpl;

import java.io.IOException;

@Mojo(name = "dependent-tracker", defaultPhase = LifecyclePhase.COMPILE)
public class DependentTrackerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "address", required = true, readonly = true)
    String address;

    @Parameter(property = "username", required = true, readonly = true)
    String username;

    @Parameter(property = "password", required = true, readonly = true)
    String password;

    @Parameter(property = "password", required = true, readonly = true)
    String index;

    private final DependencyMapper mapper = new DependencyMapperImpl();

    public void execute() throws MojoExecutionException {
        final OpenSearchClient openSearchClient = buildOpenSearchClient(address, username, password);

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

    private OpenSearchClient buildOpenSearchClient(String address, String username, String password) {
        final HttpHost host = HttpHost.create(address);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(host),
                new UsernamePasswordCredentials(username, password)
        );
        final RestClient restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        var transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new OpenSearchClient(transport);
    }
}
