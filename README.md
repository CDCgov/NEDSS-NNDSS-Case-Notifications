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

### API Capabilities:

- Update configurations
- Check data status
- Investigate issues
- Reprocess records from DLT

👉 [Explore the APIs via Swagger](https://dataingestion.dts1.nbspreview.com/case-notification/swagger-ui/index.html#/)

### Database Management:

- Includes **Liquibase integration** for auto-managing schema changes.

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

---

### XML HL7 Parser (library)

| Variable              | Description                                                  |
|-----------------------|--------------------------------------------------------------|
| `NBS_DBSERVER`        | Database URL                                                 |
| `NBS_DBUSER`          | Database username                                            |
| `NBS_DBPASSWORD`      | Database password                                            |
| `CN_AUTH_URI`         | Keycloak host                                               |
| `CN_SERVER_HOST`      | Server host used for Swagger UI (default: `localhost:8093`) |

---

## Running with Docker

### Prerequisites
- The NNDSS services use the NBS SQL Server database and Keycloak instances that are part of [cdc-sandbox](https://github.com/CDCgov/NEDSS-Modernization/tree/main/cdc-sandbox). Ensure `cdc-sandbox` is running locally in Docker before proceeding.

### Setup

1. Copy `sample.env` to `.env` and fill in your values:

    ```bash
    cp sample.env .env
    ```

    Required variables in `.env`:

    | Variable | Description |
    |---|---|
    | `NBS_DBUSER` | Database username |
    | `NBS_DBPASSWORD` | Database password |
    | `CN_AUTH_URI` | Keycloak realm URI (e.g. `http://host.docker.internal:8100/realms/NBS`) |
    | `NND_DE_CLIENT_ID` | Client ID for HL7 Parser API |
    | `NND_DE_SECRET` | Client secret for HL7 Parser API |

2. Start all services:

    ```bash
    docker compose up --build
    ```

    This starts:
    - **Kafka** on port `9092`
    - **Kafka UI** on port `8080`
    - **Case Notification Service** on port `8093`
    - **Data Extraction Service** on port `8090`

3. To run in the background:

    ```bash
    docker compose up --build -d
    ```

### Notes

- Kafka is included in the compose stack — no external Kafka cluster is needed for local development.
- Kafka UI is available at `http://localhost:8080` for inspecting topics and consumer groups.

---
