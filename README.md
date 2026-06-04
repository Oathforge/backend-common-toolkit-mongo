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
  <version>1.0.0</version>
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

## Detailed documentation

- [Mongo base persistence](docs/persistence.md)
