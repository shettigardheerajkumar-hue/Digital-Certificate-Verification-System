## Digital Certificate Verification System (Console, DAO Pattern)

This is a console-based Java 17 application that demonstrates a **Digital Certificate Verification System**
using the **DAO pattern** and **JDBC** (MySQL by default).

### Requirements

- Java 17+
- Maven
- A MySQL database (or another JDBC-compatible DB; adjust JDBC URL and driver as needed)

### Database setup (MySQL example)

Create a database and tables:

```sql
CREATE DATABASE digital_certificates;
USE digital_certificates;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_id VARCHAR(100) NOT NULL UNIQUE,
    holder_name VARCHAR(255) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    institution_name VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

Update `src/main/resources/db.properties` with your database credentials.

### Run the application

```bash
mvn compile
mvn exec:java
```

You will first see a **Login / Register** menu for administrators, and after successful login a text-based menu that lets you
**add**, **view**, **search**, **update**, **delete**, and **verify** certificates by certificate ID (which can correspond to a QR code value).

