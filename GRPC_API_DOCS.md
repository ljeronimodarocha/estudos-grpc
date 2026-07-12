# Documentação da API gRPC - Auth e User Services

## Visão Geral

Este documento descreve a API gRPC implementada entre os serviços Auth e User do sistema.

---

## Arquitetura

```
┌─────────────────┐                    ┌─────────────────┐
│                 │                    │                 │
│  Auth Service   │◄────── gRPC ──────►│  User Service   │
│  (Porta 9090)   │   localhost:9091   │  (Porta 9091)   │
│                 │                    │                 │
└─────────────────┘                    └─────────────────┘
```

---

## Serviço User (UserService) - Porta 9091

### Métodos

#### 1. Register
Cadastra um novo usuário no sistema.

**Request:**
```protobuf
message RegisterRequest {
  string username = 1;
  string email = 2;
  string display_name = 3;
}
```

**Response:**
```protobuf
message UserResponse {
  int32 id = 1;
  string name = 2;
  string email = 3;
  string displayName = 4;
}
```

**Uso no Auth Service:**
```java
UserResponse registered = userGrpcStub.register(
    com.example.grpc.user.RegisterRequest.newBuilder()
        .setUsername(request.username())
        .setEmail(request.email())
        .setDisplayName(request.displayName())
        .build()
);
```

---

#### 2. GetUserById
Retorna informações de um usuário por ID.

**Request:**
```protobuf
message GetUserByIdRequest {
  int32 id = 1;
}
```

**Response:**
```protobuf
message UserResponse {
  int32 id = 1;
  string name = 2;
  string email = 3;
  string displayName = 4;
}
```

**Uso no Auth Service:** (Opcional - para recuperação de senha)
```java
UserResponse userResponse = userGrpcStub.getUserById(
    com.example.grpc.user.GetUserByIdRequest.newBuilder()
        .setId(userId)
        .build()
);
```

---

#### 3. GetUserByUsername (NOVO)
Retorna informações de um usuário por username. **Método essencial para autenticação.**

**Request:**
```protobuf
message GetUserByUsernameRequest {
  string username = 1;
}
```

**Response:**
```protobuf
message UserResponse {
  int32 id = 1;
  string name = 2;
  string email = 3;
  string displayName = 4;
}
```

**Uso no Auth Service:**
```java
// Buscar usuário no User Service via gRPC
com.example.grpc.user.GetUserByUsernameRequest getUserRequest =
    com.example.grpc.user.GetUserByUsernameRequest.newBuilder()
        .setUsername(request.username())
        .build();

com.example.grpc.user.UserResponse userResponse = userGrpcStub.getUserByUsername(getUserRequest);
```

---

#### 4. GetUserByIdentity (NOVO)
Retorna informações de um usuário por ID como string.

**Request:**
```protobuf
message GetUserByIdentityRequest {
  string userId = 1;
}
```

**Response:**
```protobuf
message UserResponse {
  int32 id = 1;
  string name = 2;
  string email = 3;
  string displayName = 4;
}
```

**Uso no Auth Service:** (Opcional)
```java
UserResponse userResponse = userGrpcStub.getUserByIdentity(
    com.example.grpc.user.GetUserByIdentityRequest.newBuilder()
        .setUserId(userId)
        .build()
);
```

---

## Serviço Auth (AuthService) - Porta 9090

### Métodos

#### 1. Login
Autentica um usuário e retorna tokens JWT.

**Request:**
```protobuf
message LoginRequest {
  string username = 1;
  string password = 2;
}
```

**Response:**
```protobuf
message AuthResponse {
  string access_token = 1;
  string refresh_token = 2;
  string token_type = 3;
  int64 expires_in = 4;
}
```

**Fluxo:**
1. Auth chama User Service: `GetUserByUsername(username)`
2. Valida senha localmente
3. Gera tokens JWT
4. Retorna tokens ao cliente

**Implementação:**
```java
public AuthResponse login(LoginRequest request) {
    // Buscar usuário no User Service via gRPC
    GetUserByUsernameRequest getUserRequest =
        GetUserByUsernameRequest.newBuilder()
            .setUsername(request.username())
            .build();
    
    UserResponse userResponse = userGrpcStub.getUserByUsername(getUserRequest);

    // Autenticar via Spring Security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );

    // Buscar UserAuthentication para gerar tokens
    UserAuthentication userAuthentication = userServiceAuth.findByUsername(request.username());

    String accessToken = jwtUtil.generateAccessToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));
    String refreshToken = jwtUtil.generateRefreshToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));

    tokenService.saveToken(userAuthentication, refreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);

    return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenValiditySeconds);
}
```

---

#### 2. Register
Registra um novo usuário completo (inclui cadastro no User Service).

**Request:**
```protobuf
message RegisterRequest {
  string username = 1;
  string email = 2;
  string password = 3;
  string user_id = 4;
}
```

**Response:**
```protobuf
message AuthResponse {
  string access_token = 1;
  string refresh_token = 2;
  string token_type = 3;
  int64 expires_in = 4;
}
```

**Fluxo:**
1. Auth chama User Service: `Register(username, email, displayName)`
2. Cria UserAuthentication com senha hash
3. Gera tokens JWT
4. Retorna tokens ao cliente

---

#### 3. Refresh
Renova tokens expirados.

**Request:**
```protobuf
message RefreshRequest {
  string refresh_token = 1;
}
```

**Response:**
```protobuf
message AuthResponse {
  string access_token = 1;
  string refresh_token = 2;
  string token_type = 3;
  int64 expires_in = 4;
}
```

---

#### 4. Logout
Revoga um token específico.

**Request:**
```protobuf
message LogoutRequest {
  string refresh_token = 1;
}
```

**Response:**
```protobuf
message Empty {}
```

---

#### 5. Validate
Valida um token JWT.

**Request:**
```protobuf
message ValidateTokenRequest {
  string token = 1;
}
```

**Response:**
```protobuf
message ValidateResponse {
  bool valid = 1;
  string username = 2;
}
```

---

## Configuração

### User Service (application.yml)
```yaml
spring:
  grpc:
    server:
      port: 9091
```

### Auth Service (application.yml)
```yaml
server:
  port: 8082

spring:
  grpc:
    server:
      port: 9090
    client:
      channels:
        usuarios-service:
          address: "localhost:9091"
```

---

## Exemplos de Uso

### 1. Registro de Usuário

```java
// No Auth Service
public AuthResponse register(RegisterRequest request) {
    // 1. Chamar User Service para cadastrar
    com.example.grpc.user.RegisterRequest userRequest = 
        com.example.grpc.user.RegisterRequest.newBuilder()
            .setUsername(request.username())
            .setEmail(request.email())
            .setDisplayName(request.displayName())
            .build();
    
    UserResponse registered = userGrpcStub.register(userRequest);
    
    // 2. Criar autenticação e gerar tokens
    UserAuthentication userAuth = userServiceAuth.registerUserAuthentication(request, (long) registered.getId());
    UserDetails userDetails = userServiceAuth.loadUserByUsername(userAuth.getUsername());
    
    String accessToken = jwtUtil.generateAccessToken(userDetails);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);
    
    tokenService.saveToken(userAuth, refreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);
    
    return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenValiditySeconds);
}
```

### 2. Login

```java
// No Auth Service
public AuthResponse login(LoginRequest request) {
    // 1. Buscar usuário no User Service via gRPC
    com.example.grpc.user.GetUserByUsernameRequest getUserRequest =
        com.example.grpc.user.GetUserByUsernameRequest.newBuilder()
            .setUsername(request.username())
            .build();
    
    com.example.grpc.user.UserResponse userResponse = userGrpcStub.getUserByUsername(getUserRequest);

    // 2. Autenticar via Spring Security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );

    // 3. Buscar UserAuthentication para gerar tokens
    UserAuthentication userAuthentication = userServiceAuth.findByUsername(request.username());

    String accessToken = jwtUtil.generateAccessToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));
    String refreshToken = jwtUtil.generateRefreshToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));

    tokenService.saveToken(userAuthentication, refreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);

    return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenValiditySeconds);
}
```

---

## Tabela de Resumo

| Serviço | Método | Proto | Função | Parâmetros | Retorno |
|---------|--------|-------|--------|------------|---------|
| User | Register | `service User` | Cadastra usuário | username, email, displayName | UserResponse |
| User | GetUserById | `service User` | Busca por ID | id | UserResponse |
| User | GetUserByUsername | `service User` | Busca por username | username | UserResponse |
| User | GetUserByIdentity | `service User` | Busca por userId | userId | UserResponse |
| Auth | Login | `service Auth` | Autenticação | username, password | AuthResponse |
| Auth | Register | `service Auth` | Registro completo | username, email, password, displayName | AuthResponse |
| Auth | Refresh | `service Auth` | Renova tokens | refreshToken | AuthResponse |
| Auth | Logout | `service Auth` | Revoga tokens | refreshToken | Empty |
| Auth | Validate | `service Auth` | Valida token | token | ValidateResponse |

---

## Status da Implementação

✅ **Contrato gRPC atualizado** - `contratos-grpc/user.proto`  
✅ **User Service implementado** - Métodos findByUsername e findByIdentity  
✅ **GrpcServerService atualizado** - Handlers para novos métodos  
✅ **Auth Service atualizado** - Login usando GetUserByUsername  
✅ **Pom.xml atualizado** - Dependências gRPC adicionadas  
✅ **Configuração de rede** - Portas configuradas  
✅ **Compilação** - BUILD SUCCESS

---

## Próximos Passos (Opcionais)

- [ ] Implementar autenticação gRPC (mTLS)
- [ ] Adicionar logging distribuído
- [ ] Implementar circuit breaker para falhas
- [ ] Adicionar testes de integração
- [ ] Configurar Docker Compose para deploy

---

**Última atualização**: 2026-07-12
