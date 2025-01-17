package io.github.alersrt.plugins.dependent;

import io.github.alersrt.plugins.dependent.config.RootConfig;
import io.github.alersrt.plugins.dependent.domain.Dependent;
import org.apache.maven.api.plugin.Mojo;
import org.apache.maven.api.plugin.testing.InjectMojo;
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

    @InjectMojo(goal = "dependent-tracker", pom = "file:src/test/resources/test-pom.xml")
    @Test
    void execute(DependentTrackerMojo mojo) throws Exception {
        assertThat(mojo).isNotNull();

        /*------ Arranges ------*/
        var opensearchClient = new RootConfig().openSearchClient("localhost:9200", "admin", "OpenSearch#0", true);

        /*------ Actions ------*/
        mojo.execute();
        var result = opensearchClient.search(s -> s.index("test-index"), Dependent.class);

        /*------ Asserts ------*/
        assertThat(result).isNotNull();
        assertThat(result.hits().hits().size()).isEqualTo(1);
    }
}
