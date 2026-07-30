#!/bin/bash
set -e

echo "🔨 Construindo imagens Docker das aplicações..."

# Construir Auth
docker build -t auth-app:latest -f Auth/Dockerfile .

# Construir User
docker build -t user-app:latest -f User/Dockerfile .

# Construir Book
docker build -t book-app:latest -f Book/Dockerfile .

echo "✅ Imagens construídas com sucesso!"
