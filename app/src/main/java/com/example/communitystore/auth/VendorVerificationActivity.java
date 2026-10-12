package com.example.communitystore.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.vendor.VendorHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VendorVerificationActivity
        extends AppCompatActivity {

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private TextView backButton;
    private TextView selectedFileText;

    private Button uploadButton;
    private Button submitButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Uri selectedDocumentUri;
    private String selectedDocumentName =
            "Selected document";

    private final ActivityResultLauncher<String[]>
            documentLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        if (!isFileSizeValid(uri)) {

                            Toast.makeText(
                                    this,
                                    "Document must be 10MB or smaller.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        selectedDocumentUri = uri;

                        selectedDocumentName =
                                getFileName(uri);

                        selectedFileText.setText(
                                selectedDocumentName
                        );
                    }
            );

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_auth_vendor_verification
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        backButton =
                findViewById(
                        R.id.backButton
                );

        selectedFileText =
                findViewById(
                        R.id.selectedFileText
                );

        uploadButton =
                findViewById(
                        R.id.uploadButton
                );

        submitButton =
                findViewById(
                        R.id.submitButton
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        uploadButton.setOnClickListener(
                v -> documentLauncher.launch(
                        new String[]{
                                "*/*"
                        }
                )
        );

        submitButton.setOnClickListener(
                v -> approveVendorForDemo()
        );
    }

    private void approveVendorForDemo() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Registration session was lost.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (selectedDocumentUri == null) {

            Toast.makeText(
                    this,
                    "Please select a document first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        submitButton.setEnabled(false);

        submitButton.setText(
                "Verifying..."
        );

        String uid =
                user.getUid();

        String displayName =
                PendingRegistrationStore
                        .getDisplayName(this);

        String email =
                PendingRegistrationStore
                        .getEmail(this);

        if (displayName == null
                || displayName.trim().isEmpty()) {

            displayName =
                    user.getDisplayName();

            if (displayName == null
                    || displayName.trim().isEmpty()) {

                displayName =
                        "Vendor";
            }
        }

        if (email == null
                || email.trim().isEmpty()) {

            email =
                    user.getEmail();
        }

        Map<String, Object> vendor =
                new HashMap<>();

        vendor.put(
                "displayName",
                displayName
        );

        vendor.put(
                "email",
                email
        );

        vendor.put(
                "role",
                "vendor"
        );

        vendor.put(
                "documentName",
                selectedDocumentName
        );

        vendor.put(
                "documentSelected",
                true
        );

        vendor.put(
                "verificationStatus",
                "approved"
        );

        vendor.put(
                "verified",
                true
        );

        vendor.put(
                "demoVerification",
                true
        );

        vendor.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        vendor.put(
                "approvedAt",
                FieldValue.serverTimestamp()
        );

        Map<String, Object> userDocument =
                new HashMap<>();

        userDocument.put(
                "displayName",
                displayName
        );

        userDocument.put(
                "email",
                email
        );

        userDocument.put(
                "role",
                "vendor"
        );

        userDocument.put(
                "verified",
                true
        );

        userDocument.put(
                "verificationStatus",
                "approved"
        );

        userDocument.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        DocumentReference vendorReference =
                db.collection("vendors")
                        .document(uid);

        DocumentReference userReference =
                db.collection("users")
                        .document(uid);

        WriteBatch batch =
                db.batch();

        batch.set(
                vendorReference,
                vendor
        );

        batch.set(
                userReference,
                userDocument
        );

        batch.commit()
                .addOnSuccessListener(
                        unused -> {

                            PendingRegistrationStore.clear(
                                    VendorVerificationActivity.this
                            );

                            submitButton.setEnabled(true);

                            submitButton.setText(
                                    "Submit for Review"
                            );

                            showApprovedDialog();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            submitButton.setEnabled(true);

                            submitButton.setText(
                                    "Submit for Review"
                            );

                            Toast.makeText(
                                    VendorVerificationActivity.this,
                                    "Could not verify vendor: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void showApprovedDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Vendor Verified!"
                )
                .setMessage(
                        "Your document has been accepted for testing. "
                                + "Your Vendor account is now active."
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            VendorVerificationActivity.this,
                                            VendorHomeActivity.class
                                    );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();
                        }
                )
                .show();
    }

    private boolean isFileSizeValid(
            Uri uri
    ) {

        try {

            android.content.res.AssetFileDescriptor
                    descriptor =
                    getContentResolver()
                            .openAssetFileDescriptor(
                                    uri,
                                    "r"
                            );

            if (descriptor == null) {
                return false;
            }

            long length =
                    descriptor.getLength();

            descriptor.close();

            return length < 0
                    || length <= MAX_FILE_SIZE;

        } catch (IOException e) {

            return false;
        }
    }

    private String getFileName(
            Uri uri
    ) {

        String fileName =
                "Selected document";

        Cursor cursor =
                getContentResolver()
                        .query(
                                uri,
                                null,
                                null,
                                null,
                                null
                        );

        if (cursor != null) {

            int index =
                    cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                    );

            if (cursor.moveToFirst()
                    && index >= 0) {

                fileName =
                        cursor.getString(index);
            }

            cursor.close();
        }

        return fileName;
    }
}