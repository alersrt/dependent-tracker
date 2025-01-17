package io.github.alersrt.plugins.dependent.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependentRepository extends ElasticsearchRepository<Dependent, String> {
}
