# Aahar 🍱

Aahar is a full-stack AI-powered nutrition and calorie tracking application that helps users log meals, track dietary goals, and manage nutrition intelligently.

Built using:

* Spring Boot (Backend)
* React + Tailwind CSS (Frontend)
* JWT Authentication
* Google OAuth Login
* PostgreSQL/MySQL
* AI-powered meal recognition APIs

---

## Features

### Authentication

* User registration and login
* JWT-based authentication
* Google OAuth login
* Secure protected routes

### Nutrition Tracking

* Log meals and calories
* Search food database with autocomplete
* Track diet history
* Personalized dietary goals

### User Profile

* Age, weight, height tracking
* Activity level management
* Goal-based calorie calculations
* Profile completion workflow

### Modern UI

* Responsive design
* Tailwind CSS styling
* Animated modal interactions
* Mobile-friendly experience

---

# Tech Stack

## Frontend

* React
* Vite
* Tailwind CSS
* Axios

## Backend

* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate / JPA

## Database

* PostgreSQL / MySQL

## Authentication

* Google OAuth 2.0
* BCrypt password hashing

---

# Project Structure

```bash
Aahar/
│
├── frontend/        # React frontend
├── backend/         # Spring Boot backend
│
├── README.md
```

---

# Environment Variables

## Backend (`application.properties`)

```properties
spring.datasource.url=YOUR_DB_URL
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

jwt.secret=YOUR_SECRET_KEY

google.client-id=YOUR_GOOGLE_CLIENT_ID

app.allowed-origins=http://localhost:5173
```

---

## Frontend (`.env`)

```env
VITE_API_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
```

---

# Installation

## Clone the repository

```bash
git clone https://github.com/Pankaj-0001/Aahar.git
cd Aahar
```

---

# Backend Setup

```bash
cd backend
```

Run the Spring Boot server:

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

Backend runs on:

```bash
http://localhost:8080
```

---

# Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on:

```bash
http://localhost:5173
```

---

# API Endpoints

## Authentication

| Method | Endpoint                     | Description           |
| ------ | ---------------------------- | --------------------- |
| POST   | `/api/auth/register`         | Register user         |
| POST   | `/api/auth/login`            | Login user            |
| POST   | `/api/auth/google`           | Google OAuth login    |
| PUT    | `/api/auth/complete-profile` | Complete user profile |

---

# Security

* JWT token authentication
* BCrypt password encryption
* Stateless session management
* Protected API routes
* CORS configuration support

---

# Future Improvements

* AI image-based food recognition
* Daily nutrition analytics
* Meal recommendations
* Macro tracking
* Social/community features
* Dark mode

---



# Author

Pankaj

GitHub:
https://github.com/Pankaj-0001

---
