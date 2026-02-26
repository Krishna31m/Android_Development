# 🌊 VarunaApp – Village Water & Health Monitoring System

![VarunaApp Banner](./assets/banner.png)
<!-- Replace with your app banner/logo -->

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

VarunaApp is an Android-based monitoring system designed to improve village water safety and public health tracking. It enables Health Officers and Asha Workers to upload water test data, report water-related diseases, and monitor active and cured patient cases in real-time.

---

## 📱 Project Overview

VarunaApp provides:

- ✅ **Village-wise water quality monitoring**
- ✅ **Water test data entry** (manual & CSV upload)
- ✅ **Health report submission** for water-related diseases
- ✅ **Role-based access control**
- ✅ **Real-time Firebase data storage**
- ✅ **Patient tracking** (Active vs Cured cases)
- ✅ **Awareness & safety notifications**

---

## 🎯 Problem Statement

Many villages face critical challenges:

- 💧 **Contaminated drinking water**
- ⏰ **Delayed disease reporting**
- 📉 **Lack of centralized monitoring**
- 🔍 **Poor tracking of outbreak trends**

**VarunaApp solves this** by creating a digital monitoring system accessible to field health workers, enabling:
- Early detection of water contamination
- Faster response to health outbreaks
- Data-driven decision making
- Improved public health outcomes

---

## 🏗 System Architecture

### High-Level Architecture

```
┌─────────────────────┐
│   Android App       │
│   (Kotlin)          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Firebase Auth       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Cloud Firestore     │
│ Database            │
└─────────────────────┘
```

### Data Flow Diagram (Level 0)

![DFD Level 0](./assets/dfd-level-0.png)
<!-- Add your DFD Level 0 diagram here -->

```
┌──────────────┐
│              │
│    Users     │──────┐
│  (3 Roles)   │      │
│              │      │
└──────────────┘      │
                      ▼
              ┌───────────────┐
              │               │
              │  VarunaApp    │
              │   System      │
              │               │
              └───────┬───────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  Village    │ │   Water     │ │   Health    │
│  Management │ │   Quality   │ │  Reporting  │
│             │ │  Monitoring │ │             │
└─────────────┘ └─────────────┘ └─────────────┘
```

### Data Flow Diagram (Level 1)

![DFD Level 1](./assets/dfd-level-1.png)
<!-- Add your detailed DFD Level 1 diagram here -->

---

## 👥 User Roles

### 1️⃣ HealthReport (Admin)
**Permissions:**
- ✅ Add villages
- ✅ Upload water test tables
- ✅ Upload CSV water data
- ✅ Add health reports
- ✅ View & manage patient data
- ✅ Delete records

### 2️⃣ AshaWorker
**Permissions:**
- ✅ Add water test data
- ✅ Add health reports
- ✅ View village data
- ❌ Delete records

### 3️⃣ GeneralUser
**Permissions:**
- ✅ View village data
- ✅ View water reports
- ✅ View health statistics
- ❌ Add/Edit/Delete data

---

## 📸 User Interface

### Login & Authentication

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-login.png" alt="Login Screen" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-signup.png" alt="Signup Screen" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-role-selection.png" alt="Role Selection" width="250"/></td>
  </tr>
  <tr>
    <td align="center"><b>Login Screen</b></td>
    <td align="center"><b>Signup Screen</b></td>
    <td align="center"><b>Role Selection</b></td>
  </tr>
</table>


### Dashboard & Village Management

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-dashboard.png" alt="Dashboard" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-village-list.png" alt="Village List" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-add-village.png" alt="Add Village" width="250"/></td>
  </tr>
  <tr>
    <td align="center"><b>Main Dashboard</b></td>
    <td align="center"><b>Village List</b></td>
    <td align="center"><b>Add Village</b></td>
  </tr>
</table>

### Water Quality Monitoring

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-water-test.png" alt="Water Test Entry" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-csv-upload.png" alt="CSV Upload" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-water-reports.png" alt="Water Reports" width="250"/></td>
  </tr>
  <tr>
    <td align="center"><b>Water Test Entry</b></td>
    <td align="center"><b>CSV Upload</b></td>
    <td align="center"><b>Water Reports</b></td>
  </tr>
</table>

### Health Reporting

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-add-health-report.png" alt="Add Health Report" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-patient-list.png" alt="Patient List" width="250"/></td>
    <td><img src="https://raw.githubusercontent.com/Capstone-Project-app/VarunaApp/main/app/src/main/assets/ui-health-stats.png" alt="Health Information" width="250"/></td>
  </tr>
  <tr>
    <td align="center"><b>Add Health Report</b></td>
    <td align="center"><b>Patient List</b></td>
    <td align="center"><b>Health information</b></td>
  </tr>
</table>

---

## 🏘 Village Management

- ✅ Authorized users can add new villages
- ✅ Villages are displayed **alphabetically (A → Z)**
- ✅ Users select a village before uploading data
- ✅ Each village stores:
  - Water Test Tables
  - Health Reports
  - Patient Records

**Village Selection Flow:**

```
Select Village → View Data → Add Reports → Track Statistics
```

---

## 💧 Water Monitoring Features

### Manual Entry
- ✅ Multiple water parameter rows
- ✅ Real-time validation
- ✅ Village-wise storage

### CSV Upload
- ✅ Bulk data import
- ✅ Format validation
- ✅ Error handling

### Supported Parameters
- pH Level
- TDS (Total Dissolved Solids)
- Turbidity
- Chlorine Content
- Bacterial Count
- Heavy Metals
- And more...

---

## 🏥 Health Reporting Features

Each health report includes:

| Field | Description |
|-------|-------------|
| **Patient Name** | Full name of the patient |
| **Age** | Patient age |
| **Gender** | Male/Female/Other |
| **Symptoms** | Water-related symptoms (multiple selection) |
| **Symptom Start Date** | When symptoms began |
| **Severity** | Mild / Moderate / Severe |
| **Water Source** | Source of drinking water |
| **Additional Notes** | Extra medical information |
| **Status** | Cured / Not Cured |

### Storage Path
```
villages/{villageName}/health_reports/{reportId}
```

---

## 📊 Tracking & Analytics

### Real-Time Metrics
- 📈 **Active patient count**
- ✅ **Cured patient count**
- 📍 **Village-specific disease trends**
- 📋 **Real-time report viewing**

### Dashboard Features
- Visual charts and graphs
- Trend analysis
- Comparison across villages
- Export functionality

---

## 🔐 Security

### Authentication & Authorization

```kotlin
// Firebase Authentication
FirebaseAuth.getInstance()

// Role-based access
when (userRole) {
    "HealthReport" -> // Full access
    "AshaWorker" -> // Limited write access
    "GeneralUser" -> // Read-only access
}
```

### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /villages/{village} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['HealthReport', 'AshaWorker']);
    }
  }
}
```

**Security Features:**
- ✅ Firebase Authentication
- ✅ Role-Based Security Rules
- ✅ Secure data transmission
- ✅ Only authorized roles can write data
- ✅ General users have read-only access

---

## 🛠 Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **Platform** | Android SDK (Min SDK 24+) |
| **Authentication** | Firebase Authentication |
| **Database** | Firebase Cloud Firestore |
| **UI Components** | Material Design 3 |
| **Lists** | RecyclerView |
| **CSV Parsing** | OpenCSV / Custom Parser |
| **Image Loading** | Glide / Coil |
| **Security** | Role-based Firestore Rules |

---

## 📂 Database Structure

### Firestore Collections

```javascript
// Users Collection
users/{userId}
  ├── role: "HealthReport" | "AshaWorker" | "GeneralUser"
  ├── name: String
  ├── email: String
  ├── createdAt: Timestamp
  └── lastLogin: Timestamp

// Villages Collection
villages/{villageName}
  ├── name: String
  ├── createdAt: Timestamp
  ├── createdBy: String
  ├── population: Number
  └── location: GeoPoint

// Water Tables Sub-collection
villages/{villageName}/water_tables/{tableId}
  ├── createdAt: Timestamp
  ├── uploadedBy: String
  ├── parameters: Array
  │   ├── name: String
  │   ├── value: Number
  │   └── unit: String
  └── csvSource: Boolean

// Health Reports Sub-collection
villages/{villageName}/health_reports/{reportId}
  ├── patientName: String
  ├── age: Number
  ├── gender: String
  ├── symptoms: Array[String]
  ├── symptomStartDate: Timestamp
  ├── severity: "Mild" | "Moderate" | "Severe"
  ├── waterSource: String
  ├── status: "Cured" | "Active"
  ├── notes: String
  ├── createdAt: Timestamp
  └── reportedBy: String
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11+
- Firebase account
- Android device/emulator (API 24+)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/VarunaApp.git
   cd VarunaApp
   ```

2. **Open in Android Studio**
   ```bash
   File → Open → Select VarunaApp folder
   ```

3. **Configure Firebase**
   - Create a new Firebase project at [firebase.google.com](https://firebase.google.com)
   - Add Android app to your Firebase project
   - Download `google-services.json`
   - Place it in `app/` directory

4. **Enable Firebase Services**
   - Authentication (Email/Password)
   - Cloud Firestore
   - (Optional) Cloud Storage for images

5. **Build and Run**
   ```bash
   Build → Make Project
   Run → Run 'app'
   ```

---

## 📱 App Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 🔄 App Flow

```
1. User Login/Signup
   ↓
2. Role Assignment
   ↓
3. Dashboard View
   ↓
4. Select Village
   ↓
5. Choose Action:
   - View Water Reports
   - Add Water Test Data
   - View Health Reports
   - Add Health Report
   - View Statistics
   ↓
6. Data Sync with Firebase
```

---

## 🎨 Design Patterns

- **MVVM (Model-View-ViewModel)** - Architecture pattern
- **Repository Pattern** - Data abstraction
- **Singleton** - Firebase instance management
- **Observer Pattern** - LiveData & StateFlow
- **Adapter Pattern** - RecyclerView adapters

---

## 🧪 Testing

```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest
```

---

## 📝 Future Enhancements

- [ ] Offline mode with local caching
- [ ] Push notifications for critical alerts
- [ ] Multi-language support
- [ ] Data export to PDF/Excel
- [ ] Machine learning for outbreak prediction
- [ ] Integration with government health portals
- [ ] Voice input for health reports
- [ ] Image upload for water samples

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Developers

- **Your Name** - *Lead Developer* - [GitHub](https://github.com/krishna31m)

---

## 📞 Contact & Support

- **Email:** support@varunaapp.com
- **GitHub Issues:** [Report a bug](https://github.com/Capstone-Project-app/VarunaApp/issues)
- **Documentation:** [Wiki](https://github.com/Capstone-Project-app/VarunaApp/wiki)

---

## 🙏 Acknowledgments

- Firebase team for excellent backend services
- Material Design for UI components
- Open source community for various libraries
- Health workers who inspired this project

---

## 📊 Project Statistics

![GitHub stars](https://img.shields.io/github/stars/yourusername/VarunaApp)
![GitHub forks](https://img.shields.io/github/forks/yourusername/VarunaApp)
![GitHub issues](https://img.shields.io/github/issues/yourusername/VarunaApp)
![GitHub pull requests](https://img.shields.io/github/issues-pr/yourusername/VarunaApp)

---

<div align="center">

**Made with ❤️ for improving village health and water safety**

[⬆ Back to Top](#-varunaapp--village-water--health-monitoring-system)

</div>
