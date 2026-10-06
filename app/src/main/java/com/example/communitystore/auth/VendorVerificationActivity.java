package com.example.communitystore.auth;

import com.example.communitystore.R;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VendorVerificationActivity extends AppCompatActivity {

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private TextView backButton;
    private TextView selectedFileText;

    private Button uploadButton;
    private Button submitButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri selectedPdfUri;

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
                                    "PDF must be 10MB or smaller.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        selectedPdfUri = uri;

                        selectedFileText.setText(
                                getFileName(uri)
                        );
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_vendor_verification
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        storage =
                FirebaseStorage.getInstance();

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

        uploadButton.setOnClickListener(v ->
                documentLauncher.launch(
                        new String[]{
                                "application/pdf"
                        }
                )
        );

        submitButton.setOnClickListener(v ->
                submitVendorApplication()
        );
    }

    private void submitVendorApplication() {

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

        if (selectedPdfUri == null) {

            Toast.makeText(
                    this,
                    "Please upload your business document.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        submitButton.setEnabled(false);
        submitButton.setText("Uploading...");

        String uid =
                user.getUid();

        StorageReference reference =
                storage.getReference()
                        .child(
                                "vendor_documents/"
                                        + uid
                                        + "/business_document.pdf"
                        );

        reference.putFile(selectedPdfUri)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        if (task.getException() != null) {
                            throw task.getException();
                        }
                    }

                    return reference.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri ->

                        saveApplication(
                                user,
                                downloadUri.toString()
                        )
                )
                .addOnFailureListener(e -> {

                    submitButton.setEnabled(true);
                    submitButton.setText(
                            "Submit for Review"
                    );

                    Toast.makeText(
                            VendorVerificationActivity.this,
                            "Upload failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveApplication(
            FirebaseUser user,
            String documentUrl
    ) {

        Map<String, Object> vendor =
                new HashMap<>();

        vendor.put(
                "displayName",
                PendingRegistrationStore
                        .getDisplayName(this)
        );

        vendor.put(
                "email",
                PendingRegistrationStore
                        .getEmail(this)
        );

        vendor.put(
                "role",
                "vendor"
        );

        vendor.put(
                "businessRegDocUrl",
                documentUrl
        );

        vendor.put(
                "documentType",
                "Business Registration/CIPC Doc"
        );

        vendor.put(
                "verificationStatus",
                "pending"
        );

        vendor.put(
                "verified",
                false
        );

        vendor.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        db.collection("vendors")
                .document(user.getUid())
                .set(vendor)
                .addOnSuccessListener(unused -> {

                    /*
                     * Vendor is NOT added to users.
                     *
                     * They remain pending until
                     * admin approval.
                     */

                    PendingRegistrationStore.clear(
                            VendorVerificationActivity.this
                    );

                    auth.signOut();

                    submitButton.setEnabled(true);
                    submitButton.setText(
                            "Submit for Review"
                    );

                    showSubmittedDialog();
                })
                .addOnFailureListener(e -> {

                    submitButton.setEnabled(true);
                    submitButton.setText(
                            "Submit for Review"
                    );

                    Toast.makeText(
                            VendorVerificationActivity.this,
                            "Could not submit application: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showSubmittedDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Application Submitted!"
                )
                .setMessage(
                        "Your vendor documents have been submitted for review. Reviews typically take 24–48 hours."
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Back to Login",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            VendorVerificationActivity.this,
                                            LoginActivity.class
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

            android.content.res.AssetFileDescriptor descriptor =
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

            return length <= MAX_FILE_SIZE;

        } catch (IOException e) {

            return false;
        }
    }

    private String getFileName(
            Uri uri
    ) {

        String fileName =
                "Selected PDF";

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
