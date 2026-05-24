# Health Tracker AI

An AI-powered full-stack nutrition intelligence platform designed for Indian dietary contexts. Users can log meals using natural language descriptions, receive AI-generated nutritional analysis, track daily macro goals, calculate diet quality scores, and generate weekly health insights.

Live Demo: https://healthtracker-1-33rt.onrender.com/

---

## Features

- JWT-based Authentication & Authorization
- AI-powered natural language meal analysis
- Personalized nutrition targets using Mifflin-St Jeor equation
- Diet quality scoring system
- Weekly nutrition reports with AI insights
- Indian food-aware nutrition tracking
- Responsive dashboard with macro progress visualization
- Food search and nutrition metadata system
- Secure REST APIs
- Layered backend architecture

---

## Tech Stack

### Backend
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA (Hibernate)
- JWT Authentication
- MySQL
- Maven

### Frontend
- React 18
- Vite
- Tailwind CSS
- Axios
- React Router DOM

### AI Integration
- OpenRouter API
- LLM-based nutrition estimation pipeline

---

## System Architecture

```text
Client (React Frontend)
        ↓
Spring Boot REST API
        ↓
Service Layer
        ↓
JPA/Hibernate
        ↓
MySQL Database

AI Nutrition Analysis
        ↓
OpenRouter API
```

---

## Core Functionalities

### Authentication Module
- User registration & login
- JWT token generation
- Stateless authentication
- Protected routes

### AI Meal Analysis
Users can log meals using natural language like:

```text
"2 rotis with dal and paneer"
```

The system:
1. Sends meal data to OpenRouter API
2. Parses AI-generated nutrition response
3. Calculates calories and macronutrients
4. Generates diet quality score
5. Stores dietary records

---

## Nutrition Target Formula

### Mifflin-St Jeor Equation

For Men:

```math
BMR = 10W + 6.25H - 5A + 5
```

For Women:

```math
BMR = 10W + 6.25H - 5A - 161
```

Where:
- W = Weight (kg)
- H = Height (cm)
- A = Age (years)

---

## Diet Score Formula

```math
Score = Σ(wi × min(1, ci / ti)) × 100
```

Where:
- wi = nutrient weight
- ci = consumed amount
- ti = target amount

Overconsumption penalties are applied automatically.

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login and receive JWT |
| GET | `/api/user/profile` | Get user profile |
| PUT | `/api/user/profile` | Update profile |
| POST | `/api/diet/analyze` | Analyze meals |
| GET | `/api/diet/records` | Fetch dietary history |
| GET | `/api/diet/weekly-report` | Generate weekly report |
| GET | `/api/food/search?q=` | Search food database |

---

## Security

Implemented using Spring Security.

Features:
- BCrypt password hashing
- JWT Bearer authentication
- Stateless API security
- Protected endpoints
- Role-based authorization

---

## Project Structure

### Backend

```text
src
 ┣ controller
 ┣ service
 ┣ repository
 ┣ entity
 ┣ dto
 ┣ security
 ┣ config
 ┣ util
 ┗ exception
```

### Frontend

```text
src
 ┣ pages
 ┣ components
 ┣ services
 ┣ hooks
 ┣ layouts
 ┗ utils
```

---

## Environment Variables

### Backend

Create a `.env` file or configure environment variables:

```env
OPENROUTER_API_KEY=your_api_key
JWT_SECRET=your_jwt_secret
DB_URL=jdbc:mysql://localhost:3306/health_tracker
DB_USER=root
DB_PASSWORD=password
```

---

## Installation

### Backend

```bash
git clone <repo-url>

cd backend

mvn clean install

mvn spring-boot:run
```

### Frontend

```bash
cd frontend

npm install

npm run dev
```

---

## Performance

| Metric | Result |
|---|---|
| Meal Analysis API | ~1.8s avg |
| Food Search API | ~180ms avg |
| Authentication | JWT Stateless |
| Architecture | Horizontally scalable |

---

## Future Improvements

- Barcode scanning
- Image-based food recognition
- Native mobile app
- Advanced micronutrient dashboard
- Sleep & exercise integration
- Personalized ML recommendation engine
- Meal planning system
- Social/community features

---

## Author

Pankaj Upadhyay

Backend-focused Full Stack Developer  
Java • Spring Boot • React • JWT • JPA • AI Integration
