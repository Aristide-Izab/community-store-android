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

## Project Goal

Community Store aims to provide a mobile-first campus marketplace where members of a university community can:

- Register using different account types
- Verify their accounts
- Log in securely
- Access role-specific dashboards
- Browse marketplace listings
- Buy and sell products
- Interact with sellers
- Manage orders
- Participate in community features
- Access residence-related functionality
- Support approved campus vendors

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

Students can:

- Register and log in
- Verify their email address
- Access the Student dashboard
- Browse the marketplace
- View product information
- View seller profiles
- Add products to cart
- Complete checkout
- View orders
- Participate in the community board
- View messages
- Manage their profile
- Access rewards and leaderboard features

### Faculty

Faculty users have a separate role-based experience that includes:

- Faculty-specific dashboard styling
- Marketplace access
- Product browsing
- Cart and checkout
- Community features
- Messages
- Orders
- Reviews
- Profile management
- Rewards and leaderboard functionality

### Vendor

Vendor accounts provide a seller-focused experience including:

- Vendor dashboard
- Vendor verification
- Product management
- Add Product
- Active and inactive product management
- Income / revenue information
- Leaderboard
- Sales ranking
- Messages
- Vendor profile
- Logout functionality

### Residence

Residence accounts include:

- Residence dashboard
- Product listings
- Add Product
- Sales management
- Messages
- Profile management
- Leaderboard functionality
- Residence-specific navigation

---

## Main Features

### Authentication and Account Management

- Splash screen
- Welcome screen
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

- Marketplace browsing
- Product cards
- Product details
- Multiple product images
- Product image swipe support
- Seller profiles
- Favourite products
- Product categories
- Search
- Filtering
- Sorting
- Shopping cart
- Quantity management
- Product removal
- Checkout
- Saved payment methods
- Payment confirmation
- Order-related screens

### Community

- Student community board
- Faculty community board
- Add Post functionality
- Community navigation
- Role-specific community experiences

### Vendor Features

- Vendor dashboard
- Product management
- Add Product
- Income / Revenue
- Leaderboard
- Sales ranking
- Vendor messages
- Vendor profile
- Vendor verification

### Residence Features

- Residence dashboard
- Listings
- Sales
- Messages
- Profile
- Product management
- Leaderboard

---

## Screenshots

Screenshots of the latest version of Community Store will be added here.

Recommended screenshots:

| Screen | Preview |
|---|---|
| Welcome / Account Selection | Coming soon |
| Student Dashboard | Coming soon |
| Marketplace | Coming soon |
| Product Details | Coming soon |
| Cart / Checkout | Coming soon |
| Faculty Dashboard | Coming soon |
| Vendor Dashboard | Coming soon |
| Vendor Product Management | Coming soon |
| Residence Dashboard | Coming soon |

A dedicated screenshots folder will be added to the repository as the application reaches final presentation stage.

---

## My Contribution

My main responsibility in the project focused on the onboarding, authentication, Student, and Faculty areas of the application.

My work included:

- Building the Android onboarding flow
- Splash screen implementation
- Welcome screen
- Account type selection
- Registration
- Login
- Firebase Authentication integration
- Email verification
- Password reset functionality
- Role-based account routing
- Verification status handling
- Student account flows
- Faculty account flows
- Firebase / Firestore integration
- Marketplace integration
- Shared navigation
- Role-specific UI behaviour
- Student dashboard development
- Faculty dashboard development
- Marketplace screens
- Product Information integration
- Seller Profile integration
- Cart functionality
- Checkout flow
- Community integration
- Testing and integration of multiple account types
- Git and GitHub collaboration
- Project recovery and branch management during team integration

I also contributed to integrating the different account areas into one working Android application.

---

## Project Structure

The application separates functionality by account type while keeping reusable marketplace features inside shared packages.

```text
app/src/main/java/com/example/communitystore/

├── auth/
│   ├── Login
│   ├── Register
│   ├── Password Reset
│   ├── Account Verification
│   ├── Student Verification
│   ├── Vendor Verification
│   └── Residence Verification
│
├── core/
│   ├── access/
│   └── session/
│
├── student/
│   ├── Dashboard
│   ├── Marketplace
│   ├── Community
│   ├── Messages
│   ├── Orders
│   ├── Reviews
│   ├── Leaderboard
│   └── Profile
│
├── faculty/
│   ├── Dashboard
│   ├── Marketplace
│   ├── Community
│   ├── Messages
│   ├── Orders
│   ├── Reviews
│   ├── Leaderboard
│   └── Profile
│
├── vendor/
│   ├── Dashboard
│   ├── Manage Products
│   ├── Add Product
│   ├── Income
│   ├── Leaderboard
│   ├── Sales Rank
│   ├── Messages
│   └── Profile
│
├── residence/
│   ├── Dashboard
│   ├── Listings
│   ├── Sales
│   ├── Messages
│   ├── Leaderboard
│   └── Profile
│
└── shared/
    ├── cart/
    ├── checkout/
    ├── product/
    ├── seller/
    └── utils/
```

This structure keeps role-specific functionality separated while allowing reusable marketplace functionality to be shared between different account types.

---

## Authentication Flow

The application uses Firebase Authentication together with Firestore-based role information.

The general flow is:

```text
Splash
  ↓
Welcome
  ↓
Choose Account Type
  ↓
Register / Login
  ↓
Verification
  ↓
Role Detection
  ↓
Role-Specific Dashboard
```

The application supports:

```text
Student
Faculty
Vendor
Residence
```

After authentication, the application determines the user's role and verification status before routing them to the correct dashboard.

---

## Firebase

Community Store uses Firebase for several backend-related features.

### Firebase Authentication

Used for:

- Registration
- Login
- Email verification
- Password reset
- Authentication sessions

### Cloud Firestore

Used for:

- User account data
- Role information
- Verification information
- Vendor information
- Application-related data

### Firebase Hosting

Used to support Android App Links related to:

- Email verification
- Password reset
- Application link handling

---

## Running the Project

### Requirements

You will need:

- Android Studio
- Android SDK 37
- Java-compatible Android development environment
- Firebase project configured for the application
- Android emulator or physical Android device

---

## Setup

Clone the repository:

```bash
git clone https://github.com/Aristide-Izab/community-store-android.git
```

Open the project in Android Studio.

A valid Firebase configuration file is required locally:

```text
app/google-services.json
```

For security reasons, `google-services.json` is intentionally excluded from the public repository.

You must provide your own Firebase project configuration before using Firebase functionality.

After adding the Firebase configuration:

1. Open the project in Android Studio
2. Sync Gradle
3. Allow dependencies to download
4. Connect an Android device or start an emulator
5. Run the application

---

## Building the Project

The application can also be built from the command line.

### Windows

```powershell
.\gradlew.bat :app:assembleDebug
```

A successful build should return:

```text
BUILD SUCCESSFUL
```

---

## Git Workflow

The project uses a branch-based Git workflow.

Development work should be completed using feature branches.

Example:

```text
main
  ↓
feature/example-feature
  ↓
commit changes
  ↓
push branch
  ↓
Pull Request
  ↓
Review
  ↓
Merge into main
```

The `main` branch is protected.

This prevents accidental direct changes or destructive force pushes to the primary project branch.

---

## Collaboration

Community Store is a group project developed by multiple contributors.

Git and GitHub were used to:

- Separate work using branches
- Merge different account areas
- Review changes through Pull Requests
- Preserve project history
- Integrate individual team member contributions

---

## Security

Sensitive files are excluded from version control.

Examples include:

```text
app/google-services.json
local.properties
*.jks
*.keystore
serviceAccount*.json
*service-account*.json
```

These files are ignored through `.gitignore`.

No passwords, signing keys, private Firebase credentials, or service-account credentials should be committed to the repository.

---

## Repository Protection

The `main` branch is protected to reduce the risk of accidental destructive changes.

Changes should be submitted using Pull Requests rather than direct pushes to `main`.

Force pushing to `main` is disabled.

---

## Project Status

Community Store is currently in active development and final integration testing.

Current work focuses on:

- Final role-based testing
- Student flow testing
- Faculty flow testing
- Vendor flow testing
- Residence flow testing
- Marketplace testing
- Cart and checkout testing
- Firebase integration testing
- UI refinement
- Screenshot preparation
- Public GitHub presentation
- Portfolio and LinkedIn presentation

---

## Future Improvements

Potential future improvements include:

- Real payment gateway integration
- Improved product image upload
- Real-time messaging
- Push notifications
- Advanced marketplace search
- Better recommendation systems
- Admin verification dashboard
- Vendor approval management
- Cloud-hosted product images
- Order tracking
- Improved analytics
- Automated testing
- Continuous Integration using GitHub Actions

---

## Lessons Learned

This project provided experience in:

- Native Android development
- Java
- XML layouts
- Firebase Authentication
- Cloud Firestore
- Android App Links
- Role-based application architecture
- Multi-user application design
- UI/UX implementation
- Git branching
- Pull Requests
- Merge conflict resolution
- Team collaboration
- Debugging
- Integration testing
- Recovering Git history after repository conflicts

The project also demonstrated how quickly application complexity grows when several account types and multiple developers must work within the same codebase.

---

## Team Project

Community Store was developed as a university group project.

The repository contains work contributed by multiple team members across authentication, marketplace functionality, Student functionality, Faculty functionality, Vendor functionality, Residence functionality, UI development, Firebase integration, and testing.

---

## Repository

GitHub:

```text
https://github.com/Aristide-Izab/community-store-android
```

---

## Author Contribution

**Aristide Izabkora**

Application Development Student

Primary contribution areas:

- Android authentication
- Student account functionality
- Faculty account functionality
- Firebase integration
- Marketplace integration
- Shared application functionality
- Multi-role integration
- Git/GitHub project integration
