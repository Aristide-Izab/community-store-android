# Residence Feature Package

Residence screens, hosted as tabs in `ResidenceHomeActivity` (Home, Listing, Sales, Messages, Profile).

- `ResidenceDashboardFragment.java` - points, total sales, orders, products, recent orders
- `ResidenceListingsFragment.java` - own listings (activate/deactivate, view, long-press to delete)
- `AddProductActivity.java` - add a listing (photos are preview-only on the free Firebase plan)
- `ResidenceSalesFragment.java` - income for this week / month / year from orders
- `ResidenceMessagesFragment.java` - live chats and requests (`shared/chat`)
- `ResidenceProfileFragment.java` - profile, post announcements, settings, logout
- `LeaderboardActivity.java` - residence ranking and merchant perks

Residence registration/verification stays in the `auth` package because it is part of onboarding.
