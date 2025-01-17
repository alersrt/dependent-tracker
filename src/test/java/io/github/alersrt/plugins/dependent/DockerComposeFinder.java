package io.github.alersrt.plugins.dependent;

import java.nio.file.Files;
import java.nio.file.Path;

public class DockerComposeFinder {

    public static Path findCompose() {
        var partial = Path.of("docker", "docker-compose.yaml");
        var searched = partial.toAbsolutePath();
        if (!Files.exists(searched)) {
            for (int i = 0; i <= partial.getNameCount(); i++) {
                if (searched.getParent() == null) {
                    break;
                }
                searched = searched.getParent();
            }
            searched = searched.resolve(partial);
        }
        return searched;
    }
}
