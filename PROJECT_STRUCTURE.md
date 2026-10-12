# Community Store Package Structure

This repository uses a package-by-feature structure so multiple students can work without mixing responsibilities.

```text
com.example.communitystore
├── MainActivity.java
├── auth/
├── core/
│   ├── access/
│   ├── session/
│   └── firebase/
├── student/
├── faculty/
├── vendor/
├── residence/
└── shared/
    ├── adapters/
    └── utils/
```

## Ownership

- `auth/`: onboarding, registration, login, verification and password recovery.
- `core/access/`: shared role/access rules.
- `core/session/`: shared session/account state.
- `student/`: Student-only Java code.
- `faculty/`: Faculty-only Java code.
- `vendor/`: Vendor-only Java code.
- `residence/`: Residence-only Java code.
- `shared/`: components genuinely reused by multiple features.

Android XML layouts remain in `app/src/main/res/layout/`. Name new layouts with a clear feature prefix such as `activity_student_home.xml` or `activity_vendor_dashboard.xml`.

Coordinate before changing shared files such as `AndroidManifest.xml`, Gradle files, shared Firebase schema/rules, or `core/*`.
