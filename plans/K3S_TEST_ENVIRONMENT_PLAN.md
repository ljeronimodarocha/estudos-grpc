# Plano de Ambiente de Testes com k3s via Docker

## Visão Geral

Este plano descreve a configuração de um ambiente de testes utilizando k3s em modo **Docker** (containerizado) para as aplicações Spring Boot existentes: **Auth**, **User**, **Book** e **contratos-grpc**.

**Vantagem:** Não requer instalação de pacotes no sistema, apenas Docker.

## Componentes da Aplicação

### 1. Auth (Porta 8082, gRPC 9090)
- Spring Boot gRPC Server
- Datasource: PostgreSQL (authentication_db)
- Redis cache (localhost:6379)
- JWT authentication service

### 2. User (Porta 8083, gRPC 9091)
- Spring Boot gRPC Server
- Datasource: PostgreSQL (user_db)
- Client gRPC para auth-service (localhost:9090)

### 3. Book (Porta 8081)
- Spring Boot REST
- Datasource: PostgreSQL (book-postgres)
- Client gRPC para auth-service (localhost:9090)

### 4. Databases (PostgreSQL 16)
- postgres1 (porta 5432) - authentication_db
- postgres2 (porta 5433) - book-postgres
- postgres3 (porta 5434) - user_db

### 5. Cache (Redis 7)
- auth-redis (porta 6379)

## Arquivos Necessários

### 1. `docker-compose.k3s.yml` - Orquestração do k3s via Docker
```yaml
version: '3.8'

services:
  k3s-server:
    image: rancher/k3s:latest
    container_name: k3s-server
    privileged: true
    volumes:
      - k3s_data:/var/lib/rancher/k3s
      - socket:/var/run/docker.sock
    environment:
      - K3S_TOKEN=k3s-test-token-12345
    ports:
      - "6443:6443"
      - "8082:8082"
      - "9090:9090"
      - "8083:8083"
      - "9091:9091"
      - "8081:8081"
    command: server --disable-traefik
    depends_on:
      - postgres-auth
      - postgres-book
      - postgres-user
      - redis
    restart: unless-stopped

  k3s-agent:
    image: rancher/k3s:latest
    container_name: k3s-agent
    privileged: true
    volumes:
      - k3s_agent_data:/var/lib/rancher/k3s
    environment:
      - K3S_URL=https://k3s-server:6443
      - K3S_TOKEN=k3s-test-token-12345
    ports:
      - "6443:6443"
    command: agent
    depends_on:
      - k3s-server
    restart: unless-stopped

  auth-service:
    image: auth-app:latest
    container_name: auth-app
    ports:
      - "8082:8082"
      - "9090:9090"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-auth:5432/authentication_db
      - SPRING_DATASOURCE_USERNAME=authuser
      - SPRING_DATASOURCE_PASSWORD=authpass
      - REDIS_HOST=redis
      - REDIS_PORT=6379
    depends_on:
      - postgres-auth
      - redis
    restart: unless-stopped

  user-service:
    image: user-app:latest
    container_name: user-app
    ports:
      - "8083:8083"
      - "9091:9091"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-user:5434/user_db
      - SPRING_DATASOURCE_USERNAME=authuser
      - SPRING_DATASOURCE_PASSWORD=authpass
      - AUTH_SERVICE_ADDRESS=http://auth-service:9090
    depends_on:
      - postgres-user
      - auth-service
    restart: unless-stopped

  book-service:
    image: book-app:latest
    container_name: book-app
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-book:5433/book-postgres
      - SPRING_DATASOURCE_USERNAME=authuser
      - SPRING_DATASOURCE_PASSWORD=authpass
      - AUTH_SERVICE_ADDRESS=http://auth-service:9090
    depends_on:
      - postgres-book
      - auth-service
    restart: unless-stopped

  postgres-auth:
    image: postgres:16
    container_name: postgres-auth
    environment:
      - POSTGRES_DB=authentication_db
      - POSTGRES_USER=authuser
      - POSTGRES_PASSWORD=authpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_auth_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U authuser -d authentication_db"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  postgres-book:
    image: postgres:16
    container_name: postgres-book
    environment:
      - POSTGRES_DB=book-postgres
      - POSTGRES_USER=authuser
      - POSTGRES_PASSWORD=authpass
    ports:
      - "5433:5432"
    volumes:
      - postgres_book_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U authuser -d book-postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  postgres-user:
    image: postgres:16
    container_name: postgres-user
    environment:
      - POSTGRES_DB=user_db
      - POSTGRES_USER=authuser
      - POSTGRES_PASSWORD=authpass
    ports:
      - "5434:5432"
    volumes:
      - postgres_user_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U authuser -d user_db"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  k3s_data:
  k3s_agent_data:
  postgres_auth_data:
  postgres_book_data:
  postgres_user_data:
  redis_data:
  socket:
    driver: local
```

### 2. `k8s/auth-deployment.yml` - Deployment do Auth
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth
  template:
    metadata:
      labels:
        app: auth
    spec:
      containers:
      - name: auth
        image: auth-app:latest
        ports:
        - containerPort: 8082
        - containerPort: 9090
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres-auth:5432/authentication_db"
        - name: REDIS_HOST
          value: "redis"
```

### 3. `k8s/user-deployment.yml` - Deployment do User
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: user
  template:
    metadata:
      labels:
        app: user
    spec:
      containers:
      - name: user
        image: user-app:latest
        ports:
        - containerPort: 8083
        - containerPort: 9091
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres-user:5434/user_db"
        - name: AUTH_SERVICE_ADDRESS
          value: "auth-grpc:9090"
```

### 4. `k8s/book-deployment.yml` - Deployment do Book
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: book-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: book
  template:
    metadata:
      labels:
        app: book
    spec:
      containers:
      - name: book
        image: book-app:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres-book:5433/book-postgres"
        - name: AUTH_SERVICE_ADDRESS
          value: "auth-grpc:9090"
```

### 5. `k8s/postgres-auth.yml` - Service Auth PostgreSQL
```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-auth
spec:
  selector:
    app: postgres-auth
  ports:
  - port: 5432
    targetPort: 5432
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-auth
spec:
  selector:
    matchLabels:
      app: postgres-auth
  template:
    metadata:
      labels:
        app: postgres-auth
    spec:
      containers:
      - name: postgres
        image: postgres:16
        env:
        - name: POSTGRES_DB
          value: "authentication_db"
        - name: POSTGRES_USER
          value: "authuser"
        - name: POSTGRES_PASSWORD
          value: "authpass"
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: data
        emptyDir: {}
```

### 6. `k8s/postgres-book.yml` - Service Book PostgreSQL
```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-book
spec:
  selector:
    app: postgres-book
  ports:
  - port: 5433
    targetPort: 5432
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-book
spec:
  selector:
    matchLabels:
      app: postgres-book
  template:
    metadata:
      labels:
        app: postgres-book
    spec:
      containers:
      - name: postgres
        image: postgres:16
        env:
        - name: POSTGRES_DB
          value: "book-postgres"
        - name: POSTGRES_USER
          value: "authuser"
        - name: POSTGRES_PASSWORD
          value: "authpass"
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: data
        emptyDir: {}
```

### 7. `k8s/postgres-user.yml` - Service User PostgreSQL
```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-user
spec:
  selector:
    app: postgres-user
  ports:
  - port: 5434
    targetPort: 5432
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-user
spec:
  selector:
    matchLabels:
      app: postgres-user
  template:
    metadata:
      labels:
        app: postgres-user
    spec:
      containers:
      - name: postgres
        image: postgres:16
        env:
        - name: POSTGRES_DB
          value: "user_db"
        - name: POSTGRES_USER
          value: "authuser"
        - name: POSTGRES_PASSWORD
          value: "authpass"
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: data
        emptyDir: {}
```

### 8. `k8s/redis.yml` - Service Redis
```yaml
apiVersion: v1
kind: Service
metadata:
  name: redis
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        ports:
        - containerPort: 6379
```

### 9. `k8s/ingress.yml` - Ingress Controller
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  annotations:
    kubernetes.io/ingress.class: "traefik"
spec:
  rules:
  - host: auth.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: auth-service
            port:
              number: 8082
  - host: user.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: user-service
            port:
              number: 8083
  - host: book.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: book-service
            port:
              number: 8081
```

### 10. `k8s/auth-service.yml` - Kubernetes Service Auth
```yaml
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth
  ports:
  - name: http
    port: 8082
    targetPort: 8082
  - name: grpc
    port: 9090
    targetPort: 9090
---
apiVersion: v1
kind: Service
metadata:
  name: auth-grpc
spec:
  selector:
    app: auth
  ports:
  - name: grpc
    port: 9090
    targetPort: 9090
```

### 11. `k8s/user-service.yml` - Kubernetes Service User
```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user
  ports:
  - name: http
    port: 8083
    targetPort: 8083
  - name: grpc
    port: 9091
    targetPort: 9091
```

## Script de Deploy `deploy-k3s.sh`
```bash
#!/bin/bash
set -e

echo "🚀 Iniciando deploy do k3s via Docker..."

# Construir imagens das aplicações (se necessário)
# docker build -t auth-app:latest -f Auth/Dockerfile Auth/
# docker build -t user-app:latest -f User/Dockerfile User/
# docker build -t book-app:latest -f Book/Dockerfile Book/

# Iniciar todos os serviços
docker compose -f docker-compose.k3s.yml up -d

# Aguardar serviços iniciarem
sleep 10

# Verificar status
docker ps
docker compose -f docker-compose.k3s.yml ps

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
```

## Script de Undeploy `undeploy-k3s.sh`
```bash
#!/bin/bash
set -e

echo "🛑 Removendo recursos do k3s via Docker..."

docker compose -f docker-compose.k3s.yml down

echo "✅ Undeploy concluído!"
```

## Script de Build das Aplicações `build-apps.sh`
```bash
#!/bin/bash
set -e

echo "🔨 Construindo imagens Docker das aplicações..."

# Construir Auth
docker build -t auth-app:latest -f Auth/Dockerfile Auth/

# Construir User
docker build -t user-app:latest -f User/Dockerfile User/

# Construir Book
docker build -t book-app:latest -f Book/Dockerfile Book/

echo "✅ Imagens construídas com sucesso!"
```

## Comandos de Verificação

```bash
# Verificar containers rodando
docker ps --format "table"

# Verificar logs do Auth
docker logs -f auth-app

# Verificar logs do User
docker logs -f user-app

# Verificar logs do Book
docker logs -f book-app

# Executar shell no container
docker exec -it auth-app bash

# Teste de integração
curl http://localhost:8082/api/auth/test
curl http://localhost:8083/api/users/test
curl http://localhost:8081/api/books/test
```

## Dependências do Ambiente

1. **Docker** - Container runtime
   ```bash
   sudo apt install docker.io
   ```

2. **Docker Compose** - Orquestração de containers
   ```bash
   sudo apt install docker-compose
   ```

**NÃO requer:** k3s system package, kubectl system package

## Fluxo de Trabalho

1. Instalar Docker e Docker Compose
2. Executar `build-apps.sh` para construir imagens das aplicações
3. Executar `deploy-k3s.sh` para iniciar o ambiente
4. Verificar containers e serviços
5. Executar testes de integração

## Notas Importantes

- O k3s roda em modo **Docker** (single binary containerizado), não requer instalação de pacotes no sistema
- Todos os serviços (Auth, User, Book, Postgres, Redis) são orquestrados via Docker Compose
- As imagens Docker das aplicações Spring Boot devem ser construídas com `mvn package`
- Os serviços gRPC (9090, 9091) devem ser expostos para comunicação inter-service
- O Redis é compartilhado entre Auth, User e Book para caching
- Cada módulo tem seu próprio datasource PostgreSQL
- O Auth valida tokens JWT e os outros módulos delegam a validação via gRPC
- Os dados são persistidos em volumes Docker para sobrevivência entre restarts
