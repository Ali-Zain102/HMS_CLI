# 🏥 Hospital Management System (HMS CLI)

A comprehensive, console-based Hospital Management System built with **Java (OOP)** and **MariaDB**. This project demonstrates advanced Object-Oriented Programming principles, robust database design, and a multi-tiered architecture (Model-DAO-Service-UI).

## ✨ Features

- **👤 Patient Management:** Register, admit, discharge, and track patients.
- **👨‍⚕️ Staff Management:** Manage Doctors, Surgeons, Nurses, and Receptionists with dynamic scheduling.
- **📅 Appointment Booking:** Book, cancel, and complete appointments using database stored procedures.
- **🛏️ Bed & Ward Management:** Real-time bed occupancy tracking.
- **💊 Pharmacy & Inventory:** Medicine stock tracking with low-stock alerts.
- **🔬 Lab Reports:** Request tests and enter lab results.
- **💰 Billing & Payments:** Automated bill calculation and payment processing.
- **📊 Reporting Dashboard:** Complex reports (Occupancy, Doctor Schedules, Pending Bills) generated via SQL Views.

## 🛠️ Technology Stack

- **Language:** Java SE (JDK 8+)
- **Database:** MariaDB (via XAMPP)
- **Database Driver:** MySQL Connector/J 9.0.0
- **Architecture:** DAO (Data Access Object), Service Layer, CLI Menu Navigation
- **UI:** Interactive CLI with ANSI color support and ASCII tables

## 🗄️ Database Setup

1. Install and start **XAMPP** (Apache & MySQL).
2. Open **phpMyAdmin** (`http://localhost/phpmyadmin`).
3. Import the SQL scripts located in the `sql/` folder **in this exact order**:
   1. `01_schema.sql` (Creates the `hms_cli_db` database and all tables)
   2. `02_stored_procedures.sql`
   3. `03_functions.sql`
   4. `04_triggers.sql`
   5. `05_views.sql`
   6. `06_sample_data.sql`

## 🚀 How to Run

### Windows (Recommended)
Simply double-click the **`run.bat`** file in the root directory. This will automatically set the console encoding to UTF-8 (for emojis and table borders), compile the Java files, and launch the system.

### Manual Terminal Execution
```powershell
# 1. Compile the project
javac -d bin -cp "lib/*" src/com/hms/**/*.java src/com/hms/*.java

# 2. Run the project
java -cp "bin;lib/*" com.hms.Main
```

## 🧠 OOP Concepts Demonstrated
- **Encapsulation:** Private fields accessed via Getters/Setters.
- **Inheritance:** `Person` → `Staff` → `Doctor` → `Surgeon`.
- **Polymorphism:** Method overriding (`displayInfo()`) and overloading.
- **Abstraction:** Abstract classes (`Person`, `Staff`) and Interfaces (`Billable`, `Treatable`).
- **Composition & Aggregation:** Patients HAVE-A Medical Record; Doctors belong to a Department.

## 📝 License
This project is created for educational purposes. Feel free to use and modify it!
