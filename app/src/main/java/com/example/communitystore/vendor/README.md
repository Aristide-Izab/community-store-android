# Vendor Feature Package

Vendor-only screens are implemented in Java + XML to match the rest of the Community Store application.

Included screens:
- `VendorHomeActivity.java` - dashboard and navigation hub
- `VendorManageProductsActivity.java` - active/inactive product list
- `VendorAddProductActivity.java` - local demo product publishing flow
- `VendorIncomeActivity.java` - income/revenue overview
- `VendorLeaderboardActivity.java` - loyalty points and merchant perks
- `VendorSalesRankActivity.java` - vendor sales ranking
- `VendorMessagesActivity.java` - chats and message requests
- `VendorProfileActivity.java` - vendor profile and logout

`VendorVerificationActivity` remains in the shared `auth` package because verification is part of onboarding. The current university-demo flow accepts a selected document without uploading it to Firebase Storage, then creates the approved Vendor records in Firestore.
