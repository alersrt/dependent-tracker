package io.github.alersrt.plugins.dependent.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;
}
