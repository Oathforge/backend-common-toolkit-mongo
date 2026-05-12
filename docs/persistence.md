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

## Audited document example

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
