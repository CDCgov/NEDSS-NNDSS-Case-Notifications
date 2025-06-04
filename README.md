# CDC Case Notification System - Technical Documentation

This document outlines how the CDC Case Notification system operates end-to-end.

---

## Input

The pipeline begins with a **Debezium source connector** configured on the `ODSE..CNTransportQOut` table.

- Any new data inserted into this table is detected by the connector.
- The connector publishes the data to a **Kafka topic**.
- This topic is consumed by the **Data Extraction** service.

The **Case Notification** process depends on predefined configurations stored in the `MSOUTE..Case_Notification_Config` table.

- This table contains all essential settings required for the pipeline to operate correctly.
- It includes **PHINMS properties** that are critical for downstream processing.

---

## Output

- Processed data is stored in either:
    - `MSGOUTE..TransportQOut`
    - `MSGOUTE..NetssTransportQOut`

- Faulty data is routed to:
    - `MSGOUTE..case_notification_dlt` (Dead Letter Table)
    - For further investigation by analysts.

---

## Service Requirements

To function correctly, **all three services** must be available:

- **Data Extraction**
- **Case Notification**
- **HL7 Parser**

---

## Data Extraction

- Continuously extracts and categorizes data.
- Sends data downstream using Debezium and Kafka.

### Requirements:

- Source connector must be set up **before launching** the service.
- Source connector on `CNTransportQOut` is maintained by the **RTR Data Team**.
- Must use the **same Kafka cluster** as RTR.

---

## Case Notification

### Components:

- Microservice and APIs

### Responsibilities:

- Listens for events from the Data Extraction module.
- Processes valid event types:
    - `MMG (NON_STD)`
    - `STD`
- Valid events are sent to:
    - `TransportQOut`
    - `NetssTransportQOut`
- Invalid events are ignored.
- Faulty MMG or STD events are pushed to:
    - `Case_Notification_DLT`

### Dependencies:

- Requires a **constant connection to the HL7 server** to transform NND XML into HL7.

### API Capabilities:

- Update configurations
- Check data status
- Investigate issues
- Reprocess records from DLT

👉 [Explore the APIs via Swagger](https://dataingestion.dts1.nbspreview.com/case-notification/swagger-ui/index.html#/)

### Database Management:

- Includes **Liquibase integration** for auto-managing schema changes.

---

## HL7 Service

- Helper service used by Case Notification.
- Transforms **NND XML** into **HL7 2.5.1** format.
- Validates output against HL7 standards.
- Provides API endpoints for testing transformations.

👉 [Explore HL7 API via Swagger](https://dataingestion.dts1.nbspreview.com/hl7-parser/swagger-ui/index.html#/)

---

## 🛠 Liquibase Info

- [Liquibase DB Scripts](https://github.com/CDCgov/NEDSS-NNDSS-Case-Notifications/tree/main/case-notification-service/src/main/resources/db)

---

## 🌐 Project Repository

- [CDC Case Notification GitHub Repo](https://github.com/CDCgov/NEDSS-NNDSS-Case-Notifications/tree/main)

---

## ⚙️ Required Environment Variables

### Data Extraction

| Variable              | Description              |
|-----------------------|--------------------------|
| `NBS_DBSERVER`        | Database URL             |
| `NBS_DBUSER`          | Database username        |
| `NBS_DBPASSWORD`      | Database password        |
| `KAFKA_BOOTSTRAP_SERVER` | Kafka URL              |

---

### Case Notification

| Variable              | Description                                                      |
|-----------------------|------------------------------------------------------------------|
| `NBS_DBSERVER`        | Database URL                                                     |
| `NBS_DBUSER`          | Database username                                                |
| `NBS_DBPASSWORD`      | Database password                                                |
| `KAFKA_BOOTSTRAP_SERVER` | Kafka URL                                                    |
| `SERVICE_TZ`          | Timezone (default: `UTC`)                                       |
| `CN_AUTH_URI`         | Keycloak host                                                   |
| `CN_SERVER_HOST`      | Server host used for Swagger UI (default: `localhost:8093`)     |
| `NND_DE_CLIENT_ID`    | Client ID for HL7 Parser API                                    |
| `NND_DE_SECRET`       | Client secret for HL7 Parser API                                |
| `NND_DE_URL`          | URL for HL7 Parser API (e.g., `https://dataingestion.dts1.nbspreview.com/hl7-parser`) |

---

### HL7 Parser

| Variable              | Description                                                  |
|-----------------------|--------------------------------------------------------------|
| `NBS_DBSERVER`        | Database URL                                                 |
| `NBS_DBUSER`          | Database username                                            |
| `NBS_DBPASSWORD`      | Database password                                            |
| `CN_AUTH_URI`         | Keycloak host                                               |
| `CN_SERVER_HOST`      | Server host used for Swagger UI (default: `localhost:8093`) |

---
