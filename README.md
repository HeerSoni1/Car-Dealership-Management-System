# Car Dealership Management System

A Java-based console application designed to manage the day-to-day operations of a car dealership using MySQL and JDBC.

## Features

- **Admin Login:** Authentication with username verification and OTP verification.
- **Car Management:** Add, update, delete, search, and sort car records.
- **Customer Management:** Maintain and manage customer details.
- **Employee Management:** Manage employee information.
- **Sales Management:** Record sales, generate invoices, and update car stock.
- **Payment Management:** Handle payment details, payment modes, status, and receipts.
- **Reports:** Generate reports for available cars, cars sold, monthly sales, and revenue.
- **Database Connectivity:** Connect Java application to MySQL using JDBC.

## Technologies Used

- Java
- MySQL
- JDBC
- IntelliJ IDEA
 ## Concepts Applied
- Object-Oriented Programming
- DBMS and SQL
- Data Structures and Algorithms
- CRUD Operations

## Data Structures Used

- ArrayList
- HashMap
- TreeMap
- Linked List
- Stack

## Database Structure

The project uses a database named `car`.

Main tables include:
- Admin
- Car
- Customer
- Payment
- Sales

The database folder contains the SQL file required to create the database structure and sample records.

## Setup and Installation

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Install JDK 17 or later.
4. Install MySQL and start your database server.
5. Import the `database/car.sql` file into MySQL.
6. Add the MySQL JDBC driver to your project.
7. Configure your local database username and password in the database connection class.
8. Run the application's main class.

Project Structure

Car-Dealership-Management/
│
├── src/
│   ├── java2/
│   ├── dbms/
│   └── ds/
│
├── database/
│   └── car.sql
│
├── .gitignore
└── README.md
## Learning Outcomes

This project demonstrates Java OOP concepts, JDBC connectivity, SQL operations, database management, and implementation of different data structures.

---

**Note:** This project was developed as a college project for learning and practical implementation of Java, DBMS, and Data Structures.
