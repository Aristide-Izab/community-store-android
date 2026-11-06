package com.example.communitystore.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;

import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AccountStatus;
import com.example.communitystore.core.session.AuthSession;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared logic for the Vendor and Residence document step.
 *
 * The project is on the free Firebase Spark plan, and Cloud Storage for
 * Firebase needs the paid Blaze plan, so the PDF itself is NOT uploaded.
 * The app checks it is a PDF of 10MB or less and records its details
 * (name, size, type) in Firestore with the application.
 */
final class VerificationDocuments {

    static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private VerificationDocuments() {
    }

    /** Selected file details. */
    static final class PickedFile {
        final Uri uri;
        final String name;
        final long size;

        PickedFile(Uri uri, String name, long size) {
            this.uri = uri;
            this.name = name;
            this.size = size;
        }
    }

    /**
     * Reads name and size of a picked document.
     * Returns null with a reason in error[0] when it is not acceptable.
     */
    @Nullable
    static PickedFile inspect(Activity activity, Uri uri, String[] error) {

        String type = activity.getContentResolver().getType(uri);

        if (type != null && !"application/pdf".equals(type)) {
            error[0] = "Please choose a PDF document.";
            return null;
        }

        long size = fileSize(activity, uri);

        if (size > MAX_FILE_SIZE) {
            error[0] = "The PDF must be 10MB or smaller.";
            return null;
        }

        return new PickedFile(uri, fileName(activity, uri), size);
    }

    static String describe(PickedFile file) {
        if (file.size <= 0) {
            return file.name;
        }

        double kb = file.size / 1024.0;
        String size = kb >= 1024
                ? String.format(java.util.Locale.US, "%.1f MB", kb / 1024.0)
                : String.format(java.util.Locale.US, "%.0f KB", kb);

        return file.name + " • " + size;
    }

    /**
     * Saves the application (vendors/{uid} or residences/{uid}) and moves
     * users/{uid} to "active" (demo approval) or "pending_review".
     */
    static Task<Void> submit(
            UserRole role,
            PickedFile file,
            String documentType,
            Map<String, Object> roleFields
    ) {

        String uid = AuthSession.uid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String status = AccountStatus.DEMO_AUTO_APPROVE
                ? AccountStatus.ACTIVE
                : AccountStatus.PENDING_REVIEW;

        Map<String, Object> application = new HashMap<>(roleFields);
        application.put("role", role.getValue());

        // Private to the applicant (see firestore.rules), so contact details can live here.
        com.google.firebase.auth.FirebaseUser account =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        application.put("email", account == null ? "" : account.getEmail());
        application.put("displayName", account == null ? "" : account.getDisplayName());
        application.put("documentType", documentType);
        application.put("documentName", file.name);
        application.put("documentSize", file.size);
        application.put("documentUploaded", false);
        application.put("verificationStatus", status);
        application.put("demoVerification", AccountStatus.DEMO_AUTO_APPROVE);
        application.put("submittedAt", FieldValue.serverTimestamp());

        if (AccountStatus.DEMO_AUTO_APPROVE) {
            application.put("approvedAt", FieldValue.serverTimestamp());
        }

        Map<String, Object> user = new HashMap<>(roleFields);
        user.put("verificationStatus", status);
        user.put("verified", AccountStatus.DEMO_AUTO_APPROVE);

        if (AccountStatus.DEMO_AUTO_APPROVE) {
            user.put("verifiedAt", FieldValue.serverTimestamp());
        }

        String collection = role == UserRole.RESIDENCE ? "residences" : "vendors";

        WriteBatch batch = db.batch();
        batch.set(db.collection(collection).document(uid), application, SetOptions.merge());
        batch.set(db.collection("users").document(uid), user, SetOptions.merge());
        return batch.commit();
    }

    /** After submitting, sign out and show the final screen. */
    static void finish(Activity activity, UserRole role) {
        AuthSession.signOut(activity);

        Intent intent = new Intent(activity, AccountVerifiedActivity.class);
        intent.putExtra("accountType", role.getValue());
        intent.putExtra(AccountVerifiedActivity.EXTRA_PENDING_REVIEW, !AccountStatus.DEMO_AUTO_APPROVE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private static long fileSize(Activity activity, Uri uri) {
        try (AssetFileDescriptor descriptor =
                     activity.getContentResolver().openAssetFileDescriptor(uri, "r")) {
            return descriptor == null ? -1 : descriptor.getLength();
        } catch (Exception e) {
            return -1;
        }
    }

    private static String fileName(Activity activity, Uri uri) {
        String fileName = "document.pdf";

        try (Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    fileName = cursor.getString(index);
                }
            }
        } catch (Exception ignored) {
        }

        return fileName;
    }
}
