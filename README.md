# SilverCare

A lightweight, multilingual vital-sign tracking platform designed for immigrant families. Built from the developer’s own caregiving experiences, SilverCare lets family members record an elder’s profile and language preference, then log daily vital signs. Elders and families can review 7–30 day trends for temperature, blood pressure, blood glucose, and more, reducing anxiety about day-to-day health.

Current languages: English, Chinese, Vietnamese, Spanish.
Active development: Medication Reminders page.

---

## 1. Project Overview

SilverCare focuses on simple, reliable communication between elders, family, and caregivers. The app supports:

* Multilingual UI so elders can read their own data comfortably
* Role-based access (Admin, Family, Caregiver)
* Vital-sign logging and trend charts
* Care recipient management
* Medication reminders (in progress)

Why it exists: immigrant households often navigate multiple languages and low-tech preferences. SilverCare aims to make basic health tracking understandable and shareable without clinical complexity.

---

## 2. Tech Stack

Frontend

* Vue 2, Vuetify
* Axios, Vue Router, Vue i18n
* Build tooling: Vue CLI with Webpack

Backend

* Spring Boot 3.x, Maven, Java 17+
* JWT authentication and authorization
* RESTful APIs under /api

Database

* MySQL 8.x

---

## 3. Features

Authentication

* Register, Login, JWT stored in browser (localStorage token)

Multilingual UI

* English, Chinese, Vietnamese, Spanish
* Language preference saved per user or per care recipient

Care Recipient Management

* Create, view, edit, activate/deactivate care recipients
* Store name, contact, age, address, and language preference

Vital Signs

* Log temperature, blood pressure, blood glucose, heart rate, etc.
* View 7–30 day charts and summaries

Medication Reminders

* Create and manage reminders per recipient
* Page and APIs under active development

Roles and Access Control

* Admin, Family, Caregiver with scoped permissions

---

## 4. Project Structure

Monorepo example layout:

```
silvercare/
├─ Silvercare_backend/
│  └─ Silvercarebackend/
│     ├─ src/main/java/com/silvercare/silvercarebackend/
│     │  ├─ config/           # Security, CORS, JWT filters
│     │  ├─ controller/       # REST controllers
│     │  ├─ domain/           # Entities
│     │  ├─ dto/              # Request/response DTOs
│     │  ├─ repository/       # Spring Data JPA repos
│     │  └─ service/          # Business logic
│     └─ src/main/resources/
│        ├─ application.yml
│        └─ db/migration/     # Optional: Flyway migrations if present
│
└─ Silvercare_frontend/
   └─ src/
      ├─ views/
      │  ├─ CareRecipientView.vue
      │  ├─ VitalSignsView.vue
      │  ├─ ReminderFormView.vue   # in progress
      │  ├─ LoginView.vue
      │  └─ LoginHomePageView.vue
      ├─ services/
      │  └─ api.js                 # Axios instance (baseURL points to backend)
      ├─ router/
      │  └─ index.js               # Route defs (/login, /app, /reminder, etc.)
      ├─ i18n/
      │  ├─ en.json
      │  ├─ zh.json
      │  ├─ vi.json
      │  └─ es.json
      └─ assets/ components/ store/ utils/ ...
```

Notes

* Backend serves JSON responses with a nested envelope. Some endpoints currently return data as `res.data.data.data`. The frontend uses that path until the envelope is unified.

---

## 5. Local Setup

Prerequisites

* Java 17+
* Maven 3.9+
* Node.js LTS and npm
* MySQL 8.x running locally

Backend

1. Configure database in `application.yml`:

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/silvercare?useSSL=false&serverTimezone=UTC
    username: your_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update   # or validate; prefer Flyway if migrations exist
    open-in-view: false
server:
  port: 8081
```

2. Run:

```
cd Silvercare_backend/Silvercarebackend
mvn spring-boot:run
```

Frontend

1. Verify API base URL in `src/services/api.js`:

```js
const api = axios.create({
  baseURL: "http://localhost:8081/api",
  headers: { "Content-Type": "application/json" }
});
```

2. Install and start:

```
cd Silvercare_frontend
npm install
npm run serve
```

3. Open the app at `http://localhost:8080`

Authentication

* On successful login or register, a JWT token is saved to `localStorage` as `token`
* Axios interceptor attaches `Authorization: Bearer <token>`

---

## 6. API Examples

Register

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "emma_ok",
  "email": "emma_ok@example.com",
  "password": "123456",
  "role": "FAMILY",
  "languagePreference": "en"
}
```

Success response:

```
{
  "token": "<jwt>",
  "username": "emma_ok",
  "role": "FAMILY",
  "message": "auth.success",
  "preferredLanguage": "en"
}
```

Login

```
POST /api/auth/login
{
  "username": "emma_ok",
  "password": "123456"
}
```

Care Recipients

```
GET /api/care-recipients
Authorization: Bearer <jwt>

Response (current envelope):
{
  "success": true,
  "message": null,
  "data": {
    "data": [ { "id": 5, "fullName": "Liu Xia", ... } ],
    "success": true
  }
}
```

Frontend consumption:

```js
const rows = res.data?.data?.data || [];
```

Vital Signs

```
GET /api/vital-signs/{recipientId}
Authorization: Bearer <jwt>
```

Medication Reminders

```
GET /api/reminders?recipientId={id}
POST /api/reminders
PATCH /api/reminders/{id}
DELETE /api/reminders/{id}
```

Note: reminder endpoints and UI are under active development.

---

## 7. Roles and Access Control

Admin

* Full access to manage users, recipients, and system configuration

Family

* Manage their own recipients and view their vital data
* Create medication reminders for their recipients

Caregiver

* View and update vital signs for assigned recipients
* May create reminders depending on policy set by Admin

Authorization is enforced with JWT and Spring Security. Endpoints require appropriate roles; unauthorized requests return 401/403.

---

## 8. Localization

Language files

* Frontend translations live in `src/i18n/` as simple JSON dictionaries per locale

Adding a new language

1. Create `src/i18n/<locale>.json`
2. Register the locale in the i18n setup file
3. Provide translations for all required keys
4. Optional: add the language to the user or recipient preference selectors

Per-recipient language

* Recipient profiles store language preference so UI text and simple labels can be shown in that language for family/caregiver workflows

---

## 9. Development Notes

Ports

* Backend: 8081
* Frontend: 8080

Axios interceptor

* `src/services/api.js` adds `Authorization` header when `localStorage.token` exists

Response envelope

* Several endpoints return nested `data.data`. Frontend currently accesses `res.data.data.data`. This will be refactored to a single, consistent envelope.

CORS

* Ensure allowed origins include `http://localhost:8080` during local development

Common fixes

* Clear browser cache and `localStorage` if roles or tokens change
* If you see `Http403ForbiddenEntryPoint`, re-authenticate and confirm the user’s role
* Verify that frontend routes and backend endpoints match (`/api/vital-signs` vs `/api/vitals`)

Database

* For quick local setup, start MySQL and create a `silvercare` schema
* Prefer Flyway migrations if present under `src/main/resources/db/migration`

Testing with curl

```
TOKEN=<paste_jwt_here>
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/care-recipients
```

Roadmap

* Finish Medication Reminders UI and API workflows
* Normalize API response envelope
* Expand analytics: weekly summaries and trend insights
* Add notification channels and scheduling
* Add more locales and accessibility improvements

---

## License

Personal/educational project. Licensing to be defined as the product matures.
