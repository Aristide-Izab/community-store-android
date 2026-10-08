# Community Store 📱

Community Store is a native Android marketplace application developed as a university group project. It is designed to connect students, faculty members, vendors and residence administrators within a campus community.

The project focuses on creating a trusted mobile marketplace where users can register, verify their accounts, browse and sell products, and interact with services and information relevant to the university community.

---

## 📌 Project background

Community Store originally started as a web-based project using:

- HTML
- CSS
- JavaScript
- PHP
- MySQL
- XAMPP / phpMyAdmin

During development, the team realised that the required deliverable was a **mobile application rather than a website**. The project was therefore rebuilt as a native Android application.

The current Android implementation uses:

- **Android Studio**
- **Java** for application logic
- **XML** for Android layouts
- **Firebase Authentication** for user authentication
- **Cloud Firestore** for account/application data
- **Firebase Hosting + Android App Links** for verification/password-reset links
- **Figma** for UI/UX design
- **Git and GitHub** for collaboration and version control

This transition required the team to rethink the application architecture, user interface and authentication flow.

---

## 🎯 Project goal

Community Store aims to provide a mobile-first campus marketplace that allows verified members of a university community to:

- Register using different account types
- Login securely
- Buy and sell products
- Browse marketplace listings
- Manage their profile
- Access community announcements and services
- Support approved vendors
- Support residence-related communication

---

## 🛠 Technology stack

| Area | Technology |
|---|---|
| Platform | Native Android |
| IDE | Android Studio |
| Programming language | Java |
| UI | XML + Material components |
| Build system | Gradle Kotlin DSL |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| App-link hosting | Firebase Hosting |
| Deep links | Android App Links |
| UI/UX design | Figma |
| Version control | Git |
| Repository / collaboration | GitHub |

### Android configuration

- Package: `com.example.communitystore`
- Minimum SDK: 24
- Target SDK: 37
- Compile SDK: 37

---

## 👥 Account types

Community Store supports four account types:

### 🎓 Student
Students can register, verify their account and access marketplace functionality intended for the campus community.

### 💼 Faculty
Faculty accounts are handled separately so the application can identify the user's role after login.

### 🏪 Vendor
Vendor onboarding includes a business-verification interface and a manual approval concept before vendor access is granted.

### 🏠 Residence
Residence onboarding includes residence/campus-house information, warden information and an appointment-letter approval concept for authorised residence representatives.

---

## 🔐 Authentication and account flow

Authentication is handled with Firebase Authentication. Current onboarding/authentication functionality includes:

- Email/password registration
- Email/password login
- Firebase email verification
- Forgot-password flow
- Password reset through Firebase links
- Account verification checks
- Role-based account detection
- Logout / session handling

Student and Faculty accounts currently follow this general flow:

```text
Register
   ↓
Firebase Authentication account created
   ↓
Verification email sent
   ↓
User opens verification link
   ↓
Verified Firestore user profile created
   ↓
Account Verified screen
   ↓
Login
   ↓
Role retrieved from Firestore
   ↓
Application access
```

Password recovery follows this general flow:

```text
Login
   ↓
Forgot Password
   ↓
Enter email
   ↓
Firebase password-reset email
   ↓
Android App Link opens the app
   ↓
Reset Password
   ↓
Password Reset Success
   ↓
Login
```

---

## 🗄 Database

The current Android version uses **Cloud Firestore**.

The previous web implementation used **MySQL**, but the Android rebuild moved account-related data to Firebase/Firestore.

Current role values used by the application include:

```text
student
faculty
vendor
residence
```

---

## 📱 Current onboarding screens

The current Android project includes screens/activities for:

1. Splash
2. Welcome
3. Choose Account Type
4. Login
5. Registration
6. Account verification
7. Account Verified
8. Forgot Password
9. Reset Password
10. Password Reset confirmation
11. Vendor Verification
12. Residence Verification
13. Main / temporary test area
14. Logout / temporary test flow

---

## ✅ Current project status

| Feature | Status |
|---|---|
| Splash screen | ✅ Implemented |
| Welcome screen | ✅ Implemented |
| Account-type selection | ✅ Implemented |
| Student registration | ✅ Implemented |
| Faculty registration | ✅ Implemented |
| Login | ✅ Implemented |
| Firebase Authentication | ✅ Implemented |
| Cloud Firestore integration | ✅ Implemented |
| Email verification | ✅ Implemented |
| Forgot Password | ✅ Implemented |
| Reset Password | ✅ Implemented |
| Account Verified screen | ✅ Implemented |
| Role detection | ✅ Implemented |
| Unverified-account protection | ✅ Implemented |
| Vendor onboarding UI | ✅ Implemented |
| Residence onboarding UI | ✅ Implemented |
| Vendor approval backend | 🚧 Not final |
| Residence approval backend | 🚧 Not final |
| Marketplace home | 🚧 To be developed |
| Product management | 🚧 To be developed |
| Cart / checkout | 🚧 To be developed |
| Profile | 🚧 To be developed |
| Community board | 🚧 To be developed |

---

## 👨‍💻 Team members and responsibilities

Each student should add their own name, role and assigned responsibilities below.

### Aristide Izabkora

**Role:** Phase 1 — Onboarding, Account Setup and Authentication

Responsibilities:

- Splash screen
- Welcome screen
- Account-type selection
- Student / Faculty / Vendor / Residence registration flows
- Login
- Firebase Authentication integration
- Cloud Firestore account integration
- Email verification
- Account Verified flow
- Forgot Password
- Reset Password
- Password-reset confirmation
- Role-based login detection
- Preventing unverified users from accessing the app
- Android App Links
- Firebase Hosting configuration for auth links
- Translating onboarding Figma designs into Android XML
- Community Store logo / onboarding UI integration

### Shaakirah Hoedemaker 

**Role:** Phase 2 — UI/UX Design and Student & Faculty Application Development

**Responsibilities:**

**UI/UX Design**

* Designed all Community Store application screens and user interfaces in Figma
* Created the UI designs and layouts for the Student, Faculty, Vendor and Residence account types
* Maintained consistent navigation, branding, colours, layouts and components across the application
* Designed account-specific dashboards and functionality according to the project requirements

**Student Account — Android Development**

* Community Board screen
* My Orders screen
* View Order / Order Tracking screen
* Add Review screen
* Translated the approved Figma designs for these screens into Android XML
* Implemented the required UI interactions and navigation between these screens

**Faculty Account — Android Development**

* Community Board screen
* My Orders screen
* View Order / Order Tracking screen
* Add Review screen
* Add Post screen
* Translated the approved Figma designs for these screens into Android XML
* Implemented the required UI interactions and navigation between these screens

**UI Testing and Integration**

* Tested the implemented screens in Android Studio
* Checked screen layouts and navigation against the Figma designs
* Assisted with maintaining a consistent user experience across the different account types

### Team Member 3

**Name:** Add name here  
**Role:** Add role here

Responsibilities:
- Add responsibility
- Add responsibility
- Add responsibility

### Team Member 4

**Name:** Add name here  
**Role:** Add role here

Responsibilities:
- Add responsibility
- Add responsibility
- Add responsibility

---

## 🤝 Contributing

This is a shared school project. **Do not work directly on `main`.**

Every team member should read [CONTRIBUTING.md](CONTRIBUTING.md) before making changes.

The normal workflow is:

```text
Update main
   ↓
Create your own feature branch
   ↓
Code and test
   ↓
Commit your work
   ↓
Push your branch
   ↓
Create a Pull Request
   ↓
Teammate review
   ↓
Merge into main
```

---

## 💻 Running the project

### Requirements

- Android Studio
- Git
- Android SDK
- Internet access for Gradle/Firebase dependencies
- Emulator or physical Android device

### Clone

```bash
git clone YOUR_REPOSITORY_URL
cd community-store-android
```

Open the cloned project folder in Android Studio and allow Gradle to sync.

### Firebase configuration

The Android package is:

```text
com.example.communitystore
```

The application uses Firebase project configuration through:

```text
app/google-services.json
```

If the team chooses not to store that file in GitHub, obtain the approved copy privately from the project owner and place it inside `app/`.

---

## 📂 Important project structure

```text
community-store-android/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/main/
│       ├── java/com/example/communitystore/
│       ├── res/
│       └── AndroidManifest.xml
├── gradle/
├── public/
│   └── .well-known/assetlinks.json
├── .firebaserc
├── .gitignore
├── CONTRIBUTING.md
├── README.md
├── build.gradle.kts
├── firebase.json
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

---

## 🔒 Security and repository hygiene

Do not commit:

- `local.properties`
- `.gradle/`
- `build/` or `app/build/`
- `.idea/`
- keystore/signing files
- service-account credentials
- passwords or API secrets
- real student/user personal data

The repository `.gitignore` is configured to exclude common generated/local files.

---

## ⚠️ Known development note

The current Vendor and Residence verification activities still contain document-upload code that references Firebase Storage. The team decided not to rely on paid Firebase services at this stage, so the final production document-storage/approval backend should be reviewed before those flows are considered complete.

---

## 🎓 Academic project

Community Store is being developed as a university group assignment. This repository is intended for academic collaboration, software-development practice, team version control and project submission.

No public/open-source licence has been assigned unless the team and academic requirements later specify otherwise.
