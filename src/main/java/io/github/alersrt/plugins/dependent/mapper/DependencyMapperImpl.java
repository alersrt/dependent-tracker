package io.github.alersrt.plugins.dependent.mapper;

import io.github.alersrt.plugins.dependent.domain.Dependency;
import io.github.alersrt.plugins.dependent.domain.Dependent;
import org.apache.maven.model.Model;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DependencyMapperImpl implements DependencyMapper {

    private String namespace;

    @Override
    public Dependency toDependency(org.apache.maven.model.Dependency dependency) {
        if (dependency == null) {
            return null;
        }

        Dependency dep = new Dependency();
        dep.setGroupId(dependency.getGroupId());
        dep.setArtifactId(dependency.getArtifactId());
        dep.setVersion(dependency.getVersion());

        return dep;
    }

    @Override
    public Dependent toDependent(Model model, String namespace) {
        var now = Instant.now();

        if (model == null) {
            return null;
        }

        Dependent dep = new Dependent();
        dep.setGroupId(model.getGroupId());
        dep.setArtifactId(model.getArtifactId());
        dep.setVersion(model.getVersion());
        dep.setCreatedAt(now);
        dep.setUpdatedAt(now);
        dep.setNamespace(namespace);
        dep.setDependencies(model.getDependencies().stream().map(this::toDependency).toList());

        return dep;
    }
}
