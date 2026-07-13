# 🧪 Plano de Testes de Integração - H2 Database

## 🎯 Objetivo
Criar testes de integração para controllers e serviços usando H2 Database em memória, garantindo que a persistência de dados funcione corretamente.

---

## 📋 Escopo dos Testes

### 1. AuthControllerIntegrationTest
**Local:** Auth/src/test/java/com/example/auth/controller/AuthControllerIntegrationTest.java

**Testes:**
- ✅ POST /auth/register - Registro de novo usuário
- ✅ POST /auth/login - Login com credenciais válidas
- ✅ POST /auth/refresh - Refresh de token
- ✅ POST /auth/logout - Logout e revogação de token
- ✅ GET /auth/validate - Validação de token

**Configuração:**
- @WebMvcTest(AuthController.class)
- @AutoConfigureTestDatabase (H2)
- @ActiveProfiles("test")
- @MockBean para gRPC stubs

---

### 2. BookControllerIntegrationTest
**Local:** Book/src/test/java/com/example/bookapp/controller/BookControllerIntegrationTest.java

**Testes:**
- ✅ GET /api/books - Listar todos os livros
- ✅ GET /api/books/{id} - Buscar livro por ID
- ✅ POST /api/books - Criar novo livro
- ✅ PUT /api/books/{id} - Atualizar livro
- ✅ DELETE /api/books/{id} - Deletar livro
- ✅ Cenários de erro (livro não encontrado)

**Configuração:**
- @WebMvcTest(BookController.class)
- @AutoConfigureTestDatabase (H2)
- @ActiveProfiles("test")
- @MockBean para serviços não testados

---

### 3. UserRepositoryIntegrationTest
**Local:** Auth/src/test/java/com/example/auth/repository/UserRepositoryAuthenticationIntegrationTest.java

**Testes:**
- ✅ Persistência de UserAuthentication
- ✅ Busca por username
- ✅ Verificação de existência
- ✅ Atualização e exclusão

**Configuração:**
- @DataJpaTest
- @SpringBootTest
- @TestEntityManager
- @ActiveProfiles("test")

---

### 4. TokenRepositoryIntegrationTest
**Local:** Auth/src/test/java/com/example/auth/repository/TokenRepositoryIntegrationTest.java

**Testes:**
- ✅ Persistência de tokens
- ✅ Busca por token
- ✅ Busca de tokens válidos (não revogados e não expirados)
- ✅ Busca de tokens por usuário

**Configuração:**
- @DataJpaTest
- @SpringBootTest
- @TestEntityManager
- @ActiveProfiles("test")

---

## 🗂️ Estrutura de Pastas

src/test/java/com/example/
├── auth/
│   ├── controller/
│   │   └── AuthControllerIntegrationTest.java
│   ├── repository/
│   │   ├── UserRepositoryAuthenticationIntegrationTest.java
│   │   └── TokenRepositoryIntegrationTest.java
│   └── service/
│       └── AuthServiceIntegrationTest.java
└── bookapp/
    └── controller/
        └── BookControllerIntegrationTest.java

src/test/resources/
├── application-test.yml (configuração principal)
└── application-integration.yml (configuração específica)

---

## 📝 Configuração de Testes

### application-test.yml (atual)
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop

### application-integration.yml (novo)
spring:
  datasource:
    url: jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
      show-sql: true

---

## 🧩 Mocking de gRPC

Para testes de integração do AuthController, mockar o gRPC client:

@MockBean
private UserServiceGrpc.UserServiceBlockingStub userGrpcStub;

---

## 📊 Matriz de Testes

| Teste | Classe | Testes | Objetivo |
|-------|--------|--------|----------|
| AuthController | AuthControllerIntegrationTest | 5 | Testar endpoints de autenticação |
| BookController | BookControllerIntegrationTest | 6 | Testar CRUD completo de livros |
| UserRepository | UserRepositoryIntegrationTest | 4 | Testar persistência de usuários |
| TokenRepository | TokenRepositoryIntegrationTest | 4 | Testar persistência de tokens |
| **Total** | | **19** | |

---

## 🚀 Execução dos Testes

```bash
# Executar todos os testes de integração
mvn test -Dtest=*IntegrationTest

# Executar apenas AuthController
mvn test -Dtest=AuthControllerIntegrationTest

# Executar apenas BookController
mvn test -Dtest=BookControllerIntegrationTest

# Gerar relatório de cobertura com integração
mvn test jacoco:report
```

---

## ✅ Critérios de Sucesso

- [ ] 100% dos testes de integração passando
- [ ] Cobertura de controllers > 80%
- [ ] Cobertura de repositórios > 80%
- [ ] Tempo de execução < 30 segundos
- [ ] Zero falhas ou erros
- [ ] Relatórios JaCoCo gerados corretamente

---

## 📈 Benefícios

1. **Velocidade:** H2 em memória é extremamente rápido
2. **Isolamento:** Cada teste tem seu próprio banco
3. **Reprodutibilidade:** Mesma configuração local e CI/CD
4. **Cobertura Real:** Testa persistência de dados real
5. **Manutenção:** Fácil de manter e entender

---

## ⚠️ Considerações

### O que NÃO testar com H2:
- ❌ Conexão com PostgreSQL real
- ❌ Redis cache real
- ❌ gRPC real (deve ser mockado)

### O que testar com H2:
- ✅ CRUD completo
- ✅ Transações
- ✅ Relacionamentos entre entidades
- ✅ Validação de dados
- ✅ Queries SQL complexas

---

**Próximo passo:** Implementar os testes conforme este plano! 🚀
