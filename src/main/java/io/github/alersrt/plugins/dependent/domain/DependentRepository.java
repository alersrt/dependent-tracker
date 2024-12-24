package io.github.alersrt.plugins.dependent.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependentRepository extends CrudRepository<Dependent, String> {
}
