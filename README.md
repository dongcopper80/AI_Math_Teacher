# Nobita — AI Math Teacher

A Vietnamese math tutoring MVP for Tiểu học (grades 1–5), THCS (6–9), and THPT (10–12). The classroom supports Vietnamese and English lessons, step-by-step narration and available browser voices.

## Start the full stack

1. Copy `.env.example` to `.env` and change the database and storage passwords.
2. Start Docker Desktop and wait for its engine to be ready.
3. From this folder, run `docker compose up -d --build`.
4. Open http://localhost:3000. Backend health: http://localhost:8080/api/v1/health. S3 storage endpoint: http://localhost:8333.

Stop the services with `docker compose down`; stored data stays in named volumes. To also delete stored data, run `docker compose down -v`.

## Database migrations and initial data

Flyway runs versioned SQL migrations automatically when the backend starts. `V1` creates the lesson and education-level schema; `V2` seeds Tiểu học, THCS, and THPT and adds the lesson-to-level reference. Existing databases without Flyway history are baselined at version `0`, so these migrations still run against the current schema. The seed uses `ON CONFLICT DO NOTHING`, and legacy lessons with a missing or invalid education level are assigned from their grade where possible. Hibernate is set to `validate`; schema changes should be added as a new `V<number>__description.sql` migration rather than applied automatically.

## Local development

Start dependencies with `docker compose up -d postgres redis seaweedfs`. In a new terminal, run `cd backend && mvn spring-boot:run`. In another terminal run `cd web && npm install && npm run dev`. Visit http://localhost:3000.

The app uses the MinIO Java SDK against an S3-compatible local endpoint. MinIO's previously referenced public images are no longer anonymously pullable, so the runnable Compose stack uses SeaweedFS for local S3 storage. The service can be switched to any compatible S3 provider through `APP_STORAGE_ENDPOINT`, credentials and bucket variables.

## AI provider

The default `AI_PROVIDER=demo` returns a sample Vietnamese lesson without an API key. To generate a grade-appropriate, classroom-style solution online, copy `.env.example` to `.env`, set `AI_PROVIDER=openai`, add `AI_API_KEY`, and select “Tìm cách giải bằng AI online” in the web form. `AI_BASE_URL` defaults to `https://api.openai.com/v1`; `AI_MODEL` defaults to `gpt-4o-mini`. API credentials stay on the backend. Lessons can be generated in Vietnamese or English. AI mode accepts typed text or JPG/PNG/WEBP photos. For photos, the vision-capable model reads the exercise, returns the recognized text for review, and uses it to build the lesson; keep “Tìm cách giải bằng AI online” enabled. Image and problem text are sent to the configured AI provider only in this mode. If AI mode is selected without provider credentials, the app shows an error instead of silently using a demo lesson.

## Modules

- `web/`: responsive Next.js/TypeScript grade selection, text and image input, lesson board and narration controls.
- `backend/.../lesson`: lesson API, domain and PostgreSQL persistence.
- `backend/.../ai`: provider interface and deterministic demo provider.
- `backend/.../storage`: S3-compatible object storage integration.
- Redis is provisioned for future sessions, caching and lesson queues.

## API

- `GET /api/v1/health` — health check.
- `POST /api/v1/lessons` — multipart form fields `educationLevel` (`primary`, `middle`, or `high`), `grade` (1–5, 6–9, or 10–12 for the selected group), optional `problemText`, optional `image` (max 10 MB). Returns lesson title, concept, steps, answer and practice.

## MVP boundaries

The demo provider returns a fixed sample lesson and does not solve arbitrary problems. Uploaded photos are stored; AI vision OCR requires the configured provider/model to support image inputs. Browser speech depends on Vietnamese voices available on the device. The lesson screen offers available Vietnamese voices and a brighter, youthful pitch/rate; exact voice options vary by device. Connect a vision-capable provider for OCR and grade-appropriate lesson generation. The demo provider uses an elementary subtraction example for Tiểu học, a linear equation for THCS, and a derivative example for THPT.

