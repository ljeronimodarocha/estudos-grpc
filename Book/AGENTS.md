# AGENTS.md Plan

## 1) Setup Commands
- `mvn clean install` - Build and install dependencies
- `mvn dependency:resolve` - Resolve any dependency issues
- `mvn spring-boot:run` - Run the Spring Boot application
- `export AGENTS_DIR=/mnt/1TB/desenvolvimento/estudo` - Set environment variable

## 2) Testing Workflow
- `mvn test` - Run all tests
- `mvn test -Dtest=YourTestClass` - Run specific test class
- `mvn -Dtest=YourTestClass` - Run specific test class
- `mvn surefire-report:report` - Generate test report
- `eslint --ext .java src/` - Lint code non-interactively
- `prettier --write src/` - Format code

## 3) Repo-Specific Rules
- Use imperative commit messages (e.g., "fix: bug in agent logic")
- Follow Git Flow: `git flow init` for branching
- Use 2-space indentation with 80-char line limits
- Never commit secrets - use `git commit -m "ci: update agents"` for CI commits
- Use `git diff --no-pager` for review

### Java Specific:
- Use 4-space indentation for Java files
- Follow Java code style conventions
- Use Maven for dependency management

### gRPC Configuration
- Ensure protobuf files are in `src/main/proto`
- Use `mvn protobuf:compile` to generate code
- Configure `application.properties` for gRPC endpoints