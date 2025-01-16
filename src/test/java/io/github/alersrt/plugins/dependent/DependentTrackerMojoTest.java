package io.github.alersrt.plugins.dependent;

import io.github.alersrt.plugins.dependent.config.RootConfig;
import io.github.alersrt.plugins.dependent.domain.Dependent;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MojoExtension.class)
@Testcontainers
class DependentTrackerMojoTest {

    @Container
    private static final ComposeContainer ENVIRONMENT = new ComposeContainer(DockerComposeFinder.findCompose().toFile())
            .waitingFor("opensearch", Wait.forHealthcheck());

    @InjectMojo(
            goal = "io.github.alersrt:plugins.maven.dependent-tracker:dependent-tracker",
            pom = "file:src/test/resources/test-pom.xml"
    )
    @Test
    void execute() throws Exception {
        /*------ Arranges ------*/
        var opensearchClient = new RootConfig().openSearchClient("localhost:9200", "admin", "password", true);

        /*------ Actions ------*/
        var result = opensearchClient.search(s -> s.index("test-index"), Dependent.class);

        /*------ Asserts ------*/
        assertThat(result).isNotNull();
        assertThat(result.hits().total().value()).isEqualTo(1);
    }
}
