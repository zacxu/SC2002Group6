# SC2002 Group6 
## AY2025/26 Semester 1 SC2002 Group Project - Internship Placement Management System (IPMS)

Internship Placement Management System (IPMS) is a Java console application that utilizes object-oriented concepts to efficiently manage internships. The program is designed with a focus on reusability, extensibility, and maintainability, allowing for easy upgrades and future development. It provides flexibility to accommodate different user types and their requirements.

## Team Members

We are a group 6 from lab group SCEA, Nanyang Technological University, Singapore. There are 5 members in our group:

| Name         | Github Account                                  | 
|--------------|-------------------------------------------------|
| Issac        | 
| Royden       | 
| Sheow Ming   | 
| Juan         |
| Zi Xuan      |

## Setup Instructions

### Prerequisites

Java Development Kit (JDK)

### Project Structure

The project follows this directory structure:
```
SC2002Group6/
├── data/                          # Data storage directory 
│   ├── applications.csv          # Application data
│   ├── internships.csv           # Internship listings
│   ├── users.csv                 # User accounts
│   └── withdrawal_requests.csv   # Withdrawal requests
├── src/
│   ├── java/                     # Java source files
│   │   ├── MainApp.java         # Main entry point
│   │   ├── boundary/            # UI layer
│   │   ├── controller/          # Controllers
│   │   ├── entity/              # Domain entities
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic layer
│   │   ├── util/                # Utility classes
│   │   └── validator/           # Validation classes
│   └── resources/                # Resource files
│       ├── sample_student_list.csv
│       └── sample_staff_list.csv
└── docs/                         # Javadoc documentation
```

## Installation Steps

1. **Clone the repository** (if using Git):
   ```bash
   git clone <repository-url>
   cd SC2002Group6
   ```

2. **Ensure required directories exist**:
   - The `data/` directory will be automatically created when you first run the application
   - Ensure `src/resources/` contains:
     - `sample_student_list.csv`
     - `sample_staff_list.csv`

3. **Compile the Java source files**:
   
   ```bash
   javac -d bin -sourcepath src\java src\java\*.java src\java\boundary\*.java src\java\controller\*.java src\java\entity\*.java src\java\entity\enums\*.java src\java\repository\*.java src\java\service\*.java src\java\util\*.java src\java\validator\*.java
   ```
   

4. **Run the application**:
   
   ```bash
   cd bin
   java MainApp
   cd ..
   ```
   
