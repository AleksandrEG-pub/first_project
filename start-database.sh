#!/bin/sh

echo "starting database"
docker compose -f ./docker-compose.yaml up -d
