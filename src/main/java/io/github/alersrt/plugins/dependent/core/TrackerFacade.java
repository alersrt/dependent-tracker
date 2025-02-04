package io.github.alersrt.plugins.dependent.core;

import io.github.alersrt.plugins.dependent.domain.Dependent;
import io.github.alersrt.plugins.dependent.domain.DependentRepository;
import io.github.alersrt.plugins.dependent.mapper.DependencyMapper;
import lombok.RequiredArgsConstructor;
import org.apache.maven.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_APP_NAMESPACE;


@Component
@RequiredArgsConstructor
public class TrackerFacade {

    private final DependencyMapper mapper;
    private final DependentRepository repository;

    @Value("${" + PROPERTY_APP_NAMESPACE + "}")
    private String namespace;

    public void track(Collection<Model> models) {
        List<String> ids = models
                .stream()
                .map(model -> Dependent.naturalId(model.getGroupId(), model.getArtifactId(), model.getVersion(), namespace))
                .toList();

        Map<String, Dependent> entities = new HashMap<>();
        for (var entity : repository.findAllById(ids)) {
            entity.setBuiltAt(Instant.now());
            entities.put(entity.getNaturalId(), entity);
        }

        for (var model : models) {
            final String id = Dependent.naturalId(model.getGroupId(), model.getArtifactId(), model.getVersion(), namespace);
            if (!entities.containsKey(id)) {
                entities.put(id, mapper.toDependent(model, namespace));
            }
        }

        repository.saveAll(entities.values());
    }

    public void track(Model model) {

        String id = Dependent.naturalId(model.getGroupId(), model.getArtifactId(), model.getVersion(), namespace);

        var entity = repository.findById(id)
                .map(dependent -> {
                    dependent.setBuiltAt(Instant.now());
                    return dependent;
                })
                .orElse(mapper.toDependent(model, namespace));

        repository.save(entity);
    }
}
