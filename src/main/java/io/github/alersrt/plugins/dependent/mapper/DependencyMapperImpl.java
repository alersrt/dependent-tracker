package io.github.alersrt.plugins.dependent.mapper;

import org.apache.maven.model.Model;
import io.github.alersrt.plugins.dependent.domain.Dependency;
import io.github.alersrt.plugins.dependent.domain.Dependent;

public class DependencyMapperImpl implements DependencyMapper {
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
    public Dependent toDependent(Model model) {
        if (model == null) {
            return null;
        }

        Dependent dep = new Dependent();
        dep.setGroupId(model.getGroupId());
        dep.setArtifactId(model.getArtifactId());
        dep.setVersion(model.getVersion());

        return dep;
    }
}
