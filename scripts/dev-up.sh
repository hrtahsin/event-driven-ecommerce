#!/usr/bin/env bash
set -euo pipefail

docker compose -f infra/docker-compose.yml up -d

echo "Infrastructure started."
echo "PostgreSQL: localhost:55432"
echo "Kafka: localhost:29092"
echo "Kafka UI: http://localhost:8088"
