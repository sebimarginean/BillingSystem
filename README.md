# Billing System

## Overview
The Billing System is a comprehensive application designed to manage clients, invoices, and payments effectively. It offers a user-friendly graphical interface that allows users to add, update, and delete client and invoice records, track payments, and view statistics about financial interactions. The system is ideal for small to medium-sized businesses that require a straightforward solution for managing financial transactions.

## Features
- **Client Management**: Add, delete, and update client details including their total debt.
- **Invoice Management**: Create and manage invoices, including adding payments and adjusting total amounts.
- **Payment Handling**: Record payments against invoices and update outstanding amounts.
- **Statistics View**: Provides a statistical overview of client debts, number of invoices, and average per invoice, enhancing business insights.
- **Database Integration**: Connects to a MySQL database for persistent storage of clients, invoices, and payments.

## Setup and Installation
1. **Prerequisites**:
   - Java Runtime Environment (JRE) and Java Development Kit (JDK) 8 or above.
   - MySQL database server.
   - Maven for building the project.

2. **Database Configuration**:
   - Set up your MySQL server with the required `sistem_facturare` database.
   - Update the database credentials in `DatabaseConnection.java` as per your MySQL setup.

3. **Building the Project**:
   - Navigate to the project directory where `pom.xml` is located.
   - Run `mvn clean install` to build the project.

4. **Running the Application**:
   - Execute `java -jar target/BillingSystem-1.0-SNAPSHOT.jar` from the command line, or run the `BillingSystemGUI` class from your IDE.

## Usage
- **Starting the Application**: Launch the application. The main window will display tabs for Clients, Invoices, Payments, and Statistics.
- **Adding Data**:
  - **Clients Tab**: Enter client details and save them to the database.
  - **Invoices Tab**: Create invoices for existing clients and specify amounts.
  - **Payments Tab**: Record payments against open invoices.
- **Viewing Statistics**: Navigate to the Statistics tab to view aggregated data and insights.

## Contributing
Contributions to the Billing System are welcome. Please fork the repository, make your changes, and submit a pull request for review.