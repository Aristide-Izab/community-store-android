# Vendor Feature Package

Vendor-only screens (Java + XML), themed with `@color/role_vendor`.

- `VendorHomeActivity.java` - dashboard: points, total sales, orders, active products, recent orders
- `VendorIncomeActivity.java` - income for this week / month / year, worked out from orders
- `VendorLeaderboardActivity.java` - points and merchant perks (unlock with lifetime points)
- `VendorSalesRankActivity.java` - vendor ranking by points
- `VendorMessagesActivity.java` - live chats and message requests (`shared/chat`)
- `VendorProfileActivity.java` - profile, business name, menu and logout
- `VendorUi.java` - small row builders shared by the screens above

Listings are managed with the shared screens `shared/product/MyListingsActivity` and
`shared/product/AddListingActivity`, and saved to Firestore `products`.

`VendorVerificationActivity` stays in `auth` because verification is part of onboarding:
after email verification the vendor enters a business name, picks a document type and a PDF.
The PDF is checked on the phone (Cloud Storage needs the paid Firebase plan) and the
application is saved to `vendors/{uid}`.
