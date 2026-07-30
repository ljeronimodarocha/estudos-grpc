#!/bin/bash
set -e

echo "🛑 Removendo recursos do k3d..."

# Parar k3d cluster
k3d cluster delete k3s-test --ignore-errors

# Limpar recursos Kubernetes
kubectl delete ingress app-ingress --ignore-not-found=true
kubectl delete deployment auth-service user-service book-service --ignore-not-found=true
kubectl delete service auth-service user-service book-service redis postgres-auth postgres-book postgres-user --ignore-not-found=true

echo "✅ Undeploy concluído!"
