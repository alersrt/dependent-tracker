package io.github.alersrt.plugins.dependent;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
        /*------ Actions ------*/

        /*------ Asserts ------*/
    }
}
