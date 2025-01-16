package io.github.alersrt.plugins.dependent;

import io.github.alersrt.plugins.dependent.config.RootConfig;
import io.github.alersrt.plugins.dependent.domain.Dependent;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@MojoTest
@Testcontainers
class DependentTrackerMojoTest {

    @Container
    private static final ComposeContainer ENVIRONMENT = new ComposeContainer(DockerComposeFinder.findCompose().toFile())
            .waitingFor("opensearch", Wait.forHealthcheck());

    @InjectMojo(goal = "dependent-tracker")
    @MojoParameter(name = "address", value = "localhost:9200")
    @MojoParameter(name = "username", value = "admin")
    @MojoParameter(name = "password", value = "password")
    @MojoParameter(name = "index", value = "test-index")
    @MojoParameter(name = "skipSslVerification", value = "true")
    @MojoParameter(name = "namespace", value = "test")
    @Test
    void execute(DependentTrackerMojo mojo) throws Exception {
        assertThat(mojo).isNotNull();

        /*------ Arranges ------*/
        var opensearchClient = new RootConfig().openSearchClient("localhost:9200", "admin", "password", true);

        /*------ Actions ------*/
        mojo.execute();
        var result = opensearchClient.search(s -> s.index("test-index"), Dependent.class);

        /*------ Asserts ------*/
        assertThat(result).isNotNull();
        assertThat(result.hits().hits().size()).isEqualTo(1);
    }
}
