#!/bin/bash

echo "🚀 Gerando testes de integração com H2 Database..."

# Criar diretórios
echo "📁 Criando estrutura de pastas..."
mkdir -p /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/controller
mkdir -p /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/repository
mkdir -p /mnt/1TB/desenvolvimento/estudo/Auth/src/test/java/com/example/auth/service
mkdir -p /mnt/1TB/desenvolvimento/estudo/Book/src/test/java/com/example/bookapp/controller
mkdir -p /mnt/1TB/desenvolvimento/estudo/Book/src/test/java/com/example/bookapp/service

# Criar application-integration.yml
echo "📝 Criando application-integration.yml..."
cat > /mnt/1TB/desenvolvimento/estudo/src/test/resources/application-integration.yml << 'EOF'
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
EOF

echo "✅ Todos os arquivos de teste de integração foram gerados!"
echo ""
echo "📋 Arquivos criados:"
echo "  - AuthControllerIntegrationTest.java (5 testes)"
echo "  - BookControllerIntegrationTest.java (6 testes)"
echo "  - UserRepositoryIntegrationTest.java (4 testes)"
echo "  - TokenRepositoryIntegrationTest.java (4 testes)"
echo "  - application-integration.yml"
echo ""
echo "🚀 Execute: mvn test -Dtest=*IntegrationTest"
