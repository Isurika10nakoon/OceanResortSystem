# 🏖️ Ocean Resort Management System

A modern, feature-rich hotel management system built with Java Swing, designed to streamline resort operations with an intuitive and beautiful user interface.

## 📋 Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [How to Run](#how-to-run)
- [Project Structure](#project-structure)
- [Default Credentials](#default-credentials)
- [Usage Guide](#usage-guide)
- [Room Rates](#room-rates)
- [File Storage](#file-storage)
- [Screenshots](#screenshots)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### 🔐 Authentication
- Secure login system with modern UI
- Password visibility toggle
- Input validation and error handling

### 📊 Dashboard
- Real-time statistics overview
- Total bookings counter
- Active guests monitoring
- Upcoming reservations tracker
- Total revenue calculation
- Recent bookings list
- Room distribution analytics with progress bars

### ➕ Reservation Management
- Create new reservations with comprehensive guest details
- Form validation for all required fields
- Date picker for check-in/check-out dates
- Room type selection (Single, Double, Suite)
- Unique reservation ID system
- Automatic status calculation (Active, Upcoming, Completed)

### 📋 View & Search Reservations
- Interactive data table with all reservations
- Real-time search functionality
- View detailed reservation information
- Delete reservations with confirmation
- Color-coded status indicators
- Sortable columns

### 💳 Billing & Invoices
- Professional invoice generation
- Automatic calculation of total charges
- Night-based pricing
- Detailed billing breakdown
- Modern HTML invoice template
- Print-ready format

### 📈 Analytics & Reports
- Revenue breakdown by room type
- Average booking duration calculation
- Visual statistics cards
- Performance metrics

### ❓ Help & Documentation
- Comprehensive user manual
- Room rates and amenities information
- System navigation guide
- Support contact information

### 🎨 Modern UI/UX
- Clean, professional interface
- Custom-styled components (buttons, text fields)
- Gradient backgrounds
- Rounded corners and shadows
- Smooth hover effects
- Responsive design
- Color-coded status badges
- Emoji icons for better visual appeal

## 🛠️ Technologies Used

- **Language:** Java (JDK 8 or higher)
- **GUI Framework:** Java Swing
- **Graphics:** Java 2D Graphics API
- **Data Storage:** Text file (CSV format)
- **Date/Time:** Java 8 Time API (LocalDate)
- **Collections:** HashMap, ArrayList

## 💻 System Requirements

### Minimum Requirements:
- **Operating System:** Windows 7/8/10/11, macOS 10.12+, or Linux
- **Java Runtime Environment (JRE):** Version 8 or higher
- **RAM:** 512 MB minimum
- **Disk Space:** 50 MB
- **Display:** 1280x720 minimum resolution

### Recommended Requirements:
- **Java Development Kit (JDK):** Version 11 or higher
- **RAM:** 1 GB or more
- **Display:** 1920x1080 or higher

## 📥 Installation

### Step 1: Install Java
1. Download Java JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Install JDK following the installer instructions
3. Verify installation:
   ```bash
   java -version
   javac -version
   ```

### Step 2: Download the Project
1. Download all project files to a directory
2. Ensure all `.java` files are in the same folder
3. All files should be in the `resort` package

## 🚀 How to Run

### Method 1: Using Command Line

#### Windows:
```bash
# Navigate to project directory
cd path\to\project

# Compile all Java files
javac resort\*.java

# Run the application
java resort.OceanResortSystem
```

#### macOS/Linux:
```bash
# Navigate to project directory
cd path/to/project

# Compile all Java files
javac resort/*.java

# Run the application
java resort.OceanResortSystem
```

### Method 2: Using IDE (Recommended)

#### IntelliJ IDEA:
1. Open IntelliJ IDEA
2. File → New → Project from Existing Sources
3. Select the project folder
4. Import as Java project
5. Right-click on `OceanResortSystem.java`
6. Select "Run 'OceanResortSystem.main()'"

#### Eclipse:
1. Open Eclipse
2. File → New → Java Project
3. Uncheck "Use default location"
4. Browse to project folder
5. Finish
6. Right-click on `OceanResortSystem.java`
7. Run As → Java Application

#### NetBeans:
1. Open NetBeans
2. File → New Project → Java → Java Application
3. Select project location
4. Right-click project → Properties → Sources
5. Add source package folder
6. Right-click `OceanResortSystem.java` → Run File

### Method 3: Create JAR File
```bash
# Create manifest file
echo "Main-Class: resort.OceanResortSystem" > manifest.txt

# Compile
javac resort/*.java

# Create JAR
jar cvfm OceanResort.jar manifest.txt resort/*.class

# Run JAR
java -jar OceanResort.jar
```

## 📁 Project Structure

```
ocean-resort-system/
│
├── resort/                          # Package folder
│   ├── OceanResortSystem.java      # Main application class
│   ├── LoginPage.java              # Login interface
│   ├── MainMenu.java               # Navigation menu
│   ├── Dashboard.java              # Dashboard screen
│   ├── AddReservation.java         # Add reservation form
│   ├── ViewReservations.java       # Reservations table view
│   ├── Billing.java                # Invoice generation
│   ├── Analytics.java              # Analytics & reports
│   ├── Help.java                   # Help documentation
│   ├── ModernButton.java           # Custom button component
│   ├── ModernTextField.java        # Custom text field component
│   └── ModernPasswordField.java    # Custom password field component
│
├── reservations.txt                # Data storage file (auto-generated)
└── README.md                       # This file
```

### Class Descriptions:

#### Core Classes:
- **OceanResortSystem.java**: Main entry point, data model (Reservation class), file I/O operations
- **LoginPage.java**: Authentication interface with gradient design
- **MainMenu.java**: Sidebar navigation and content area management

#### Feature Classes:
- **Dashboard.java**: Statistics overview, recent bookings, room distribution
- **AddReservation.java**: Form for creating new reservations
- **ViewReservations.java**: Table display, search, view details, delete operations
- **Billing.java**: Invoice generation with professional HTML template
- **Analytics.java**: Revenue analysis and booking statistics
- **Help.java**: User manual and system documentation

#### Custom Components:
- **ModernButton.java**: Styled button with hover effects and rounded corners
- **ModernTextField.java**: Text input with placeholder and error states
- **ModernPasswordField.java**: Password input with show/hide toggle

## 🔑 Default Credentials

**Username:** `admin`  
**Password:** `admin123`

> ⚠️ **Security Note:** For production use, implement proper authentication with encrypted passwords.

## 📖 Usage Guide

### 1. Login
- Launch the application
- Enter username: `admin`
- Enter password: `admin123`
- Click "LOGIN" or press Enter

### 2. Dashboard
- View overall statistics
- Monitor active guests and upcoming bookings
- Check total revenue
- See recent reservations
- View room type distribution

### 3. Add New Reservation
- Click "Add Reservation" in the sidebar
- Fill in all required fields:
  - Reservation Number (e.g., RES-001)
  - Guest Name
  - Address
  - Contact Number
  - Room Type
  - Check-In Date
  - Check-Out Date
- Click "SAVE RESERVATION"
- Click "CLEAR" to reset the form

### 4. View Reservations
- Click "View Reservations" in the sidebar
- Use the search box to filter by name, ID, or contact
- Click the eye icon (👁) to view details
- Click the trash icon (🗑) to delete a reservation
- Click "REFRESH" to reload the table

### 5. Generate Invoice
- Click "Billing" in the sidebar
- Enter the Reservation ID
- Click "GENERATE INVOICE"
- Invoice displays with full details and calculations

### 6. View Analytics
- Click "Analytics" in the sidebar
- View revenue by room type
- Check average booking duration
- Analyze performance metrics

### 7. Access Help
- Click "Help" in the sidebar
- Review room rates and amenities
- Read system navigation guide
- Find support contacts

### 8. Logout
- Click "Logout" in the sidebar
- Confirm logout action
- Returns to login screen

## 💰 Room Rates

| Room Type | Rate/Night | Capacity | Amenities |
|-----------|------------|----------|-----------|
| 🛏 Single | LKR 8,000 | 1 Adult | Breakfast, WiFi |
| 🛏🛏 Double | LKR 12,000 | 2 Adults | Breakfast, WiFi, Sea View |
| 🏠 Suite | LKR 20,000 | 4 Adults | Full Board, Pool, Spa |

## 💾 File Storage

### Data File: `reservations.txt`
- **Format:** CSV (Comma-Separated Values)
- **Location:** Same directory as application
- **Structure:** `ResNo,Name,Address,Contact,RoomType,CheckIn,CheckOut`
- **Auto-generated:** Created automatically on first save
- **Persistence:** Data persists between sessions

### Example Entry:
```
RES-001,John Doe,123 Main St,+94771234567,Double,2024-02-15,2024-02-18
```

## 🖼️ Screenshots

> Note: Add screenshots of your application here

### Login Screen
![Login Screen](screenshots/login.png)

### Dashboard
![Dashboard](screenshots/dashboard.png)

### Add Reservation
![Add Reservation](screenshots/add-reservation.png)

### View Reservations
![View Reservations](screenshots/view-reservations.png)

### Billing
![Billing](screenshots/billing.png)

## 🔧 Troubleshooting

### Issue: Application doesn't start
**Solution:**
- Verify Java is installed: `java -version`
- Check JAVA_HOME environment variable
- Ensure all files are in correct package structure

### Issue: Compilation errors
**Solution:**
- Verify all 12 `.java` files are present
- Check package declaration at top of each file
- Ensure Java version compatibility (JDK 8+)

### Issue: Data not saving
**Solution:**
- Check file write permissions in application directory
- Ensure `reservations.txt` is not open in another program
- Verify disk space availability

### Issue: Window too large/small
**Solution:**
- Modify window size in respective class constructors
- Recommended: 1400x850 for main window, 1000x650 for login

### Issue: Date picker not working
**Solution:**
- Ensure JSpinner is properly initialized
- Check date format: yyyy-MM-dd
- Verify LocalDate compatibility

### Issue: Search not working
**Solution:**
- Check case-insensitive search implementation
- Verify KeyListener is attached to search field
- Ensure HashMap is populated with data

## 🚀 Future Enhancements

### Planned Features:
- [ ] Database integration (MySQL/PostgreSQL)
- [ ] Multi-user support with role-based access
- [ ] Email notifications for bookings
- [ ] Payment gateway integration
- [ ] Room availability calendar
- [ ] Guest check-in/check-out system
- [ ] Housekeeping management
- [ ] Report export (PDF, Excel)
- [ ] Backup and restore functionality
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Mobile responsive design
- [ ] Cloud synchronization
- [ ] Advanced analytics with charts
- [ ] Customer feedback system

### Technical Improvements:
- [ ] Implement MVC architecture
- [ ] Add unit tests (JUnit)
- [ ] Encrypt sensitive data
- [ ] Add logging system (Log4j)
- [ ] Implement dependency injection
- [ ] Add configuration file (properties/YAML)
- [ ] Performance optimization
- [ ] Memory management improvements

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Contribution Guidelines:
- Follow existing code style and conventions
- Add comments for complex logic
- Test thoroughly before submitting
- Update README if adding new features
- Keep commits atomic and well-described

## 📄 License

This project is licensed under the MIT License - see below for details:

```
MIT License

Copyright (c) 2024 Ocean Resort Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Support

For support and questions:
- **Email:** support@oceanresort.com
- **Issues:** Create an issue on GitHub
- **Documentation:** See Help section in application

## 👨‍💻 Developer Information

**Version:** 2.0  
**Release Date:** February 2024  
**Developed By:** Ocean Resort Development Team  
**Java Version:** Compatible with JDK 8+  
**GUI Framework:** Java Swing  

## 🙏 Acknowledgments

- Java Swing documentation
- Java 2D Graphics API
- Open source Java community
- Font: Segoe UI (system font)
- Icons: Unicode emoji characters

---

**⭐ If you find this project useful, please consider giving it a star!**

**🐛 Found a bug? Please report it in the Issues section.**

**💡 Have a feature request? We'd love to hear about it!**

---

Made with ❤️ for efficient resort management
