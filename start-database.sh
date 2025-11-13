#!/bin/sh

export YLAB_PROJECT_POSTGRES_DB="first_project_database"
export YLAB_PROJECT_POSTGRES_USER="first_project_user"
export YLAB_PROJECT_POSTGRES_PASSWORD="first_project_password"

echo "starting database"
docker compose -f ./docker-compose.yaml up -d
