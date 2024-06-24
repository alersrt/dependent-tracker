package org.student.plugins.dependent.mapper;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.student.plugins.dependent.domain.Dependent;

public interface DependencyMapper {

    org.student.plugins.dependent.domain.Dependency toDependency(Dependency dependency);

    Dependent toDependent(Model model);
}
