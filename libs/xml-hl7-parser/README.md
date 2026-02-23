# XML-HL7 Parser

A Java library that transforms NND (National Notifiable Disease) Intermediary XML messages into HL7 messages. Used by the [Case Notification Service](../../case-notification-service) to convert case notification data for downstream processing.

---

## What It Does

1. Unmarshals NND XML (conforming to `NBSNNDIntermediaryMessage.xsd`) into Java objects via JAXB
2. Maps XML elements to HL7 segments (MSH, PID, NK1, OBR, OBX) using lookup tables
3. Builds an HL7 message using the [HAPI](https://hapifhir.github.io/hapi-hl7v2/) library
4. Validates the output against HL7 v2.5.1 standards

---

## Key Dependencies

| Dependency | Purpose |
|---|---|
| `ca.uhn.hapi:hapi-base:2.5.1` / `hapi-structures-v25` | HL7 v2.5.1 message construction and parsing |
| `jakarta.xml.bind` / `glassfish jaxb` | JAXB XML binding + XSD code generation |
| `spring-boot-starter-data-jpa` | Database access for lookup tables |
| Java 21 | Language toolchain |

JAXB classes are generated at compile time from `NBSNNDIntermediaryMessage.xsd` into `gov.cdc.xmlhl7parser.model.generated.jaxb`.

---

## Environment Variables

| Variable | Description |
|---|---|
| `NBS_DBSERVER` | SQL Server hostname |
| `NBS_DBUSER` | Database username |
| `NBS_DBPASSWORD` | Database password |
| `CN_AUTH_URI` | Keycloak issuer URI (e.g. `http://localhost:8100/realms/NBS`) |
| `CN_SERVER_HOST` | Server host for Swagger UI (default: `localhost:8088`) |

---

## Usage

This is a library module (`java-library` plugin). It is consumed as a project dependency:

```gradle
implementation project(':xml-hl7-parser')
```

The main entry point is `Hl7MessageBuilder`, a Spring `@Service` that accepts NND XML and returns an HL7 v2.5.1 message string.

---

## Database Access

Connects to two SQL Server databases:

- **NBS_ODSE** - Source data lookups
- **NBS_MSGOUTE** - Message output and service/action pair lookups
