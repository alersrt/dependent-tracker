package io.github.alersrt.plugins.dependent.core;

import io.github.alersrt.plugins.dependent.domain.Dependent;
import io.github.alersrt.plugins.dependent.domain.DependentRepository;
import io.github.alersrt.plugins.dependent.mapper.DependencyMapper;
import lombok.RequiredArgsConstructor;
import org.apache.maven.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_APP_NAMESPACE;


@Component
@RequiredArgsConstructor
public class TrackerFacade {

    private final DependencyMapper mapper;
    private final DependentRepository repository;

    @Value("${" + PROPERTY_APP_NAMESPACE + "}")
    private String namespace;

    public void track(Model model) {

        String id = Dependent.naturalId(model.getGroupId(), model.getArtifactId(), model.getVersion(), namespace);

        var entity = repository.findById(id)
                .map(dependent -> {
                    dependent.setBuitAt(Instant.now());
                    return dependent;
                })
                .orElse(mapper.toDependent(model, namespace));

        repository.save(entity);
    }
}
