# Sistema gRPC - Authentication & Books

Multi-module Spring Boot application com gRPC para autenticação e gestão de livros.

## 🛡️ Tratamento Centralizado de Erros

O projeto implementa `GlobalExceptionHandler` com `@RestControllerAdvice` para tratamento consistente de exceções em todos os controllers:

- `BadCredentialsException` → 401 "Invalid username or password"
- `AuthenticationException` → 401 com mensagem da exceção
- `AccessDeniedException` → 403 "Access denied"
- `ExpiredJwtException` → 401 "Token has expired"
- `RuntimeException` → 500 com mensagem da exceção
- `Exception` → 500 "An unexpected error occurred" (fallback genérico)

### Estrutura de Respostas

Todas as respostas de erro seguem o padrão:
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "timestamp": "2026-07-24T23:35:25.285Z"
}
```

## 🤖 Desenvolvido com IA

Este projeto foi desenvolvido e mantido pelo **opencode**, um agente de código baseado em **llama.cpp**, um modelo de IA local otimizado para execução eficiente em hardware local.

**Tecnologias de IA Utilizadas:**
- **llama.cpp** - Implementação de modelos LLM otimizada para CPU/GPU
- **opencode** - Agente de desenvolvimento integrado ao llama.cpp
- **Modelo**: Qwen3.5-9B-Q5_K_M

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    Multi-Module Spring Boot                  │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐ │
│  │  Auth    │  │  Book    │  │  User    │  │contratos-grpc│ │
│  │ Module   │  │ Module   │  │ Module   │  │  (Protobuf)   │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Módulos

- **Auth** - Módulo de autenticação com JWT e gRPC
- **Book** - Gestão de livros com autenticação delegada
- **User** - Gestão de usuários com autenticação delegada
- **contratos-grpc** - Definições gRPC e geração de código

---

## 🛠️ Tecnologias

### Backend
- **Spring Boot 4.1.0** - Framework principal
- **Java 26** - Versão LTS
- **Maven** - Build system

### gRPC
- **Spring Boot Starter gRPC** - Integração gRPC
- **Protobuf** - Definições de contrato
- **gRPC-ProtoBUF-Maven-Plugin** - Geração de código

### Segurança
- **JWT** - Authentication tokens
- **Spring Security** - Security framework
- **PasswordEncoder** - Hashing de senhas

### Banco de Dados
- **PostgreSQL** - Banco de dados principal
- **Redis** - Cache de tokens
- **H2 In-Memory** - Para testes

---

## 📦 Estrutura do Projeto

```
estudo/
├── Auth/                          # Módulo de autenticação
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/auth/
│   │   │   │   ├── controller/    # Endpoints REST
│   │   │   │   ├── service/       # Lógica de negócio
│   │   │   │   ├── repository/    # Data access
│   │   │   │   ├── model/         # Entidades
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── exception/     # Tratamento centralizado de erros
│   │   │   │   └── util/          # Utilitários JWT
│   │   │   └── resources/         # Configurações
│   │   └── test/                  # Testes (54 testes)
│   └── pom.xml
├── Book/                          # Módulo de livros
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/bookapp/
│   │   │   │   ├── controller/    # Endpoints REST
│   │   │   │   ├── service/       # Lógica de negócio
│   │   │   │   └── repository/    # Data access
│   │   │   └── resources/
│   │   └── test/                  # Testes (12 testes)
│   └── pom.xml
├── User/                          # Módulo de usuários
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/user/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   └── grpc/
│   │   │   └── resources/
│   │   └── test/                  # Testes (27 testes)
│   └── pom.xml
├── contratos-grpc/                # Definições gRPC
│   ├── src/
│   │   ├── main/
│   │   │   └── proto/             # .proto files
│   │   └── test/
│   └── pom.xml
├── sistema-grpc_parent/           # Parent POM
└── AGENTS.md                       # Documentação do agente
```

---

## 🚀 Build & Execução

### Pré-requisitos

- **Java 26** ou superior
- **Maven 3.6+**
- **PostgreSQL** (opcional, para desenvolvimento)
- **Redis** (opcional, para cache)

### Build

```bash
# Instalar dependências
mvn install

# Executar todos os testes
mvn test

# Limpar e executar testes
mvn clean test

# Gerar código gRPC
cd contratos-grpc && mvn generate-sources
```

### Execução

```bash
# Executar módulo Auth
cd Auth && mvn spring-boot:run

# Executar módulo Book
cd Book && mvn spring-boot:run

# Executar módulo User
cd User && mvn spring-boot:run
```

### Execução com Docker

```bash
# Iniciar PostgreSQL e Redis
docker-compose up -d

# Executar aplicação
mvn spring-boot:run
```

### Scripts de Teste

```bash
# Gerar testes de integração (User)
bash generate-integration-tests.sh

# Gerar TODOS os testes de integração (Auth, Book)
bash generate-all-tests.sh
```

Os scripts criam arquivos de teste em `src/test/java/` e configuram `application-integration.yml` para testes com H2.

---

## 🧪 Testes

### Estrutura de Testes

- **Unit Tests** - Testes isolados de componentes
- **Integration Tests** - Testes de integração com H2 in-memory
- **Coverage** - JaCoCo com 60% mínimo

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar testes de Auth
mvn test -pl auth

# Executar testes de Book
mvn test -pl book

# Executar testes de User
mvn test -pl user

# Verificar cobertura
mvn jacoco:report
```

### Resumo dos Testes

| Módulo | Testes | Status |
|--------|--------|--------|
| **Auth** | 54 | ✅ Passando |
| **Book** | 12 | ✅ Passando |
| **User** | 27 | ✅ Passando |
| **Total** | **93** | ✅ **100%** |

---

## 🔐 Autenticação

### Fluxo de Autenticação

1. **Login** - Usuário envia credenciais
2. **Validação** - Auth service valida usuário
3. **Token Generation** - JWT tokens gerados
4. **Delegation** - Book/User delegam validação para Auth via gRPC

### Endpoints de Auth

- `POST /auth/login` - Login
- `POST /auth/register` - Registro
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - Logout
- `POST /auth/validate` - Validar token (gRPC)

---

## 🔌 gRPC

### Definições de Contrato

Arquivos `.proto` em `contratos-grpc/src/main/proto/`:

```protobuf
// Auth Service
service AuthService {
  rpc Login(LoginRequest) returns (AuthResponse);
  rpc Register(RegisterRequest) returns (UserResponse);
  rpc ValidateToken(ValidateRequest) returns (ValidateResponse);
}

// User Service
service UserService {
  rpc GetUserByUsername(GetUserByUsernameRequest) returns (UserResponse);
  rpc Register(RegisterRequest) returns (UserResponse);
}
```

### Geração de Código

```bash
cd contratos-grpc
mvn generate-sources
```

### Tratamento de Erros gRPC

As chamadas gRPC são protegidas com `try-catch` para capturar `StatusRuntimeException` e converter em respostas estruturadas:

```java
try {
    userResponse = userGrpcStub.getUserByUsername(getUserRequest);
} catch (Throwable t) {
    throw handleGrpcError(t);
}
```

O método `handleGrpcError()` converte exceções gRPC em mensagens amigáveis:
- `StatusRuntimeException` → "User Service unavailable: [description]"
- Outras exceções → "Failed to reach User Service"

---

## 📊 Monitoramento

### Coverage Report

```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

### Logs

```bash
# Verificar logs
tail -f target/*.log
```

---

## 🐳 Docker

## 🐳 Deploy k3s

### Passos de Deploy

```bash
# 1. Construir imagens
./infra/build-apps.sh

# 2. Carregar imagens no cluster k3d
k3d image load auth-app:latest user-app:latest book-app:latest --cluster k3s-test

# 3. Aplicar manifests Kubernetes
kubectl apply -f ./infra/k8s/

# 4. Restart deployments
kubectl rollout restart deployment auth-service book-service user-service
```

### imagePullPolicy

Os manifests de deployment usam `imagePullPolicy: Never` para utilizar imagens carregadas localmente nos nodes do cluster k3d. Isso evita tentativas de pull de registry externo.

### Problema Resolvido

- **ImagePullBackOff**: k3d nodes não compartilham imagens do Docker host automaticamente
- **no main manifest attribute**: Auth e Book não herdavam spring-boot-maven-plugin do parent pom
- **Solução**: Adicionado plugin em Auth/Book pom.xml; `imagePullPolicy: Never` nos manifests; `k3d image load` no deploy script

### Docker Compose

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: study_db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

```bash
docker-compose up -d
```

---

## 📝 Conventions

### Commit Messages

Conventional Commits:
```
feat: add new feature
fix: fix bug
docs: update documentation
test: add tests
refactor: code refactoring
```

### Code Style

- **Prettier** - Code formatting
- **PSV** - Static code analysis
- **JaCoCo** - Code coverage > 60%

---

## 🤝 Contribuição

1. Fork do projeto
2. Crie branch (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'feat: add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Abra Pull Request

---

## 🐳 Infraestrutura (infra/)

### k3s Test Environment
- `infra/` - Arquivos de orquestração Docker e Kubernetes
- `docker-compose.k3s.yml` - Docker Compose para k3s server, agent, e serviços de app
- `deploy-k3s.sh` - Script para iniciar ambiente k3s
- `undeploy-k3s.sh` - Script para remover ambiente k3s
- `build-apps.sh` - Script para build de Docker images

### Kubernetes Manifests
- `infra/k8s/` - Manifests para deployments, statefulsets, services, ingress
  - `auth-deployment.yml` - Auth service deployment
  - `user-deployment.yml` - User service deployment
  - `book-deployment.yml` - Book service deployment
  - `postgres-auth.yml` - Auth DB statefulset
  - `postgres-book.yml` - Book DB statefulset
  - `postgres-user.yml` - User DB statefulset
  - `redis.yml` - Redis deployment
  - `ingress.yml` - Ingress controller config
  - `auth-service.yml` - Auth service exposure
  - `user-service.yml` - User service exposure

---

## 📋 Plans Directory

### Documentos de Planejamento
- `plans/` - Documentos de planejamento para features e melhorias
- `K3S_TEST_ENVIRONMENT_PLAN.md` - Plano de arquitetura do ambiente k3s
- `GLOBAL_EXCEPTION_HANDLER_PLAN.md` - Plano de tratamento centralizado de erros
- `JWT_VALIDATION_FILTER_IMPROVEMENTS.md` - Melhorias de validação JWT
- `TOKEN_COOKIE_PLAN.md` - Plano de implementação de token cookie
- `TEST_COVERAGE_PLAN.md` - Plano de melhoria de cobertura de testes

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 🏷️ Tags

- `spring-boot`
- `grpc`
- `jwt`
- `authentication`
- `multi-module`
- `java-26`
- `postgresql`
- `redis`
- `ai-assisted`
- `llama-cpp`

---

## 📞 Suporte

Para questões técnicas ou dúvidas sobre o projeto, entre em contato com o time de desenvolvimento.

---

*Gerado e mantido com ❤️ por IA (llama.cpp + opencode)*