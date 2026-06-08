# Mongo Base Persistence

## Purpose

This module provides a shared foundation for MongoDB documents with auditing support and automatic time-ordered ID generation.

## Components

- `BaseDocumentAudit`
- `TimeOrderedUuid`
- `MongoTimeOrderedUuidUtil`

## BaseDocumentAudit

`BaseDocumentAudit` encapsulates common audit fields:
- `version`: optimistic version control for documents
- `createdDate`: creation timestamp
- `modifiedDate`: last modification timestamp
- `createdBy`: user or process that created the document
- `modifiedBy`: user or process that performed the last modification

`BaseDocumentAudit` only provides the mapped fields and Spring Data auditing annotations. Each Mongo service must still enable auditing in its own Spring configuration.

Required baseline:
- `@EnableMongoAuditing`

Required for `createdBy` and `modifiedBy` population:
- an `AuditorAware<?>` bean that resolves the current user according to the service's authentication setup

Recommended approach:
- read the current user from the Spring Security context, or replace that part with the security abstraction already used by the service
- if some writes can happen without an authenticated user, optionally return a default value such as `"system"`

The bean name is arbitrary. It only needs to match `auditorAwareRef`.

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

Without that service-level configuration, Spring Data Mongo auditing will not populate the audit fields automatically.

## Audited document example

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

## TimeOrderedUuid

The `@TimeOrderedUuid` annotation marks `String` fields that should receive a time-ordered UUID right before they are persisted for the first time.

You do not need to call any generator manually. `MongoTimeOrderedUuidUtil` listens to the `BeforeConvertEvent` and fills the field when the value is empty.

## When to use it

- Mongo documents exposed through an API.
- Models where you want to avoid sequential IDs.
- Mongo services that need a consistent auditing foundation.
