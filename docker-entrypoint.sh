#!/bin/sh

echo "Pulling Docker images for code execution..."

# Подтягиваем образы для выполнения кода
# Используем || true чтобы не прерывать запуск приложения, если образ уже есть или недоступен
docker pull python:3.11-alpine || echo "Warning: Failed to pull python:3.11-alpine (may already exist)"
docker pull eclipse-temurin:21-jdk-alpine || echo "Warning: Failed to pull eclipse-temurin:21-jdk-alpine (may already exist)"
docker pull gcc:13.2.0 || echo "Warning: Failed to pull gcc:13.2.0 (may already exist)"
docker pull node:22-alpine || echo "Warning: Failed to pull node:22-alpine (may already exist)"

echo "Docker images pull completed"

# Запускаем основное приложение
exec "$@"

