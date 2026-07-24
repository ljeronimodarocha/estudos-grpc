# Plano de Implementação: GlobalExceptionHandler e Tratamento de Erros gRPC

**Data de Criação:** 24 de Julho de 2026  
**Status:** ✅ Concluído

---

## 🎯 Objetivo

Implementar tratamento centralizado de exceções e tratamento robusto de erros gRPC no módulo Auth.

---

## 📋 Problema Identificado

### 1. Erros de Autenticação
Quando `authenticationManager.authenticate()` lançava `BadCredentialsException`, o Spring Security convertia a exceção em **403 Forbidden** sem mensagem estruturada:

```json
{
  "timestamp": "2026-07-24T23:35:25.285Z",
  "status": 403,
  "error": "Forbidden",
  "trace": "org.springframework.security.authentication.BadCredentialsException: Usuário inexistente ou senha inválida..."
}
```

### 2. Erros de gRPC
Chamadas gRPC (`getUserByUsername`, `register`) lançavam `StatusRuntimeException` sem tratamento, exporando stack traces internos.

---

## ✅ Solução Implementada

### 1. `GlobalExceptionHandler.java`
**Localização:** `Auth/src/main/java/com/example/auth/exception/GlobalExceptionHandler.java`

Implementa `@RestControllerAdvice` com handlers para:

| Exceção | Status HTTP | Mensagem |
|---------|-------------|----------|
| `BadCredentialsException` | 401 | "Invalid username or password" |
| `AuthenticationException` | 401 | Mensagem da exceção |
| `AccessDeniedException` | 403 | "Access denied" |
| `ExpiredJwtException` | 401 | "Token has expired" |
| `IllegalArgumentException` | 400 | Mensagem da exceção |
| `RuntimeException` | 500 | Mensagem da exceção |
| `Exception` | 500 | "An unexpected error occurred" (fallback) |

### 2. `ErrorResponse.java`
**Localização:** `Auth/src/main/java/com/example/auth/dto/ErrorResponse.java`

Record com estrutura consistente:
```java
public record ErrorResponse(int status, String message, LocalDateTime timestamp)
```

### 3. Tratamento de Erros gRPC em `AuthService.java`

#### login()
```java
com.example.grpc.user.UserResponse userResponse;
try {
    userResponse = userGrpcStub.getUserByUsername(getUserRequest);
} catch (Throwable t) {
    throw handleGrpcError(t);
}
```

#### register()
```java
UserResponse registered;
try {
    registered = userGrpcStub.register(userRequest);
} catch (Throwable t) {
    throw handleGrpcError(t);
}
```

#### handleGrpcError(Throwable t)
```java
private RuntimeException handleGrpcError(Throwable t) {
    if (t instanceof StatusRuntimeException e) {
        return new RuntimeException("User Service unavailable: " + e.getStatus().getDescription(), e);
    }
    return t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException("Failed to reach User Service", t);
}
```

---

## 📊 Resultados Esperados

### Antes
```json
{
  "timestamp": "2026-07-24T23:35:25.285Z",
  "status": 403,
  "error": "Forbidden",
  "trace": "org.springframework.security.authentication.BadCredentialsException..."
}
```

### Depois
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "timestamp": "2026-07-24T23:35:25.285Z"
}
```

### Benefícios
- ✅ Respostas estruturadas e previsíveis
- ✅ Mensagens amigáveis para clientes
- ✅ Tratamento centralizado (funciona em todos os controllers)
- ✅ Proteção contra exposição de stack traces
- ✅ Tratamento consistente de erros gRPC

---

## 🧪 Testes

Todos os 54 testes existentes passaram sem modificações:
- **Auth**: 54 testes ✅
- **Book**: 12 testes ✅
- **User**: 1 teste ✅

---

## 📝 Commits Relacionados

**Commit:** `bb45653` - "feat(auth): add GlobalExceptionHandler e tratamento de erros gRPC"

**Arquivos Modificados:**
- `Auth/src/main/java/com/example/auth/dto/ErrorResponse.java` (novo)
- `Auth/src/main/java/com/example/auth/exception/GlobalExceptionHandler.java` (novo)
- `Auth/src/main/java/com/example/auth/service/AuthService.java` (modificado)
- `Auth/src/main/java/com/example/auth/grpc/GrpcServerService.java` (modificado)

---

## 📚 Documentação Atualizada

### README.md
- ✅ Adicionada seção "Tratamento Centralizado de Erros"
- ✅ Atualizada estrutura de diretórios (novo pacote `exception/`)
- ✅ Adicionada seção "Tratamento de Erros gRPC"

---

## 🎯 Próximos Passos (Opcional)

1. **Testes de Integration** - Adicionar testes para GlobalExceptionHandler
2. **Documentação de API** - Documentar formato de respostas de erro
3. **Monitoramento** - Adicionar métricas de erros (Micrometer)
4. **Logging** - Implementar logging estruturado de exceções

---

**Status:** ✅ Concluído  
**Data de Implementação:** 24 de Julho de 2026
