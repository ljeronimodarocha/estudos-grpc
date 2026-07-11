## AGENTS.md

### Commands
- `mvn install` for building dependencies
- `mvn test` to run all tests
- `mvn clean` to clean build artifacts
- `mvn package` for packaging application

### Test Execution
- Run tests: `mvn test`
- Coverage: `mvn jacoco:report` (if configured)
- Focus on module: `mvn test -pl user` (replace 'user' with module name)

### Monorepo Structure
- `User/`: Spring Boot application module with all about users
- `Book/`: Spring Boot application module with all about Books
- `Auth/`: Spring Boot application module with security and authentication module
- `contratos-grpc/`: gRPC contract definitions and code generation

### Framework Quirks
- Maven-based build system
- Shared parent pom: `sistema-grpc-parent`
- Java version: 26
- Spring Boot version: 4.1.0
- gRPC integration in contratos-grpc module

### Testing Conventions
- Unit tests: Maven Surefire plugin
- gRPC tests: Requires `mvn generate-sources` for proto files
- Integration tests: May require running `mvn test` with specific profiles

### Repo-Specific Conventions
- Commit messages: conventional commits
- PRs must have tests
- Code formatting: Prettier (if configured)
- Parent pom version: 1.0.0
- Java version: 26
- Spring Boot version: 4.1.0