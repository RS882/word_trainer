# 🌍 Word Trainer

Language Learning Platform built with Java and Spring Boot.

Word Trainer is a backend system designed to help users learn, manage, and practice foreign language vocabulary through structured lexeme management, translations, learning progress tracking, and personalized learning workflows.

The project demonstrates practical experience in backend architecture, authentication, domain modeling, REST API design, auditing, validation, and educational software development.

---

# 📖 Overview

Word Trainer serves as the core backend for a language learning platform.

The system manages:

* User accounts
* Lexemes
* Translations
* Learning progress
* Language-specific content
* Vocabulary statistics
* Authentication and authorization

The platform focuses on helping users build vocabulary efficiently while tracking learning progress over time.

---

# 🚀 Core Features

## 👤 User Management

The platform provides secure user account management.

Capabilities:

* Registration
* Authentication
* Profile management
* Learning history tracking

---

## 🔐 Authentication & Authorization

Secure authentication is implemented using JWT-based security.

Features:

* Access Tokens
* Refresh Tokens
* Protected APIs
* Stateless authentication

Benefits:

* Scalable architecture
* Reduced server-side session management
* Secure API access

---

## 📚 Vocabulary Management

Users can manage language learning content through lexemes and translations.

Capabilities:

* Create lexemes
* Update lexemes
* Delete lexemes
* Retrieve lexemes
* Organize vocabulary

The platform treats vocabulary as a first-class domain entity.

---

## 🌐 Translation Management

Lexemes can be linked with multiple translations.

Benefits:

* Multi-language support
* Flexible vocabulary relationships
* Improved learning experience

---

## 📈 Learning Progress Tracking

The platform tracks user interaction with vocabulary.

Capabilities:

* Learning status tracking
* User progress monitoring
* Vocabulary statistics
* Personalized learning data

This creates the foundation for adaptive learning workflows.

---

## 🏷 Multi-Language Support

The system supports multiple languages through dedicated language management mechanisms.

Examples:

* English
* German
* Russian
* Ukrainian

The architecture allows future language expansion.

---

## 📊 Learning Analytics

User learning results are stored and analyzed.

Benefits:

* Progress visibility
* Learning insights
* Vocabulary retention tracking
* Personalized recommendations (future enhancement)

---

## 📁 Bulk Vocabulary Import

The platform supports importing vocabulary data.

Benefits:

* Faster content creation
* Simplified vocabulary management
* Improved scalability

---

# 🏗 Architecture

```text
Client
   │
   ▼
REST API
   │
   ▼
Business Layer
   │
   ├── User Management
   ├── Lexeme Management
   ├── Translation Management
   ├── Learning Progress
   │
   ▼
Persistence Layer
   │
   ▼
Database
```

The architecture follows a layered design with clear separation between API, business logic, and persistence concerns.

---

# 📚 Domain Model

Core business entities:

## User

Represents a learner using the platform.

---

## Lexeme

Represents a vocabulary unit.

---

## Translation

Represents one or more translations associated with a lexeme.

---

## UserLexemeResult

Stores learning progress and user-specific vocabulary statistics.

---

# 🔒 Security Design

Implemented security mechanisms:

## JWT Authentication

Stateless user authentication.

---

## Refresh Token Flow

Secure session continuation without repeated login.

---

## Protected Endpoints

Authenticated access to user-specific resources.

---

## Custom Authentication Components

Dedicated authentication infrastructure enables flexible access control and security integration.

---

# 📝 Auditing

The platform includes auditing capabilities.

Features:

* Entity change tracking
* Revision history
* Audit metadata

Benefits:

* Traceability
* Data history visibility
* Easier troubleshooting

The implementation uses Hibernate Envers for entity auditing.

---

# ⚙ Technology Stack

## Backend

* Java
* Spring Boot
* Spring MVC

## Security

* Spring Security
* JWT

## Persistence

* Spring Data JPA
* Hibernate
* Hibernate Envers

## Documentation

* OpenAPI
* Swagger

## Validation

* Jakarta Validation

## Testing

* JUnit 5
* Mockito

---

# 🧪 Testing Strategy

The project contains tests covering:

### Business Logic

* Vocabulary operations
* Translation management
* Learning workflows

### Security

* Authentication logic
* Token processing

### Validation

* Request validation
* Business rule validation

### REST APIs

* Controller testing
* API behavior verification

Testing tools:

* JUnit 5
* Mockito
* Spring Boot Test

---

# 📊 Design Highlights

The project demonstrates practical experience with:

* Domain-Driven Design concepts
* JWT Authentication
* REST API Design
* DTO Mapping
* Entity Auditing
* Validation Frameworks
* Educational Software Development
* Multi-Language Data Modeling

---

# 🤔 Why This Design?

### Why Separate Lexemes and Translations?

Vocabulary and translations evolve independently.

This separation provides:

* Better flexibility
* Improved maintainability
* Support for multiple translations
* Easier language expansion

---

### Why Track User Learning Progress?

Learning platforms become significantly more valuable when they can track progress.

Benefits:

* Personalized learning
* Progress visibility
* Statistical insights
* Future recommendation systems

---

### Why JWT Authentication?

JWT enables scalable stateless authentication.

Benefits:

* Reduced server-side state
* Better scalability
* Standardized security model

---

### Why Hibernate Envers?

Educational data changes over time.

Auditing provides:

* Revision history
* Data traceability
* Improved debugging
* Better transparency

---

### Why Build This Project?

The goal was to gain practical experience with:

* Backend development
* Authentication systems
* Educational platforms
* Domain modeling
* API design
* Data persistence

while solving a real-world language learning problem.

---

# 🚀 Future Improvements

Planned enhancements:

* Spaced Repetition Algorithms
* Learning Recommendations
* Gamification
* AI-assisted Vocabulary Generation
* Pronunciation Support
* Advanced Analytics
* Mobile Integration

---

# 👨‍💻 Author

**Ruslan Senkin**

Java Backend Developer

Specialization:

* Java
* Spring Boot
* Spring Security
* REST APIs
* Domain Modeling
* Educational Software
* Cloud-Native Development
* Backend Architecture
