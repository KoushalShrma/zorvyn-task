# Deployment guide

This project is configured for:

- Render for the backend
- Neon for PostgreSQL

## 1. Create the Neon database

1. Create a Neon project and a PostgreSQL database.
2. Copy the Neon JDBC connection details.
3. Build the JDBC URL in this format:

   `jdbc:postgresql://<host>/<database>?sslmode=require`

4. Keep the username and password from Neon private.

## 2. Set Render environment variables

Create a Render web service from this repository and set these environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `APP_BOOTSTRAP_ENABLED`
- `APP_BOOTSTRAP_VIEWER_USERNAME`
- `APP_BOOTSTRAP_VIEWER_PASSWORD`
- `APP_BOOTSTRAP_ANALYST_USERNAME`
- `APP_BOOTSTRAP_ANALYST_PASSWORD`
- `APP_BOOTSTRAP_ADMIN_USERNAME`
- `APP_BOOTSTRAP_ADMIN_PASSWORD`

Recommended values:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://<neon-host>/<neon-database>?sslmode=require`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `APP_BOOTSTRAP_ENABLED=true`

Render sets the port automatically through `PORT`, so you do not need to configure it manually.

## 3. Decide whether to seed demo users

If `APP_BOOTSTRAP_ENABLED=true`, the application will seed the demo users and sample records on first start.

If you do not want demo data in production, leave `APP_BOOTSTRAP_ENABLED=false` and create your own users another way.

## 4. Local Docker Compose

For local testing with Docker Compose, create a private `.env` file with:

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- optional bootstrap variables if you want the sample data

Then run:

```bash
docker compose up --build
```

## 5. Verification

After deployment, open the Render service URL and confirm the backend starts without datasource errors.

The Swagger UI is available at:

- `/swagger-ui.html`