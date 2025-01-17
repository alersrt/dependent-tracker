package io.github.alersrt.plugins.dependent.mapper;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import io.github.alersrt.plugins.dependent.domain.Dependent;


public interface DependencyMapper {

    io.github.alersrt.plugins.dependent.domain.Dependency toDependency(Dependency dependency);

    Dependent toDependent(Model model, String namespace);
}
