#!/bin/bash
set -e

echo "🚀 Iniciando deploy do k3s via k3d..."

# Construir imagens das aplicações
echo "🔨 Construindo imagens Docker..."
./infra/build-apps.sh

# Verificar se cluster já existe
if k3d cluster list 2>/dev/null | grep -q "k3s-test"; then
  echo "⚠️ Cluster 'k3s-test' já existe, pulando criação"
else
  echo "⚙️ Criando cluster k3d..."
  k3d cluster create k3s-test --servers 1 --wait
fi

# Aguardar k3d iniciar
echo "⏳ Aguardando k3d iniciar..."
sleep 5

# Carregar imagens no cluster k3d
echo "📦 Carregando imagens no cluster..."
k3d image load auth-app:latest user-app:latest book-app:latest --cluster k3s-test

# Aplicar manifests Kubernetes
echo "📋 Aplicando manifests Kubernetes..."
kubectl apply -f ./infra/k8s/

# Verificar status
echo "✅ Verificando status dos pods..."
kubectl get pods
kubectl get services

echo "✅ Deploy concluído!"
echo ""
echo "Acesse:"
echo "  Auth:    http://localhost:8082"
echo "  User:    http://localhost:8083"
echo "  Book:    http://localhost:8081"
echo "  gRPC Auth:    localhost:9090"
echo "  gRPC User:    localhost:9091"
echo "  Redis:   localhost:6379"
echo "  Postgres Auth: localhost:5432"
echo "  Postgres Book: localhost:5433"
echo "  Postgres User: localhost:5434"