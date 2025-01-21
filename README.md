# DEPENDENT-TRACKER

Dependents tracker as a Maven plugin.

## Common description

Takes dependency list and send it to OpenSearch using the project's definition as a key.

## Usage

```xml
<plugin>
    <groupId>io.github.alersrt</groupId>
    <artifactId>plugins.maven.dependent-tracker</artifactId>
    <version>1.0.2</version>
    <executions>
        <execution>
            <goals>
                <goal>dependent-tracker</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <address>opensearch:9200</address>
        <username>username</username>
        <password>password</password>
        <index>dependencies</index>
        <namespace>test</namespace>
        <skipSslVerification>true</skipSslVerification>
    </configuration>
</plugin>
```
