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
@Document(indexName = "#{@environment.getProperty('" + PROPERTY_OPENSEARCH_INDEX + "')}")
public class Dependent {

    @Id
    private String naturalId;

    @Field
    private String groupId;

    @Field
    private String artifactId;

    @Field
    private String version;

    @Field
    private String namespace;

    @Field(type = FieldType.Date_Nanos)
    private Instant buitAt;

    @Field(type = FieldType.Object)
    private List<Dependency> dependencies;

    public static String naturalId(String groupId, String artifactId, String version, String namespace) {
        return String.join(":", groupId, artifactId, version, namespace);
    }
}
