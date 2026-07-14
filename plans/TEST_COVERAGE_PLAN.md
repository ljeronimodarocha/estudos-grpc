# Plano de Aumento de Cobertura de Testes
**Projetos:** Auth, Book, User  
**Versão JaCoCo:** 0.8.15 LTS (Suporta Java 21-26)  
**Objetivo:** 80% de cobertura  
**Data de Criação:** 14 de Julho de 2026

---

## 📊 Diagnóstico Atual

### Resumo do Estado
| Módulo | Arquivos Java | Testes Existentes | Cobertura | Testes Necessários |
|--------|---------------|-------------------|-----------|-------------------|
| **AUTH** | 42 | 3 | 1.5% | ~35-40 testes |
| **BOOK** | 28 | 2 | 1.6% | ~15-20 testes |
| **USER** | 31 | 4 | 4.4% | ~10-15 testes |

### Gaps Identificados

#### 🔴 AUTH - Crítico (69% de lacuna)
- `config/` 6 arquivos → 0 testes
- `controller/` 1 arquivo → 0 testes
- `dto/` 6 arquivos → 0 testes
- `grpc/` 1 arquivo → 0 testes
- `model/` 2 arquivos → 0 testes
- `service/` 3 arquivos → 0 testes

#### 🟡 BOOK - Moderado (65% de lacuna)
- `config/` 3 arquivos → 0 testes
- `filter/` 1 arquivo → 0 testes
- `model/` 1 arquivo → 0 testes
- `repository/` 1 arquivo → 0 testes

#### 🟢 USER - Melhor (65% de lacuna)
- `config/` 3 arquivos → 0 testes
- `dto/` 2 arquivos → 0 testes
- `filter/` 1 arquivo → 0 testes
- `model/` 1 arquivo → 0 testes
- `repository/` 1 arquivo → 0 testes

---

## 🎯 Estratégia de Execução

### **FASE 1: Alta Prioridade - Services e DTOs (Semanas 1-2)**
**Objetivo: Alcançar 15-20% de cobertura**

| Prioridade | Módulo | Arquivos | Testes | Tempo |
|------------|--------|----------|--------|-------|
| 🔴 P0 | Auth Service | AuthService, TokenService | 6-8 | 4h |
| 🔴 P0 | Auth DTOs | 6 DTOs | 12 | 6h |
| 🔴 P0 | Book Service | BookServiceImpl | 5-7 | 3h |
| 🔴 P0 | User Service | UserService | 1-2 | 2h |
| 🔴 P0 | Auth Config | SecurityConfig, JwtConfig | 8-10 | 6h |
| 🔴 P0 | Book DTOs | Book, RegisterRequest | 6 | 3h |
| 🔴 P0 | User DTOs | RegisterRequest | 4 | 2h |

**Total Fase 1: ~26 testes, ~21h**

---

### **FASE 2: Média Prioridade - Repositories e Controllers (Semanas 3-4)**
**Objetivo: Alcançar 30-40% de cobertura**

| Prioridade | Módulo | Arquivos | Testes | Tempo |
|------------|--------|----------|--------|-------|
| 🟡 P1 | Auth Repository | TokenRepository, UserRepository | 10 | 6h |
| 🟡 P1 | Book Repository | BookRepository | 6 | 3h |
| 🟡 P1 | User Repository | UserRepository | 6 | 3h |
| 🟡 P1 | Auth Controller | AuthController | 10 | 6h |
| 🟡 P1 | Book Controller | BookController | 3 | 2h |
| 🟡 P1 | User Controller | UserController | 2 | 2h |

**Total Fase 2: ~33 testes, ~22h**

---

### **FASE 3: Baixa Prioridade - Modelos, Configs e gRPC (Semanas 5-6)**
**Objetivo: Alcançar 50-60% de cobertura**

| Prioridade | Módulo | Arquivos | Testes | Tempo |
|------------|--------|----------|--------|-------|
| 🟢 P2 | Auth Model | Token, UserAuthentication | 8 | 4h |
| 🟢 P2 | Auth Configs | GrpcClientConfig, OAuth2Config, PasswordEncoderConfig | 12 | 8h |
| 🟢 P2 | Book Configs | GrpcClientConfig, SecurityConfig | 6 | 4h |
| 🟢 P2 | User Configs | GrpcClientConfig, SecurityConfig | 6 | 4h |
| 🟢 P2 | Filters | JwtAuthenticationFilter, JwtValidationFilter | 8 | 4h |

**Total Fase 3: ~40 testes, ~28h**

---

### **FASE 4: Otimização e Refinamento (Semana 7+)**
**Objetivo: Alcançar 80% de cobertura com testes de borda**

| Tipo | Descrição | Tempo |
|------|-----------|-------|
| Edge Cases | Testes de casos extremos e erros | 6h |
| Integration | Testes de integração entre módulos | 12h |
| Coverage Analysis | Análise de código não coberto | 8h |

**Total Fase 4: ~26h**

---

## 📈 Projeção de Resultados

### Timeline Estimada
```
Semana 1-2:  ██████████░░░░░░░░░░░░  20% cobertura
Semana 3-4:  ██████████████████░░░░░  40% cobertura
Semana 5-6:  ████████████████████████  60% cobertura
Semana 7:    ████████████████████████████████████  80% cobertura
```

### Métricas Esperadas
| Fase | Testes Totais | Cobertura | Horas |
|------|---------------|-----------|-------|
| F1 | 26 | 15-20% | 21h |
| F2 | 59 | 30-40% | 22h |
| F3 | 90 | 50-60% | 28h |
| F4 | 116 | 70-80% | 26h |
| **TOTAL** | **181** | **80%** | **97h** |

---

## 🛠️ Padrões de Teste Recomendados

### 1. Unit Tests (Mockito + JUnit 5)
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private AuthenticationManager authManager;
    @Mock private UserServiceAuth userServiceAuth;
    @InjectMocks private AuthService authService;
    
    @Test
    void login_success() {
        // Arrange - Ative mocks
        // Act - Execute método
        // Assert - Verifique comportamento
    }
}
```

### 2. Integration Tests (SpringBootTest)
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    
    @Test
    void loginEndpoint_returns200WithValidCreds() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
```

### 3. Edge Cases
- Tokens inválidos/expirados
- Usuários não encontrados
- Inputs nulos/vazios
- Erros de gRPC
- Condições de concorrência

### 4. Testes de Segurança
- Permissões de endpoints
- Validação de JWT
- Proteção contra CSRF/XSS

---

## ⚠️ Riscos e Mitigações

| Risco | Impacto | Mitigação |
|-------|---------|-----------|
| Tempo insuficiente | Baixa cobertura | Priorizar P0/P1, usar timeboxing |
| Testes frágeis | Manutenção difícil | Testar comportamento, não implementação |
| Configurações complexas | Dificuldade de teste | Mockar dependências externas |
| gRPC binário | Dificuldade de testar | Usar mocks e stubs |

---

## 📝 Checklist de Implementação

### Para cada novo teste:
- [ ] Seguir padrão Arrange-Act-Assert
- [ ] Usar nomes descritivos (`whenServiceThrowsException`)
- [ ] Mockar apenas dependências, não implementação
- [ ] Cobrir sucesso, falha e bordas
- [ ] Executar suite completa após criar
- [ ] Manter cobertura >60% localmente

### Para cada fase:
- [ ] Rodar `mvn clean test jacoco:report`
- [ ] Analisar relatório HTML
- [ ] Identificar gaps críticos
- [ ] Ajustar plano baseado em resultados

---

## 🎯 Perguntas para Refinamento

1. **Qual prazo real para atingir 80% de cobertura?**
2. **Há recursos disponíveis (QA, dev dedicado)?**
3. **Deve-se usar Spring Boot Testcontainers?**
4. **Precisa-se de testes de performance e segurança?**

---

## 📌 Comandos Úteis

```bash
# Gerar relatório de cobertura
mvn clean test jacoco:report -pl Auth,Book,User

# Verificar cobertura atual
mvn jacoco:report -pl Auth,Book,User

# Analisar relatório HTML
open Auth/target/site/jacoco/index.html
```

---

**Status:** Plano aprovado para execução  
**Próximo passo:** Iniciar FASE 1 (Services e DTOs)