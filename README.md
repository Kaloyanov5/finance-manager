# Finance-Manager

A **Spring Boot**-based Finance Manager that utilizes **AI-driven categorization** and **AI Chatbot** to help users manage their finances effectively.

## Features

- **AI-Powered Categorization**: Automatically categorize transactions.
- **AI Chatbot**: Help users maintain financial stability.
- **Transaction Management**: Add and delete financial transactions.
- **Interactive Dashboard**: Visualize expenses and trends.
- **Secure Authentication**: User login and data protection.
- **Task Management**: Add and delete tasks to complete.

## Technologies Used

- Java 23
- Spring Boot (Backend)
- Hibernate & JPA (Database ORM)
- MySQL (Database Storage)
- HTML&CSS & JavaScript (Frontend)
- Maven (Build Tool)

## Setup & Installation

### 1️⃣ **Clone the Repository**

Clone the repository to your local machine:

```bash
git clone https://github.com/Kaloyanov5/finance-manager.git
cd finance-manager
```

### 2️⃣ **Database Setup**

Ensure you have **MySQL** installed and running.

- Run the schema setup script:

```sql
CREATE DATABASE finance_manager;
```

### 3️⃣ **Create & Configure `application.properties`**

- Create `application.properties` files in `src/main/resources/`
- Copy the contents of `src/main/resources/application.properties.example` and paste them in the `application.properties` file
- Assign working values to the variables:
  - Database configuration (IP and port the server is running, name of database, user and the password to the user)
  - Gemini API key (you can get your API key from here -> https://aistudio.google.com/apikey)
  - Secret key (generate a 256 bit key here -> https://jwtsecret.com/generate)

### 4️⃣ **Build and Run the Application**

#### Using Maven:

```bash
mvn clean install
mvn spring-boot:run
```

#### Using IntelliJ IDEA:

- Open the project in IntelliJ.
- Run `FinanceManagerApplication.java`.

## Usage

1. Register or log in to your account.
2. Add transactions manually.
3. View categorized expenses.
4. Use the dashboard:
   - Track expenses.
   - Add and delete tasks.
   - Chat with **AI-powered Finance Chatbot**.

## License

This project is open-source and available under the **MIT License**.

## Contributing

Pull requests are welcome! Please open an issue for major changes before submitting PRs.
