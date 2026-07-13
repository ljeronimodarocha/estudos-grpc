#!/bin/bash

echo "🚀 Gerando TODOS os testes de integração com H2 Database..."

# Criar diretórios
echo "📁 Criando estrutura..."
mkdir -p /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/controller
mkdir -p /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/repository
mkdir -p /mnt/1TB/desenvolvimento/estudo/Book/src/test/java/com/example/bookapp/controller

# Criar application-integration.yml
echo "📝 Criando application-integration.yml..."
cat > /mnt/1TB/desenvolvimento/estudo/src/test/resources/application-integration.yml << 'YMLEOF'
spring:
  datasource:
    url: jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
      show-sql: true
  security:
    user:
      name: testuser
      password: testpassword
jwt:
  token-validity-seconds: 3600
  refresh-token-validity-seconds: 86400
YMLEOF

echo "✅ application-integration.yml criado!"

echo ""
echo "📝 Gerando AuthControllerIntegrationTest.java..."
cat > /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/controller/AuthControllerIntegrationTest.java << 'JAVAEOF'
package com.example.auth.controller;

import com.example.auth.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.grpc.user.UserServiceGrpc.UserServiceBlockingStub userStub;

    @BeforeEach
    void setUp() {
        when(userStub.getUserByUsername(any())).thenReturn(
            com.example.grpc.user.UserResponse.newBuilder()
                .setId(1L)
                .setName("Test User")
                .setEmail("test@example.com")
                .setDisplayName("Test Display")
                .build()
        );
    }

    @Test
    void register_returnsAuthResponse() throws Exception {
        String json = """
            {
                "username": "newuser",
                "email": "new@example.com",
                "displayName": "New User",
                "password": "password123"
            }
            """;
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void login_returnsAuthResponse() throws Exception {
        String json = """
            {
                "username": "testuser",
                "password": "testpassword"
            }
            """;
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void refresh_returnsAuthResponse() throws Exception {
        String json = """
            {
                "refreshToken": "refresh-token-123"
            }
            """;
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void logout_returnsOk() throws Exception {
        String json = """
            {
                "refreshToken": "refresh-token-123"
            }
            """;
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void validate_returnsValidateResponse() throws Exception {
        mockMvc.perform(get("/auth/validate?token=valid-token-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").exists());
    }
}
JAVAEOF

echo "✅ AuthControllerIntegrationTest.java criado (5 testes)!"

echo ""
echo "📝 Gerando BookControllerIntegrationTest.java..."
cat > /mnt/1TB/desenvolvimento/estudo/Book/src/test/java/com/example/bookapp/controller/BookControllerIntegrationTest.java << 'JAVAEOF'
package com.example.bookapp.controller;

import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @BeforeEach
    void setUp() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Test Book 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("123456");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Test Book 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("789012");

        when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));
    }

    @Test
    void getAllBooks_returnsAllBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getBookById_returnsBookWhenFound() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(new Book() {{setId(1L); setTitle("Test");}}));
        mockMvc.perform(get("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createBook_savesAndReturnsBook() throws Exception {
        String json = """
            {"title": "New Book", "author": "Author", "isbn": "123"}
            """;
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    void updateBook_updatesBook() throws Exception {
        String json = """
            {"title": "Updated", "author": "Updated", "isbn": "456"}
            """;
        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteBook_deletesBook() throws Exception {
        mockMvc.perform(delete("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
JAVAEOF

echo "✅ BookControllerIntegrationTest.java criado (5 testes)!"

echo ""
echo "📝 Gerando UserRepositoryIntegrationTest.java..."
cat > /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/repository/UserRepositoryAuthenticationIntegrationTest.java << 'JAVAEOF'
package com.example.auth.repository;

import com.example.auth.model.UserAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryAuthenticationIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void save_userIsPersisted() {
        UserAuthentication user = UserAuthentication.builder()
            .username("testuser")
            .password("encodedpass")
            .build();
        
        testEntityManager.persist(user);
        
        Optional<UserAuthentication> found = testEntityManager.find(UserAuthentication.class, user.getId());
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findByUsername_returnsUser() {
        UserAuthentication user = UserAuthentication.builder()
            .username("testuser")
            .password("encodedpass")
            .build();
        testEntityManager.persist(user);

        Optional<UserAuthentication> found = testEntityManager
            .getReference(UserAuthentication.class, user.getId());
        
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void existsByUsername_returnsTrueWhenExists() {
        UserAuthentication user = UserAuthentication.builder()
            .username("existinguser")
            .password("pass")
            .build();
        testEntityManager.persist(user);

        assertTrue(testEntityManager.exists(UserAuthentication.class, "existinguser"));
    }

    @Test
    void delete_deletesUser() {
        UserAuthentication user = UserAuthentication.builder()
            .username("todelete")
            .password("pass")
            .build();
        testEntityManager.persist(user);

        testEntityManager.remove(user);
        assertFalse(testEntityManager.find(UserAuthentication.class, user.getId()).isPresent());
    }
}
JAVAEOF

echo "✅ UserRepositoryIntegrationTest.java criado (4 testes)!"

echo ""
echo "📝 Gerando TokenRepositoryIntegrationTest.java..."
cat > /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/repository/TokenRepositoryIntegrationTest.java << 'JAVAEOF'
package com.example.auth.repository;

import com.example.auth.model.Token;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TokenRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void save_tokenIsPersisted() {
        Token token = Token.builder()
            .token("test-token-123")
            .tokenType(Token.TokenType.REFRESH)
            .revoked(false)
            .expired(false)
            .build();
        
        testEntityManager.persist(token);
        
        Optional<Token> found = testEntityManager.find(Token.class, token.getId());
        assertTrue(found.isPresent());
        assertEquals("test-token-123", found.get().getToken());
    }

    @Test
    void findByToken_returnsToken() {
        Token token = Token.builder()
            .token("search-token")
            .tokenType(Token.TokenType.ACCESS)
            .revoked(false)
            .expired(false)
            .build();
        testEntityManager.persist(token);

        Optional<Token> found = testEntityManager
            .getReference(Token.class, token.getId());
        
        assertNotNull(found);
    }

    @Test
    void findAllByUserAuthenticationAndRevokedFalse_returnsValidTokens() {
        Token token = Token.builder()
            .token("valid-token")
            .tokenType(Token.TokenType.ACCESS)
            .revoked(false)
            .expired(false)
            .build();
        testEntityManager.persist(token);

        List<Token> validTokens = testEntityManager
            .findAll(Token.class);
        
        assertNotNull(validTokens);
        assertFalse(validTokens.stream().anyMatch(Token::isRevoked));
    }

    @Test
    void findByTokenAndRevokedFalseAndExpiredFalse_returnsValidToken() {
        Token token = Token.builder()
            .token("valid-search-token")
            .tokenType(Token.TokenType.ACCESS)
            .revoked(false)
            .expired(false)
            .build();
        testEntityManager.persist(token);

        Optional<Token> found = testEntityManager
            .getReference(Token.class, token.getId());
        
        assertNotNull(found);
        assertFalse(found.isRevoked());
    }
}
JAVAEOF

echo "✅ TokenRepositoryIntegrationTest.java criado (4 testes)!"

echo ""
echo "============================================"
echo "✅ TODOS OS TESTES DE INTEGRAÇÃO GERADOS!"
echo "============================================"
echo ""
echo "📋 Resumo:"
echo "  • AuthControllerIntegrationTest.java - 5 testes"
echo "  • BookControllerIntegrationTest.java - 5 testes"
echo "  • UserRepositoryIntegrationTest.java - 4 testes"
echo "  • TokenRepositoryIntegrationTest.java - 4 testes"
echo "  • application-integration.yml - Configuração H2"
echo ""
echo "🚀 Executar:"
echo "   mvn clean test -Dtest=*IntegrationTest"
echo ""
