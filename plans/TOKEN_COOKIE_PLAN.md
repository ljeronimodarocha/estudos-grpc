# Token Cookie Plan

## Data: 2026-07-25

## Objetivo
Migrar tokens JWT de resposta JSON para cookies HttpOnly, prevenindo roubo de sessão via XSS.

## Status: CONCLUÍDO

## Alterações Realizadas

### 1. AuthController.java
- Substituída resposta JSON de tokens por cookies HttpOnly
- Body da resposta contém apenas `{ "expiresIn": <segundos> }`
- Login/Register/Refresh: setam cookies `access_token` e `refresh_token`
- Logout: remove cookies (maxAge = 0)
- Validate: permanece inalterado

### 2. AuthResponse.java
- DTO simplificado: apenas `expiresIn` (long)
- Tokens removidos do response body (apenas body com expiresIn)

### 3. JwtValidationFilter.java (Book)
- Token extraído do cookie `access_token`
- Fallback para header `Authorization: Bearer` (backward compat)

### 4. GrpcServerService.java
- Atualizado para usar `setExpiresIn()` ao invés de `setAccessToken()`/`setRefreshToken()`

### 5. AuthService.java
- Retornar `new AuthResponse(accessTokenValiditySeconds)` ao invés de 4 args

### 6. Testes Atualizados
- `AuthControllerTest.java`: assertions atualizadas para verificar cookies + body
- `AuthResponseTest.java`: testando apenas `expiresIn`
- `AuthServiceTest.java`: assertions atualizadas para `result.expiresIn()`

## Configuração dos Cookies

| Propriedade | access_token | refresh_token |
|---|---|---|
| HttpOnly | true | true |
| Secure | true | true |
| SameSite | Strict | Strict |
| Path | / | / |
| Max-Age | 3600 | 86400 |

## Resultado dos Testes

- **Auth Module**: 68 testes, 0 falhas
- **Book Module**: 12 testes, 0 falhas
