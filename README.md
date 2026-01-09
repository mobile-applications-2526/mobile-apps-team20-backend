# 🎓 CampusConnect Backend

A robust Spring Boot backend API for the CampusConnect mobile application, enabling students to connect, create events, and participate in group chats.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Development](#development)

## 🌟 Overview

CampusConnect is a campus social networking platform that provides REST APIs for user authentication, event management, real-time chat functionality, and user connections. The backend is built with Spring Boot and uses PostgreSQL for persistent storage and Redis for caching and session management.

## ✨ Features

- **Authentication & Authorization**
  - Google OAuth2 authentication
  - Email-based authentication with verification codes
  - JWT token-based security
  - Refresh token mechanism
  - Account activation via email

- **Event Management**
  - Create and manage campus events
  - Event participation system
  - Event-based group chats

- **Real-time Communication**
  - WebSocket support for instant messaging
  - Event-based chat rooms
  - STOMP protocol implementation

- **User Management**
  - User profiles and connections
  - Email verification system
  - Secure password handling

## 🛠️ Tech Stack

**Backend Framework:** Spring Boot 3.5.6 with Java 21

**Databases:**
- PostgreSQL (Primary database)
- Redis (Caching & session management)

**Security:**
- Spring Security
- JWT (JSON Web Tokens)
- Google OAuth2

**Communication:**
- REST APIs
- WebSockets (STOMP)
- Spring Mail

**Documentation:**
- SpringDoc OpenAPI (Swagger)

**Other Technologies:**
- Lombok (Code generation)
- Docker & Docker Compose
- Maven

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK 21** or higher
- **Docker** and **Docker Compose**
- **Maven** (or use the included Maven wrapper)
- **Git**

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone [https://github.com/Juangmz7/mobile-server.git](https://github.com/Juangmz7/mobile-server.git)
cd mobile-server
```

### 2. Create Environment File

Create a `.env` file in the root directory with the following variables:

```env
# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Redis Configuration
REDIS_PORT=6379

# Email Configuration (Gmail)
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Security Keys
ENCRYPT_SECRET_KEY=your-encryption-secret-key-min-32-chars
JWT_SECRET_KEY=your-jwt-secret-key-min-32-chars

# Account Activation
ACTIVATE_ACCOUNT_URL=http://localhost:8080/api/auth/activate-account

# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 3. Start the Application with Docker Compose

The updated Docker Compose configuration orchestrates all services:

```bash
docker-compose up -d
```

This will start:
- **app**: The Spring Boot application
- **postgres**: PostgreSQL database (exposed on port 5433)
- **redis**: Redis cache server (exposed on port 6379)

### 4. Verify the Application

Once all containers are running, verify the application:

```bash
# Check container status
docker-compose ps

# View application logs
docker-compose logs -f app
```

The application should be available at `http://localhost:8080`

### 5. Access API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

## 🔐 Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SERVER_PORT` | Port for the Spring Boot application | `8080` |
| `DB_USERNAME` | PostgreSQL username | `campusconnect` |
| `DB_PASSWORD` | PostgreSQL password | `securepassword123` |
| `DB_URL` | Database connection URL (auto-configured in Docker) | `jdbc:postgresql://postgres:5432/CampusConnect` |
| `REDIS_HOST` | Redis host (auto-configured in Docker) | `redis` |
| `REDIS_PORT` | Redis port | `6379` |
| `MAIL_PORT` | SMTP port for email service | `587` |
| `MAIL_USERNAME` | Gmail account for sending emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Gmail app password | `your-app-password` |
| `ENCRYPT_SECRET_KEY` | Key for data encryption (min 32 chars) | Generated secret key |
| `JWT_SECRET_KEY` | Key for JWT token signing (min 32 chars) | Generated secret key |
| `ACTIVATE_ACCOUNT_URL` | URL for account activation | `http://localhost:8080/api/auth/activate-account` |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | From Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | From Google Cloud Console |

### Setting up Gmail for Email Service

1. Enable 2-Factor Authentication on your Gmail account
2. Generate an App Password: [Google App Passwords](https://myaccount.google.com/apppasswords)
3. Use the generated app password as `MAIL_PASSWORD`

### Setting up Google OAuth2

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Copy the Client ID and Client Secret to your `.env` file

## 📚 API Documentation

### Authentication Endpoints (`/api/auth`)

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with email (sends verification code)
- `POST /api/auth/validate-code` - Validate email verification code
- `POST /api/auth/google` - Authenticate with Google OAuth2
- `GET /api/auth/activate-account` - Activate account via email link
- `POST /api/auth/refresh-token` - Refresh JWT token
- `POST /api/auth/logout` - Logout and invalidate tokens

### User Endpoints (`/api/user`)

User profile management and connections

### Event Endpoints (`/api/event`)

Event creation, management, and participation

### Chat Endpoints

- REST API endpoints for chat history
- WebSocket endpoints for real-time messaging

For complete API documentation, visit the Swagger UI at `http://localhost:8080/swagger-ui.html` when the application is running.

## 📁 Project Structure

```
mobile-server/
├── src/
│   ├── main/
│   │   ├── java/com/mbproyect/campusconnect/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── auth/            # Security & JWT configuration
│   │   │   │   ├── exceptions/      # Global exception handlers
│   │   │   │   └── websocket/       # WebSocket configuration
│   │   │   ├── controller/          # REST controllers
│   │   │   │   ├── auth/            # Authentication endpoints
│   │   │   │   ├── chat/            # Chat endpoints (REST & WebSocket)
│   │   │   │   ├── event/           # Event endpoints
│   │   │   │   └── user/            # User endpoints
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── events/              # Event handling
│   │   │   ├── infrastructure/      # External integrations
│   │   │   ├── model/               # Data models
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   └── enums/           # Enumerations
│   │   │   ├── service/             # Service interfaces
│   │   │   ├── serviceimpl/         # Service implementations
│   │   │   ├── shared/              # Shared utilities
│   │   │   └── CampusconnectApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Test files
├── docker-compose.yml               # Docker Compose configuration
├── pom.xml                          # Maven dependencies
├── mvnw / mvnw.cmd                  # Maven wrapper
└── README.md
```

## 💻 Development

### Running Locally (without Docker)

1. Ensure PostgreSQL and Redis are running locally or in Docker:

```bash
# Start only database services
docker-compose up -d postgres redis
```

2. Update your `.env` file with local database URLs:

```env
DB_URL=jdbc:postgresql://localhost:5433/CampusConnect
REDIS_HOST=localhost
```

3. Run the application:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Building the Application

```bash
# Windows
mvnw.cmd clean package

# Linux/Mac
./mvnw clean package
```

The compiled JAR will be in `target/CampusConnect.jar`

### Docker Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and restart
docker-compose up -d --build

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v
```

### Database Management

Access PostgreSQL:
```bash
docker exec -it <postgres-container-name> psql -U <DB_USERNAME> -d CampusConnect
```

Access Redis CLI:
```bash
docker exec -it redis-dev redis-cli
```

## 🔧 Troubleshooting

### Common Issues

**Port Already in Use:**
- Change `SERVER_PORT`, PostgreSQL port (5433), or Redis port (6379) in `.env` and `docker-compose.yml`

**Database Connection Failed:**
- Ensure PostgreSQL container is running: `docker-compose ps`
- Check credentials in `.env` match PostgreSQL environment variables

**Email Not Sending:**
- Verify Gmail credentials and app password
- Ensure 2FA is enabled on Gmail account
- Check firewall settings for port 587

**Google OAuth Not Working:**
- Verify redirect URI matches exactly
- Check Client ID and Secret are correct
- Ensure Google+ API is enabled in Google Cloud Console

## 📊 Database Schema

The application uses Hibernate with `ddl-auto=update`, so the schema is automatically created and updated based on the JPA entities.

## 🤝 Contributing

This is a school project, but contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project was created as part of a school project and is currently under active development.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- The open-source community for various dependencies used in this project
