# backend-common-toolkit-mongo

`backend-common-toolkit-mongo` is the module intended for projects that use MongoDB and want to reuse a shared foundation for auditing and time-ordered identifier generation.

## What it includes

- `BaseDocumentAudit`
- `TimeOrderedUuidDocument`
- `MongoTimeOrderedUuidUtil`

## When to use it

Use this module if your service works with Spring Data MongoDB. If your project uses JPA/Hibernate, use `backend-common-toolkit-jpa`. If you do not need persistence support, you do not need this module.

## Maven dependency

```xml
<dependency>
  <groupId>io.backendtoolkit</groupId>
  <artifactId>backend-common-toolkit-mongo</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Quick example

```java
@Document(collection = "users")
public class UserDocument extends BaseDocumentAudit {

  @Id
  @TimeOrderedUuidDocument
  private String id;

  private String email;
}
```

## Detailed documentation

- [Mongo base persistence](docs/persistence.md)
