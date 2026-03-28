# AchievementProject

A mobile-first web application for tracking personal milestones and competitive achievements across multiple users. Users earn badges for hitting thresholds, and certain "supremacy" achievements dynamically transfer to whoever currently holds the record.

---

## Features

- **Multi-user tracking** — multiple people participate in the same tasks and compete on a shared leaderboard
- **Threshold achievements** — permanently unlocked when a user's cumulative progress hits a fixed value (e.g. win 5 games)
- **Supremacy achievements** — dynamically held by the current leader, transferred when someone else overtakes them (requires a minimum threshold before activating)
- **Gendered badges** — each achievement has a sex (`MALE`, `FEMALE`, `UNISEX`) so badge images match the user
- **Achievement categories** — achievements are grouped by category (Sports, Board Games, Fitness, Creative, etc.) enabling cross-category meta-achievements
- **User authentication** — registration, login, JWT-based sessions with refresh tokens
- **Mobile-first UI** — designed for phone use first, works on desktop too

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Kotlin + Spring Boot (Gradle) |
| Frontend | Angular 17+ (standalone components, signals) |
| Database | PostgreSQL |
| Auth | Spring Security + JWT |
| Migrations | Flyway |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5 + Testcontainers |

---

## Project Structure

```
AchievementProject/
├── backend/
│   ├── domain/                  # Pure business logic, no framework deps
│   │   ├── model/               # Domain entities (plain Kotlin data classes)
│   │   ├── port/                # Repository interfaces (defined by domain, implemented by infra)
│   │   ├── service/             # Achievement engine, evaluation logic
│   │   ├── event/               # Domain events (AchievementUnlockedEvent, etc.)
│   │   ├── exception/           # Domain-specific exceptions
│   │   └── valueobject/         # Immutable value types (ProgressDelta, etc.)
│   ├── app/                     # Use case orchestration
│   │   ├── usecase/             # One class per user action (RecordProgressUseCase, etc.)
│   │   ├── dto/                 # Request/response objects
│   │   ├── mapper/              # Domain ↔ DTO conversion
│   │   ├── config/              # Spring wiring, security config, CORS
│   │   └── migrations/          # Flyway SQL migration files
│   └── infrastructure/          # Framework + I/O adapters
│       ├── web/                 # Spring MVC REST controllers
│       ├── persistence/         # JPA entities + repository implementations
│       ├── security/            # JWT filter, UserDetailsService, password encoding
│       ├── storage/             # Badge image storage (S3 / local)
│       ├── notification/        # Event listeners, notification records
│       └── event/               # Spring ApplicationEventPublisher adapter
└── frontend/
    ├── src/
    │   ├── app/
    │   │   ├── core/            # Auth interceptor, error interceptor, guards
    │   │   ├── features/        # Dashboard, tasks, achievements, profile
    │   │   └── shared/          # Reusable components, pipes, directives
    │   ├── environments/
    │   └── assets/
    └── ...
```

### Architecture rule

Dependencies flow inward only: **Infrastructure → App → Domain**. The domain module has zero framework dependencies and can be tested without Spring or a database.

---

## Domain Concepts

**Task** — a trackable activity (e.g. "Basketball wins") with a name, description, and unit label.

**Achievement** — a rule attached to a task that defines when it unlocks:
- `THRESHOLD` — unlocks permanently when cumulative progress ≥ a fixed value
- `SUPREMACY` — held by the current leader, but only activates once someone first crosses a minimum threshold. Transfers to a new leader if they overtake the current holder.

**Achievement sex** — `MALE`, `FEMALE`, or `UNISEX`. Used to serve the correct badge image for each user.

**Achievement category** — e.g. `SPORTS`, `BOARD_GAMES`, `FITNESS`, `CREATIVE`. Used for gallery grouping and cross-category meta-achievements.

---

## Getting Started

### Prerequisites

- JDK 17+
- Node.js 18+
- Docker + Docker Compose
- Gradle (or use the included wrapper)

### Local development

**1. Start the database**

```bash
docker compose up -d postgres
```

**2. Run the backend**

```bash
cd backend
./gradlew bootRun
```

The API starts on `http://localhost:8080`. Flyway migrations run automatically on startup.  
Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

**3. Run the frontend**

```bash
cd frontend
npm install
ng serve
```

The app opens at `http://localhost:4200`. API calls are proxied to `localhost:8080` via the Angular dev server proxy config.

---

## API Overview

All responses use a consistent envelope:

```json
{
  "data": {},
  "meta": {},
  "errors": null
}
```

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login, returns access + refresh tokens |
| `GET` | `/api/auth/me` | Current user profile |
| `GET/POST` | `/api/tasks` | List or create tasks |
| `GET` | `/api/tasks/{id}` | Task detail with participants |
| `POST` | `/api/tasks/{id}/participants` | Join a task |
| `POST` | `/api/tasks/{id}/progress` | Record progress (triggers achievement engine) |
| `GET` | `/api/tasks/{id}/leaderboard` | Sorted participant standings |
| `GET` | `/api/tasks/{id}/achievements` | Achievements grouped by category |
| `GET` | `/api/users/{id}/achievements` | All achievements held by a user |
| `GET` | `/api/achievements/categories` | List all categories |

---

## Achievement Engine

The engine runs inside a database transaction every time progress is recorded.

**Threshold evaluation:**
1. On progress recorded for `(task_id, user_id)`, find all `THRESHOLD` achievements where `threshold_value <= new_cumulative_total`
2. For each, check if the user already holds it — if not, award it permanently

**Supremacy evaluation:**
1. Find the participant with the highest cumulative total for the task
2. If their total `>= min_threshold`, they should hold the achievement
3. If the current holder is different, revoke the old holder's record and award it to the new leader
4. Ties — the current holder retains the achievement until the tie is broken

Both evaluations are atomic with the progress write. A failure in either rolls back everything.

---

## Implementation Roadmap

| Phase | Backend | Frontend |
|---|---|---|
| 1 | Project skeleton, DB schema, Flyway migrations | Project setup, design system, shell layout |
| 2 | Auth endpoints (register, login, JWT) | Auth screens (login, register, profile) |
| 3 | Tasks + progress CRUD | Dashboard, task detail, log progress |
| 4 | Achievement engine + unit tests | Achievement gallery, unlock animation |
| 5 | Badge uploads, notifications, cross-category achievements | Admin screen, PWA manifest, polish |

---

## Environment Variables

Create a `.env` file in `backend/` (never commit this):

```env
DB_URL=jdbc:postgresql://localhost:5432/achievementdb
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your-256-bit-secret
JWT_EXPIRY_MS=900000
REFRESH_TOKEN_EXPIRY_DAYS=7
STORAGE_TYPE=local        # or s3
```

---

## Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit with a clear message: `git commit -m "feat: add supremacy transfer logic"`
4. Push and open a pull request

---

## License

MIT
