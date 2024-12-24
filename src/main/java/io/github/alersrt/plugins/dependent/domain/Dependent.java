package io.github.alersrt.plugins.dependent.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

import static io.github.alersrt.plugins.dependent.utils.CommonConstants.PROPERTY_OPENSEARCH_INDEX;

@Data
@Document(indexName = PROPERTY_OPENSEARCH_INDEX)
public class Dependent {

    @Field
    private String groupId;

    @Field
    private String artifactId;

    @Field
    private String version;

    @Field
    private String namespace;

    @Field(type = FieldType.Date_Nanos)
    private Instant createdAt;

    @Field(type = FieldType.Date_Nanos)
    private Instant updatedAt;

    @Field(type = FieldType.Object)
    private List<Dependency> dependencies;

    public static String naturalId(String groupId, String artifactId, String version, String namespace) {
        return String.join(":", groupId, artifactId, version, namespace);
    }

    @Id
    public String getNaturalId() {
        return naturalId(this.groupId, this.artifactId, this.version, this.namespace);
    }
}
