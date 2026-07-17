# UPI_Without_Internet 🔐📡

An offline-first UPI-inspired payment system designed to explore how digital transactions can work in low-connectivity environments.

This project simulates a secure payment network where transactions are created on sender devices without continuous internet access, propagated through a device-to-device mesh network using a gossip protocol, and synchronized with the backend through a bridge node when connectivity becomes available.

The system focuses on real-world software engineering challenges such as secure transaction processing, distributed communication, duplicate transaction prevention, and eventual consistency.

## Key Features

* Offline transaction creation and processing
* Device-to-device mesh transaction propagation
* Gossip protocol-based packet forwarding
* Secure transaction validation using encryption
* Idempotent backend synchronization
* Transaction ledger and status tracking
* REST API-based backend architecture

## Tech Stack

**Backend**

* Java 21
* Spring Boot
* Spring Data JPA / Hibernate
* REST APIs
* PostgreSQL / MySQL

**Security**

* RSA-based encryption
* Authentication and transaction validation

**Frontend**

* Angular
* TypeScript
* HTML/CSS

**Tools**

* Maven
* Git & GitHub
* Postman

  ## 🚀 How to Run the Project Locally

Follow these steps to set up and run **UPI_Without_Internet** on your local machine.

### Prerequisites

Make sure you have installed:

* Java 21 or above
* Maven 3.8+
* Git
* PostgreSQL / MySQL (depending on your database configuration)

Verify installations:

```bash
java -version
mvn -version
```

---

### 1. Clone the Repository

```bash
git clone https://github.com/Hashh01/-UPI_Without_Internet.git
```

Navigate to the project directory:

```bash
cd UPI_Without_Internet
```

---

### 2. Configure Database

Open:

```
src/main/resources/application.properties
```

Update your database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/upi_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Make sure your database server is running.

---

### 3. Build the Application

Run:

```bash
mvn clean install
```

This will download dependencies and create the application build.

---

### 4. Start the Backend Server

Run:

```bash
mvn spring-boot:run
```

or run the generated JAR file:

```bash
java -jar target/UPI_Without_Internet.jar
```

The application will start on:

```
http://localhost:8080
```

---

### 5. Access the Demo Dashboard

Open your browser:

```
http://localhost:8080
```

You can test the workflow:

1. Create an offline payment transaction
2. Inject transaction into the mesh network
3. Run gossip propagation between devices
4. Connect the bridge node and synchronize transactions
5. Verify transaction settlement and idempotency handling

---

## 🧪 API Testing

APIs can be tested using:

* Postman
* Browser
* Swagger UI (if enabled)

Swagger URL:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🛠 Troubleshooting

### Port already in use

Change the port in:

```
src/main/resources/application.properties
```

Example:

```properties
server.port=8081
```

### Database connection error

Check:

* Database server is running
* Credentials are correct
* Database name exists


## Project Goal

The goal of this project is to understand and implement offline-first architecture patterns used in distributed systems and FinTech applications.
