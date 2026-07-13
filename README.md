# Sistema gRPC - Authentication & Books

Multi-module Spring Boot application com gRPC para autenticação e gestão de livros.

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
- **Java 21** - Versão LTS
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
│   │   │   │   └── util/          # Utilitários JWT
│   │   │   └── resources/         # Configurações
│   │   └── test/                  # Testes (23 testes)
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
│   │   └── test/                  # Testes (1 teste)
│   └── pom.xml
├── contratos-grpc/                # Definições gRPC
│   ├── src/
│   │   ├── main/
│   │   │   └── proto/             # .proto files
│   │   └── test/
│   └── pom.xml
├── sistema-grpc-parent/           # Parent POM
└── AGENTS.md                       # Documentação do agente
```

---

## 🚀 Build & Execução

### Pré-requisitos

- **Java 21** ou superior
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
| **Auth** | 23 | ✅ Passando |
| **Book** | 12 | ✅ Passando |
| **User** | 1 | ✅ Passando |
| **Total** | **36** | ✅ **100%** |

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

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 🏷️ Tags

- `spring-boot`
- `grpc`
- `jwt`
- `authentication`
- `multi-module`
- `java-21`
- `postgresql`
- `redis`
- `ai-assisted`
- `llama-cpp`

---

## 📞 Suporte

Para questões técnicas ou dúvidas sobre o projeto, entre em contato com o time de desenvolvimento.

---

*Gerado e mantido com ❤️ por IA (llama.cpp + opencode)*
