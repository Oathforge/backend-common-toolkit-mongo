# backend-common-toolkit-mongo

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)
![Maven Central](https://img.shields.io/maven-central/v/io.github.oathforge/backend-common-toolkit-mongo)
![Release](https://img.shields.io/github/v/release/Oathforge/backend-common-toolkit-mongo)

`backend-common-toolkit-mongo` is the module intended for projects that use MongoDB and want to reuse a shared foundation for auditing and time-ordered identifier generation.

## What it includes

- `BaseDocumentAudit`
- `TimeOrderedUuid`
- `MongoTimeOrderedUuidUtil`

## When to use it

Use this module if your service works with Spring Data MongoDB. If your project uses JPA/Hibernate, use `backend-common-toolkit-jpa`. If you do not need persistence support, you do not need this module.

## Maven dependency

```xml
<dependency>
  <groupId>io.github.oathforge</groupId>
  <artifactId>backend-common-toolkit-mongo</artifactId>
  <version>1.0.1</version>
</dependency>
```

Do not declare `backend-common-toolkit` separately when you use this module.

`backend-common-toolkit-mongo` already includes `backend-common-toolkit` transitively, so importing both dependencies is unnecessary.

If your service needs Mongo support, declare only `backend-common-toolkit-mongo`.

## Quick example

`@Id` is still required. `@TimeOrderedUuid` only marks the field for automatic ID assignment before persistence.

```java
@Document(collection = "users")
public class UserDocument extends BaseDocumentAudit {

  @Id
  @TimeOrderedUuid
  private String id;

  private String email;
}
```

## Auditing activation

`BaseDocumentAudit` defines the audit fields and Spring Data annotations, but each Mongo service must still enable Spring Data Mongo auditing in its own application context.

At minimum, the service must declare `@EnableMongoAuditing`.

If the service wants Spring to populate `createdBy` and `modifiedBy`, it must also provide an `AuditorAware<?>` bean that resolves the current user from the authentication mechanism used by that service.

Typical approach:

- read the current user from the Spring Security context, or replace that part with the service's own user-resolution mechanism
- if the operation can run without an authenticated user, optionally return a default value such as `"system"`

The bean name is not fixed. It only has to match the value used in `auditorAwareRef`.

The example below uses `SecurityContextHolder` because it is the most common Spring setup, but that part can be replaced with any project-specific way of obtaining the current user.

Example:

```java
@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class MongoAuditingConfiguration {

  @Bean
  AuditorAware<String> auditorProvider() {
    return () -> {
      try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
          return Optional.of("system");
        }
        return Optional.ofNullable(authentication.getName()).or(() -> Optional.of("system"));
      } catch (Exception e) {
        return Optional.of("system");
      }
    };
  }
}
```

Without this configuration, extending `BaseDocumentAudit` is not enough to make Mongo auditing run.

## Detailed documentation

- [Mongo base persistence](docs/persistence.md)
