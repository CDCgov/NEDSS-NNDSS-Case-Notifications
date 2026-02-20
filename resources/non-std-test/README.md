# Non-Standard Performance Test Scripts

Scripts for manually testing the non-standard case notification pipeline. They seed test data into `CN_transportq_out`, push Debezium-style messages to Kafka, and report throughput metrics.

## Usage

Run in order:

### 1. `seed-test-data.sql`

Inserts 100 test rows into `NBS_ODSE.CN_transportq_out` with a sample Hepatitis B XML payload. Auto-selects starting UIDs to avoid collisions. Prints the UID range needed for the next step.

```
sqlcmd -S <server> -U <user> -P <password> -d NBS_ODSE -i seed-test-data.sql
```

### 2. `push-kafka-messages.sh`

Pushes Debezium-style CDC messages to the `nbs_CN_transportq_out` Kafka topic via `docker compose`. Use the UID range printed by step 1.

```bash
./push-kafka-messages.sh <start_uid> <end_uid>
# Example: ./push-kafka-messages.sh 1050 1149
```

### 3. `perf-metrics.sql`

Reports processing results: status breakdown, throughput (messages/second), and DLT error summary.

```
sqlcmd -S <server> -U <user> -P <password> -i perf-metrics.sql
```

### 4. `reset-test-data.sql`

Deletes all `NOT-PERF-*` test rows from `CN_transportq_out` and `TransportQ_out` for re-runs.

```
sqlcmd -S <server> -U <user> -P <password> -i reset-test-data.sql
```
