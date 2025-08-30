# Challenge – Farmatodo (Spring Boot, Hexagonal, API Key)

Proyecto de referencia **Spring Boot 3** con arquitectura **Hexagonal** para: clientes, productos, tokenización de tarjetas, carrito, pedidos y pagos; con **autenticación por API Key**, **notificaciones por correo**, **logs centralizados** (traceId/txId) y **tests** con cobertura.

## 🧱 Arquitectura (paquetes)
```
com.farmatodo.challenge
├─ adapters
│  ├─ in/web (controllers/dto)
│  └─ out
│     ├─ persistence (JPA adapters + repos)
│     ├─ email (adapters correo)
│     └─ payments (adapter pasarela)
├─ application
│  ├─ customers / products / cart / orders / tokenization
│  │   ├─ port (in/out)  └─ service (casos de uso)
├─ domain
│  ├─ customers / products / cart / orders / tokenization (model)
├─ bootstrap/config (Security, Properties, Logging)
└─ shared
   ├─ exception (ProblemDetail handler)
   └─ logging (Correlation/Tx context)
```

## 🚀 Quick start

### Requisitos
- JDK 21 (o 17+), Maven 3.9+
- PostgreSQL (local) ó Docker (ver `docker-compose.yml`)
- (Dev) API Key para pruebas

### Propiedades principales
`src/main/resources/application.properties` (base):
```properties
server.servlet.context-path=/api

# Seguridad (API keys)
app.api-keys=supersecretkey123,otherKey456

# Pagos & tokenización
payments.retry-count=3
payments.reject-probability=0.20
tokenization.reject-probability=0.10
tokenization.reject-probability=0.20

# Datasource (DEV)
spring.datasource.url=jdbc:postgresql://localhost:5432/farmatodo
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging JSON
logging.config=classpath:logback-spring.xml
```

Perfiles:
- `dev` → usa **ConsoleMailAdapter** (sin SMTP real) o MailHog (`localhost:1025`).
- `prod` → usa **SpringMailAdapter** (SMTP real).

Arranque (dev):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker (Postgres + MailHog)
```bash
docker compose up -d
# Postgres: localhost:5432  (user=postgres, pass=postgres, db=challenge)
# MailHog UI: http://localhost:8025  (SMTP en 1025)
```

## 🔐 Seguridad – API Key
Enviar en **cada request** autenticada:
```
X-API-Key: supersecretkey123
```
## ✉️ Correo (DEV & PROD)

- **dev**: `ConsoleMailAdapter` o MailHog (`spring.mail.host=localhost`, `spring.mail.port=1025`).
- **prod (GCP)**: `SpringMailAdapter` con SendGrid/Mailgun vía SMTP 587.

Variables típicas en Cloud Run:
```
SPRING_PROFILES_ACTIVE=prod
SPRING_MAIL_HOST=smtp.sendgrid.net
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=apikey
SPRING_MAIL_PASSWORD=(secret manager)
```

## 📜 Logs centralizados
- Filtro añade **traceId** (MDC) por request.
- **txId** por transacción (pedido/pago).
- Logback JSON → stdout → Cloud Logging.

## 🧪 Tests & Cobertura
```bash
mvn clean verify
# abre target/site/jacoco/index.html (fila "Total")
```

## 🧰 Endpoints (base: `/api` → variable `{{base}}`)
> Usa la colección Postman incluida (variables `base` y `apiKey`).

### Health
- `GET {{base}}/actuator/health` → 200

### Ping
- `GET {{base}}/v1/ping` (API Key)

### Customers
- `POST {{base}}/v1/customers`
```json
{ "name":"Andres","email":"a@b.com","phone":"3001112233","address":"Calle 123" }
```

### Products
- `GET {{base}}/v1/products/search?q=vit&minStock=3`

### Tokenization
- `POST {{base}}/v1/tokenize`
```json
{ "pan":"4111111111111111","cvv":"123","expMonth":12,"expYear":2030,"cardholder":"JOHN DOE" }
```

### Cart
- `POST {{base}}/v1/cart`
```json
{
  "customerId":"<uuid>",
  "items":[ {"sku":"SKU-001","qty":2}, {"sku":"SKU-002","qty":1} ]
}
```

### Orders & Payments
- `POST {{base}}/v1/orders`
```json
{
  "customerId":"<uuid>",
  "cartId":"<uuid>",
  "shippingAddress":"Calle 123",
  "token":"tok_abc",
  "retryCount": 2,
  "rejectProbability": 0.0
}
```

Respuestas:
- `201 { "id":"<uuid>","status":"PAID" }`
- `201 { "id":"<uuid>","status":"FAILED" }`

**Errores (ProblemDetail RFC 7807)**: 400 validación, 404 no encontrado, 409 conflicto, 422 tokenización, 500 fallback.

## 📦 Postman
Usa la colección: `challenge-farmatodo.postman_collection.json`

Variables:
- `base` = `http://localhost:8080/api`
- `apiKey` = `dev-key-123`

## 🌱 Datos de ejemplo (opcional)
`src/main/resources/data.sql`
```sql
insert into products(id, sku, name, stock, price) values
 (gen_random_uuid(),'SKU-001','Vitamina C',10,12500),
 (gen_random_uuid(),'SKU-002','Ibuprofeno',20,9800);
```

## 📤 Deploy (resumen GCP – Cloud Run)
1. Imagen → Artifact Registry.
2. Secrets (API key SMTP).
3. Deploy con envs y secrets montados.
4. Logs → Cloud Logging. Health: `/api/actuator/health`.
