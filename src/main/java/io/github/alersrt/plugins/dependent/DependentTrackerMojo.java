package io.github.alersrt.plugins.dependent;

import io.github.alersrt.plugins.dependent.core.TrackerFacade;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertySource;

import java.util.Optional;

import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_APP_NAMESPACE;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_ADDRESS;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_INDEX;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_PASSWORD;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_SKIP_SSL_VERIFICATION;
import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_USERNAME;


@Mojo(name = "dependent-tracker")
public class DependentTrackerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject project;

    @Parameter(property = "address", required = true, readonly = false)
    String address;

    @Parameter(property = "username", required = true, readonly = false)
    String username;

    @Parameter(property = "password", required = true, readonly = false)
    String password;

    @Parameter(property = "index", required = true, readonly = false)
    String index;

    @Parameter(property = "skipSslVerification", required = false, readonly = false)
    Boolean skipSslVerification = false;

    @Parameter(property = "namespace", required = true, readonly = false)
    String namespace;

    public void execute() throws MojoExecutionException {
        try (var ctx = new AnnotationConfigApplicationContext()) {
            ctx.scan("io.github.alersrt.plugins.dependent");
            ctx.getEnvironment().getPropertySources().addLast(new CustomPropertySource());
            ctx.refresh();
            TrackerFacade facade = ctx.getBean(TrackerFacade.class);
            facade.track(project.getModel());
        }
    }

    private class CustomPropertySource extends PropertySource<String> {

        public CustomPropertySource() {
            super("customPropertySource");
        }

        @Override
        public String getProperty(String name) {
            return switch (name) {
                case PROPERTY_OPENSEARCH_ADDRESS -> address;
                case PROPERTY_OPENSEARCH_USERNAME -> username;
                case PROPERTY_OPENSEARCH_PASSWORD -> password;
                case PROPERTY_OPENSEARCH_INDEX -> index;
                case PROPERTY_OPENSEARCH_SKIP_SSL_VERIFICATION -> Optional.ofNullable(skipSslVerification)
                        .map(Object::toString)
                        .orElse(null);
                case PROPERTY_APP_NAMESPACE -> namespace;
                default -> null;
            };
        }
    }
}
