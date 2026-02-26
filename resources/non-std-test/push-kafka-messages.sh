#!/bin/bash
# ============================================================
# Performance Test: Push Debezium-style messages to Kafka
# Usage: ./push-kafka-messages.sh <start_uid> <end_uid>
#
# Use the UID range printed by seed-test-data.sql
# Example: ./push-kafka-messages.sh 1050 1149
# ============================================================

set -e

START_UID=${1:?Usage: $0 <start_uid> <end_uid>}
END_UID=${2:?Usage: $0 <start_uid> <end_uid>}
TOPIC="nbs_CN_transportq_out"
COUNT=$(( END_UID - START_UID + 1 ))

echo "Pushing $COUNT messages to $TOPIC (UIDs $START_UID to $END_UID)"
echo "Start time: $(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)"

for RECORD_UID in $(seq "$START_UID" "$END_UID"); do
  INDEX=$(( RECORD_UID - START_UID ))
  echo "{\"payload\":{\"before\":null,\"after\":{\"cn_transportq_out_uid\":${RECORD_UID},\"add_reason_cd\":null,\"add_time\":null,\"add_user_id\":null,\"last_chg_reason_cd\":null,\"last_chg_time\":null,\"last_chg_user_id\":null,\"message_payload\":\"placeholder\",\"notification_uid\":$((900000 + INDEX)),\"notification_local_id\":\"NOT-PERF-${INDEX}\",\"public_health_case_local_id\":\"CAS-PERF-${INDEX}\",\"report_status_cd\":\"A\",\"record_status_cd\":\"UNPROCESSED\",\"record_status_time\":null,\"version_ctrl_nbr\":1},\"op\":\"c\",\"ts_ms\":$(date +%s000)}}"
done | docker compose exec -T kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server kafka:9092 \
  --topic "$TOPIC"

echo "End time: $(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)"
echo "Done. Pushed $COUNT messages."