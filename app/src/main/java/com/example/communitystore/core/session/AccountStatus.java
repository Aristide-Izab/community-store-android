package com.example.communitystore.core.session;

/**
 * Registration / verification status stored in users/{uid}.verificationStatus.
 *
 * Flow:
 *   email_pending -> active                                (Student, Faculty)
 *   email_pending -> documents_pending -> active           (Vendor, Residence, demo approval on)
 *   email_pending -> documents_pending -> pending_review   (Vendor, Residence, demo approval off)
 *   pending_review -> active | rejected                    (set by an admin in Firestore)
 */
public final class AccountStatus {

    public static final String EMAIL_PENDING = "email_pending";
    public static final String DOCUMENTS_PENDING = "documents_pending";
    public static final String PENDING_REVIEW = "pending_review";
    public static final String REJECTED = "rejected";
    public static final String ACTIVE = "active";

    /** Older accounts were saved with "approved". It is treated as active. */
    public static final String LEGACY_APPROVED = "approved";

    /**
     * There is no admin dashboard yet, so Vendor and Residence documents are
     * approved immediately for testing and demos.
     *
     * Set to false to leave new Vendor/Residence accounts in "pending_review"
     * until someone changes verificationStatus to "active" in Firestore.
     */
    public static final boolean DEMO_AUTO_APPROVE = true;

    private AccountStatus() {
    }

    public static boolean isActive(String status) {
        return ACTIVE.equals(status) || LEGACY_APPROVED.equals(status);
    }
}
