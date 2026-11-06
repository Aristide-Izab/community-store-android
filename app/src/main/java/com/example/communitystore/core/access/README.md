# Access Control

Shared role and permission logic.

- `UserRole.java` - the four account types
- `AccountRouter.java` - decides where a signed-in user goes from `users/{uid}.verificationStatus`
- `AccessManager.java` - opens the dashboard for a role
- `RoleScreens.java` - opens the profile / home screen for the signed-in role
- `BottomNavThemeManager.java`, `QuickAccessThemeManager.java` - role-coloured navigation

Do not create account-specific copies of access-control classes.
