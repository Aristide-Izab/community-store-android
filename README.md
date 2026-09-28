# Community Store 📱

Community Store is a native Android marketplace application developed as a university group project.

The application is designed to connect members of a university community, including:

- Students
- Faculty members
- Vendors
- Residence administrators

Community Store aims to provide a trusted campus marketplace where users can buy, sell, discover products and interact with services available within their university community.

---

# 📌 Project Background

Community Store originally started as a web-based project.

Our initial technology stack included:

- HTML
- CSS
- JavaScript
- PHP
- MySQL
- XAMPP
- phpMyAdmin

During development, the team realised that the project requirements were for a **mobile application rather than a website**.

Because of this, the team decided to redesign and rebuild the project as a native Android application.

The project was migrated to:

- Android Studio
- Java
- XML
- Firebase Authentication
- Cloud Firestore

This change required the team to rethink the architecture, user interface and authentication system.

---

# 🎯 Project Goal

The goal of Community Store is to create a mobile-first marketplace that allows verified members of a university community to:

- Register using different account types
- Login securely
- Buy and sell products
- Browse marketplace listings
- Communicate within the campus community
- Access community announcements
- Manage their profiles
- Support approved vendors
- Support residence-related communication

---

# 🛠 Technology Stack

## Mobile Development

| Technology | Purpose |
|---|---|
| Android Studio | Main development environment |
| Java | Application logic |
| XML | Android user interface layouts |
| Material Design | UI components and styling |
| Gradle Kotlin DSL | Project dependency and build configuration |

---

## Backend / Cloud Services

| Technology | Purpose |
|---|---|
| Firebase Authentication | User registration, login, email verification and password reset |
| Cloud Firestore | User and application data |
| Firebase Hosting | Android App Link support |
| Android App Links | Opens verification and password reset links inside the application |

---

## UI / UX

| Technology | Purpose |
|---|---|
| Figma | UI/UX design and application prototypes |
| Android XML | Implementation of Figma designs inside Android Studio |

---

## Version Control

| Technology | Purpose |
|---|---|
| Git | Source control |
| GitHub | Team collaboration and repository hosting |
| Git Branches | Allows each team member to work independently |
| Pull Requests | Review changes before merging into `main` |

---

# 🗄 Database

Community Store currently uses **Cloud Firestore** as its main application database.

The previous web implementation used **MySQL**, but after transitioning to Android and Firebase, Firestore became the main database for account-related data.

---

# 🔐 Authentication

Authentication is handled using **Firebase Authentication**.

Currently supported functionality includes:

- Email and password registration
- Email and password login
- Email verification
- Password reset
- Password recovery
- Firebase session management
- Logout
- Account verification checks
- Role-based account detection

Users who have not completed the required verification process are not given access to the main application.

---

# 👥 Account Types

Community Store supports four account types.

## 🎓 Student

Students can create accounts that will eventually allow them to:

- Browse products
- Buy products
- Sell products
- Access campus marketplace features

Student accounts currently require account verification before access is granted.

---

## 💼 Faculty

Faculty users have their own registration type.

Faculty accounts are separated from Student accounts so that the application can identify the account type after login.

Faculty accounts also require verification before access is granted.

---

## 🏪 Vendor

Vendor accounts are intended for businesses or individuals who want to sell products through Community Store.

Vendor onboarding includes an additional verification/approval process.

The Vendor flow includes:

- Vendor registration
- Business verification interface
- Business document submission interface
- Pending approval state

Vendor approval functionality will be completed as the application backend develops further.

---

## 🏠 Residence

Residence accounts are intended for authorised residence representatives or administrators.

Residence onboarding includes:

- Residence registration
- Residence / Campus House information
- Warden information
- Appointment letter submission interface
- Manual approval process

Residence accounts are intended to prevent unauthorised users from posting official residence information.

---

# 📱 Current Application Screens

The onboarding system currently contains the following screens:

1. Splash Screen
2. Welcome Screen
3. Choose Account Type
4. Login
5. Student Registration
6. Faculty Registration
7. Vendor Registration
8. Residence Registration
9. Account Verification
10. Account Verified
11. Forgot Password
12. Reset Password
13. Password Reset Confirmation
14. Vendor Verification
15. Residence Verification

More screens will be added as the marketplace functionality is developed.

---

# ✅ Current Features

| Feature | Status |
|---|---|
| Splash Screen | ✅ Completed |
| Welcome Screen | ✅ Completed |
| Choose Account Type | ✅ Completed |
| Student Registration | ✅ Completed |
| Faculty Registration | ✅ Completed |
| Vendor Registration UI | ✅ Completed |
| Residence Registration UI | ✅ Completed |
| Login | ✅ Completed |
| Firebase Authentication | ✅ Completed |
| Cloud Firestore Integration | ✅ Completed |
| Email Verification | ✅ Completed |
| Password Reset | ✅ Completed |
| Forgot Password | ✅ Completed |
| Account Verified Screen | ✅ Completed |
| Role Detection | ✅ Completed |
| Unverified Account Protection | ✅ Completed |
| Logout | ✅ Testing / Basic Implementation |
| Vendor Approval Backend | 🚧 In Development |
| Residence Approval Backend | 🚧 In Development |
| Marketplace Home | 🚧 To Be Developed |
| Product Management | 🚧 To Be Developed |
| Cart | 🚧 To Be Developed |
| Checkout | 🚧 To Be Developed |
| Profile | 🚧 To Be Developed |
| Community Board | 🚧 To Be Developed |

---

# 🧠 Role-Based Authentication

Community Store stores the user's role so that the application can identify whether the person logging in is a:

```text
student
faculty
vendor
residence
