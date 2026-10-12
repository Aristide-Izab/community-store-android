# Community Store

Community Store is a native Android marketplace application developed as a university group project.

The application is designed to connect students, faculty members, vendors, and residence users within a campus community through a shared mobile marketplace and community platform.

The project allows different account types to register, verify their accounts, access role-specific dashboards, browse marketplace listings, interact with sellers, and use features tailored to their role.

---

## Project Background

Community Store originally started as a web-based project using:

- HTML
- CSS
- JavaScript
- PHP
- MySQL
- XAMPP / phpMyAdmin

During development, the team transitioned the project into a native Android application.

This required the system architecture, user interface, authentication flow, and account management process to be redesigned for Android.

The current version is built primarily with Java and XML in Android Studio and uses Firebase services for authentication and application data.

---

## Current Technology Stack

| Area | Technology |
|---|---|
| Platform | Native Android |
| IDE | Android Studio |
| Programming Language | Java |
| User Interface | XML + Material Components |
| Build System | Gradle Kotlin DSL |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| App Links | Android App Links |
| Link Hosting | Firebase Hosting |
| UI/UX Design | Figma |
| Version Control | Git |
| Collaboration | GitHub |

### Android Configuration

- Package: `com.example.communitystore`
- Minimum SDK: 24
- Target SDK: 37
- Compile SDK: 37

---

## Account Types

Community Store supports four user roles.

### Student

Students can create an account, verify their email address, access the campus marketplace, participate in the community area, manage orders, and use student-specific features.

### Faculty

Faculty members have their own role-based experience with dedicated dashboard styling, marketplace access, community features, profile management, and account verification.

### Vendor

Vendor accounts support a seller-focused experience including:

- Vendor dashboard
- Product management
- Add Product
- Sales and income information
- Leaderboard and sales ranking
- Messages
- Vendor profile
- Vendor verification flow

### Residence

Residence accounts provide residence-focused functionality including dashboards, listings, sales, messaging, profile management, and product-related features.

---

## Main Features

### Authentication and Account Management

- Welcome and onboarding flow
- Account type selection
- Registration
- Login
- Email verification
- Forgot password
- Password reset
- Role-based routing
- Verification status handling
- Logout

### Marketplace

- Product browsing
- Product details
- Multiple product images
- Seller profiles
- Favourite products
- Shopping cart
- Quantity management
- Checkout
- Saved payment methods
- Order-related screens

### Community

- Student community board
- Faculty community board
- Post creation
- Community navigation
- Role-specific community experiences

### Vendor Features

- Vendor dashboard
- Product management
- Add Product
- Income / revenue
- Leaderboard
- Sales ranking
- Vendor messages
- Vendor profile

### Residence Features

- Residence dashboard
- Listings
- Sales
- Messages
- Profile
- Product management

---

## Screenshots

Screenshots of the latest version of Community Store will be added here.

Suggested screens:

| Screen | Preview |
|---|---|
| Account Selection | Coming soon |
| Student Dashboard | Coming soon |
| Marketplace | Coming soon |
| Product Details | Coming soon |
| Faculty Dashboard | Coming soon |
| Vendor Dashboard | Coming soon |
| Vendor Product Management | Coming soon |
| Residence Dashboard | Coming soon |

---

## My Contribution

My main responsibility in the project focused on the onboarding, authentication, Student, and Faculty areas of the application.

My work included:

- Building the Android onboarding flow
- Account type selection
- Registration and login
- Firebase Authentication integration
- Email verification
- Password reset functionality
- Role-based account routing
- Student account flows
- Faculty account flows
- Firebase / Firestore integration
- Marketplace integration
- Shared navigation and role-specific UI behaviour
- Testing and integration of multiple account types
- Git and GitHub collaboration

I also contributed to integrating the different parts of the application into a single working Android project.

---

## Project Structure

The application separates functionality by account type and shared features.

```text
app/src/main/java/com/example/communitystore/

├── auth/
│   ├── Login
│   ├── Register
│   ├── Password Reset
│   └── Account Verification
│
├── core/
│   ├── access/
│   └── session/
│
├── student/
│
├── faculty/
│
├── vendor/
│
├── residence/
│
└── shared/
    ├── cart/
    ├── checkout/
    ├── product/
    ├── seller/
    └── utils/
