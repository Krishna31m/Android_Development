#**🚨 Women Safety App**

A women’s safety Android application built with Kotlin and Firebase, designed to provide quick SOS alerts, real-time location tracking, and emergency audio recording to ensure user safety in critical situations.

✨ Features
🔑 Authentication

Firebase Authentication (Email/Password).

New users can Sign Up with Name, Email, Password, and Mobile Number.

Existing users can Login securely.

Session Management → Once logged in, users stay logged in until they logout or uninstall.

Logout option available in the app menu.

🖼️ Splash Screen

Displays app logo and name on startup.

Redirects user to Login/Signup if not logged in.

Redirects user to MainActivity if already logged in.

📞 Emergency Contacts

Users can save a minimum of 3 emergency contacts (mobile numbers).

Contacts are stored locally (using SharedPreferences or SQLite).

Contacts can be updated anytime.

🚨 SOS Help Button

When clicked:

Sends an SOS message with the user’s live location (Google Maps link) to saved contacts via SMS.

Continuously tracks and updates the location so emergency contacts can monitor in real-time.

🎤 Automatic Recording (RecorderActivity)

When SOS is triggered, the app automatically:

Starts audio recording in the background.

Saves recordings to the user’s device (internal storage in /SOSRecordings/).

Provides option to stop recording manually.

🛠️ Tech Stack

Language: Kotlin

IDE: Android Studio

Backend & Auth: Firebase Authentication, Firebase Firestore

Database: SharedPreferences / SQLite (for local contact storage)

Location Services: Google FusedLocationProviderClient

Messaging: SMSManager

Recording: MediaRecorder API

📱 Screens Overview

SplashActivity → Displays app logo, handles session check.

LoginActivity → Allows users to log in.

SignupActivity → Allows new users to sign up.

MainActivity → Displays SOS button and contact management.

RecorderActivity → Handles background audio recording.

🚀 How to Run

Clone the repository:

git clone https://github.com/your-username/WomenSafetyApp.git
cd WomenSafetyApp


Open the project in Android Studio.

Connect your project with Firebase:

Go to Tools > Firebase in Android Studio.

Enable Authentication and Firestore Database.

Add required permissions in AndroidManifest.xml:

<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


Run the app on a real device (emulator may not support SMS sending).

📂 Project Structure
WomenSafetyApp/
│── app/src/main/java/com/krishna/womensafety/
│   │── SplashActivity.kt
│   │── LoginActivity.kt
│   │── SignupActivity.kt
│   │── MainActivity.kt
│   │── RecorderActivity.kt
│
│── app/src/main/res/layout/
│   │── activity_splash.xml
│   │── activity_login.xml
│   │── activity_signup.xml
│   │── activity_main.xml
│   │── activity_recorder.xml
│
│── AndroidManifest.xml
│── build.gradle
│── README.md

🔒 Security & Privacy

User credentials are securely stored in Firebase Authentication.

Emergency contacts are stored only on the user’s device.

Location and recordings are not shared publicly — only sent to user’s chosen contacts.

🌟 Future Enhancements

Integration with WhatsApp / Email for SOS alerts.

Shake detection to trigger SOS automatically.

Cloud backup for recordings.

Panic button widget on the home screen.

👨‍💻 Author

Krishna

🎓 B.Tech Student at Lovely Professional University

💼 Aspiring Software Developer

📧 Contact: krishnajms038@gmail.com

📷 Instagram: @me_ikrishna
