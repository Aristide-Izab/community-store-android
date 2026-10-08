package com.example.communitystore.auth;

import com.example.communitystore.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
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

public class ResidenceVerificationActivity extends AppCompatActivity {

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private TextView backButton;
    private TextView selectedFileText;

    private EditText residenceNameInput;
    private EditText wardenEmailInput;

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
                R.layout.activity_auth_residence_verification
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

        residenceNameInput =
                findViewById(
                        R.id.residenceNameInput
                );

        wardenEmailInput =
                findViewById(
                        R.id.wardenEmailInput
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

        submitButton.setOnClickListener(
                v -> validateAndSubmit()
        );
    }

    private void validateAndSubmit() {

        String residenceName =
                residenceNameInput
                        .getText()
                        .toString()
                        .trim();

        String wardenEmail =
                wardenEmailInput
                        .getText()
                        .toString()
                        .trim();

        if (residenceName.isEmpty()) {

            residenceNameInput.setError(
                    "Enter the residence or campus house"
            );

            residenceNameInput.requestFocus();
            return;
        }

        if (wardenEmail.isEmpty()) {

            wardenEmailInput.setError(
                    "Enter the warden email"
            );

            wardenEmailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(wardenEmail)
                .matches()) {

            wardenEmailInput.setError(
                    "Enter a valid email address"
            );

            wardenEmailInput.requestFocus();
            return;
        }

        if (selectedPdfUri == null) {

            Toast.makeText(
                    this,
                    "Please upload your appointment letter.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        uploadResidenceApplication(
                residenceName,
                wardenEmail
        );
    }

    private void uploadResidenceApplication(
            String residenceName,
            String wardenEmail
    ) {

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

        submitButton.setEnabled(false);
        submitButton.setText("Uploading...");

        String uid =
                user.getUid();

        StorageReference reference =
                storage.getReference()
                        .child(
                                "residence_documents/"
                                        + uid
                                        + "/appointment_letter.pdf"
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

                        saveResidenceApplication(
                                user,
                                residenceName,
                                wardenEmail,
                                downloadUri.toString()
                        )
                )

                .addOnFailureListener(e -> {

                    submitButton.setEnabled(true);

                    submitButton.setText(
                            "Submit for Approval"
                    );

                    Toast.makeText(
                            this,
                            "Upload failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveResidenceApplication(
            FirebaseUser user,
            String residenceName,
            String wardenEmail,
            String documentUrl
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "displayName",
                PendingRegistrationStore
                        .getDisplayName(this)
        );

        data.put(
                "email",
                PendingRegistrationStore
                        .getEmail(this)
        );

        data.put(
                "role",
                "residence"
        );

        data.put(
                "residenceName",
                residenceName
        );

        data.put(
                "wardenEmail",
                wardenEmail
        );

        data.put(
                "appointmentLetterUrl",
                documentUrl
        );

        data.put(
                "verificationStatus",
                "pending"
        );

        data.put(
                "verified",
                false
        );

        data.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        db.collection("residences")
                .document(user.getUid())
                .set(data)

                .addOnSuccessListener(unused -> {

                    PendingRegistrationStore.clear(
                            ResidenceVerificationActivity.this
                    );

                    auth.signOut();

                    submitButton.setEnabled(true);

                    submitButton.setText(
                            "Submit for Approval"
                    );

                    showSubmittedDialog();
                })

                .addOnFailureListener(e -> {

                    submitButton.setEnabled(true);

                    submitButton.setText(
                            "Submit for Approval"
                    );

                    Toast.makeText(
                            this,
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
                        "Your residence administrator application has been submitted. A residence team member will review your role within 1–2 working days."
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Back to Login",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            ResidenceVerificationActivity.this,
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
