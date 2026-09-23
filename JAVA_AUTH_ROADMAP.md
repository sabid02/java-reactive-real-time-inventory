# Reactive Java + Spring WebFlux + Reactive MongoDB Authentication: Step-by-Step Learning Guide

Welcome to your step-by-step journey to building a production-ready **Reactive Authentication System** in Java using **Spring Boot 3 (WebFlux)**, **Spring Security 6 (Reactive)**, **Project Reactor (`Mono` / `Flux`)**, **Reactive MongoDB**, and **JWT**.

> 💡 **Methodology**: We proceed **1 File Per Instruction / Step** so you can learn, build, compile, and understand every single component interactively!

---

## 📌 Master File Creation Sequence (Reactive Stack)

| Step | File Path | Purpose | Status |
| :---: | :--- | :--- | :---: |
| **01** | [`settings.gradle`](file:///c:/Users/user/Documents/sabid/personal-projects/java-learning/settings.gradle) | Defines the project name and multi-project hierarchy | ✅ Created |
| **02** | [`build.gradle`](file:///c:/Users/user/Documents/sabid/personal-projects/java-learning/build.gradle) | Declares dependencies (WebFlux, Reactive MongoDB, Security, JWT, Lombok) | ✅ Updated to Reactive |
| **03** | [`src/main/resources/application.yml`](file:///c:/Users/user/Documents/sabid/personal-projects/java-learning/src/main/resources/application.yml) | Configures port, Reactive MongoDB connection URI, and JWT secrets | ✅ Created & Explained |
| **04** | [`src/main/java/com/example/auth/AuthApplication.java`](file:///c:/Users/user/Documents/sabid/personal-projects/java-learning/src/main/java/com/example/auth/AuthApplication.java) | Main reactive application entry point (`public static void main`) | ✅ Created & Explained |
| **05** | `src/main/java/com/example/auth/model/Role.java` | Enum defining authorization roles (`ROLE_USER`, `ROLE_ADMIN`) | ⏳ Pending |
| **06** | `src/main/java/com/example/auth/model/User.java` | Reactive MongoDB Document representing a user | ⏳ Pending |
| **07** | `src/main/java/com/example/auth/repository/UserRepository.java` | `ReactiveMongoRepository` interface for non-blocking database access (`Mono`/`Flux`) | ⏳ Pending |
| **08** | `src/main/java/com/example/auth/dto/request/RegisterRequest.java` | Request body DTO with input validations for registration | ⏳ Pending |
| **09** | `src/main/java/com/example/auth/dto/request/LoginRequest.java` | Request body DTO with input validations for login | ⏳ Pending |
| **10** | `src/main/java/com/example/auth/dto/response/ApiResponse.java` | Standardized wrapper for reactive API responses | ⏳ Pending |
| **11** | `src/main/java/com/example/auth/dto/response/AuthResponse.java` | Response DTO returning JWT token and user info | ⏳ Pending |
| **12** | `src/main/java/com/example/auth/exception/UserAlreadyExistsException.java` | Custom runtime exception for duplicate registration | ⏳ Pending |
| **13** | `src/main/java/com/example/auth/exception/GlobalExceptionHandler.java` | Reactive `@RestControllerAdvice` converting errors to clean JSON | ⏳ Pending |
| **14** | `src/main/java/com/example/auth/security/JwtUtils.java` | Utility class to generate, sign, parse, and validate JWT tokens | ⏳ Pending |
| **15** | `src/main/java/com/example/auth/security/UserDetailsImpl.java` | Bridge between `User` document and Spring Security's `UserDetails` | ⏳ Pending |
| **16** | `src/main/java/com/example/auth/security/ReactiveUserDetailsServiceImpl.java` | Implements `ReactiveUserDetailsService` returning `Mono<UserDetails>` from MongoDB | ⏳ Pending |
| **17** | `src/main/java/com/example/auth/security/JwtAuthenticationManager.java` | Reactive `ReactiveAuthenticationManager` validating JWT tokens | ⏳ Pending |
| **18** | `src/main/java/com/example/auth/security/SecurityContextRepository.java` | Reactive `ServerSecurityContextRepository` extracting Bearer token from HTTP headers | ⏳ Pending |
| **19** | `src/main/java/com/example/auth/config/SecurityConfig.java` | Configures `ServerHttpSecurity` filter chain, password encoder (BCrypt), CORS, and CSRF | ⏳ Pending |
| **20** | `src/main/java/com/example/auth/service/AuthService.java` | Reactive business logic returning `Mono<AuthResponse>` for register & login | ⏳ Pending |
| **21** | `src/main/java/com/example/auth/controller/AuthController.java` | Reactive REST Controller returning `Mono<ResponseEntity<ApiResponse<AuthResponse>>>` | ⏳ Pending |
| **22** | `src/main/java/com/example/auth/controller/TestController.java` | Protected reactive test endpoints for public, user, and admin access | ⏳ Pending |

---

## ⚡ What Makes This Stack Reactive?

1. **Non-Blocking Architecture**: Uses Netty server instead of Tomcat threads. Requests do not block waiting for I/O.
2. **Project Reactor (`Mono` & `Flux`)**:
   - `Mono<T>`: Emits 0 or 1 item asynchronously (e.g. finding 1 user or returning a single auth response).
   - `Flux<T>`: Emits 0 to N items asynchronously (e.g. streaming a list of users).
3. **Reactive MongoDB (`ReactiveMongoRepository`)**: Non-blocking database queries returning `Mono` or `Flux`.
4. **Reactive Spring Security**: Uses `ServerHttpSecurity`, `ReactiveUserDetailsService`, and `ServerSecurityContextRepository` for asynchronous authentication filter chains.
