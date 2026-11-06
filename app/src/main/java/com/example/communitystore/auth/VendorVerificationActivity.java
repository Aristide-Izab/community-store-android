package com.example.communitystore.auth;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AccountStatus;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Vendor registration step 3: business name + proof of trading (PDF).
 * Reached after the vendor's email address is verified.
 */
public class VendorVerificationActivity extends AppCompatActivity {

    private static final String[] DOCUMENT_TYPES = {
            "Business Registration (CIPC)",
            "Trading Licence",
            "SARS Tax Clearance / Tax Number",
            "Municipal Permit",
            "Other proof of trading"
    };

    private EditText businessNameInput;
    private Spinner documentTypeSpinner;
    private TextView selectedFileText;
    private Button submitButton;

    private VerificationDocuments.PickedFile pickedFile;

    private final ActivityResultLauncher<String[]> documentLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri == null) {
                            return;
                        }

                        String[] error = new String[1];
                        VerificationDocuments.PickedFile file =
                                VerificationDocuments.inspect(this, uri, error);

                        if (file == null) {
                            Toast.makeText(this, error[0], Toast.LENGTH_LONG).show();
                            return;
                        }

                        pickedFile = file;
                        selectedFileText.setText(VerificationDocuments.describe(file));
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_vendor_verification);

        if (AuthSession.uid() == null) {
            Toast.makeText(this, "Please log in to continue your registration.", Toast.LENGTH_LONG).show();
            AuthSession.logoutToWelcome(this);
            return;
        }

        new SessionManager(this).saveUserRole(UserRole.VENDOR);

        businessNameInput = findViewById(R.id.businessNameInput);
        documentTypeSpinner = findViewById(R.id.documentType);
        selectedFileText = findViewById(R.id.selectedFileText);
        submitButton = findViewById(R.id.submitButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        TextView reviewInfo = findViewById(R.id.reviewInfo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DOCUMENT_TYPES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentTypeSpinner.setAdapter(adapter);

        String cachedName = new SessionManager(this).getDisplayName();
        if (!cachedName.isEmpty()) {
            businessNameInput.setHint(cachedName + "'s Store");
        }

        if (!AccountStatus.DEMO_AUTO_APPROVE) {
            reviewInfo.setText("Reviews typically take 24–48 hours. You'll be able to log in once approved.");
            submitButton.setText("Submit for Review");
        } else {
            submitButton.setText("Submit & Activate");
        }

        // Leaving signs out; logging in again brings the vendor back here.
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Toast.makeText(this, "You can finish this step later by logging in.", Toast.LENGTH_LONG).show();
            AuthSession.logoutToWelcome(this);
        });

        uploadButton.setOnClickListener(v -> documentLauncher.launch(new String[]{"application/pdf"}));
        submitButton.setOnClickListener(v -> submit());
    }

    private void submit() {

        String businessName = businessNameInput.getText().toString().trim();

        if (businessName.isEmpty()) {
            businessNameInput.setError("Enter your business or store name");
            businessNameInput.requestFocus();
            return;
        }

        if (pickedFile == null) {
            Toast.makeText(this, "Please choose your PDF document first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (AuthSession.uid() == null) {
            Toast.makeText(this, "Your session expired. Please log in again.", Toast.LENGTH_LONG).show();
            AuthSession.logoutToWelcome(this);
            return;
        }

        String label = submitButton.getText().toString();
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        Map<String, Object> fields = new HashMap<>();
        fields.put("businessName", businessName);

        VerificationDocuments.submit(
                        UserRole.VENDOR,
                        pickedFile,
                        String.valueOf(documentTypeSpinner.getSelectedItem()),
                        fields
                )
                .addOnSuccessListener(unused -> VerificationDocuments.finish(this, UserRole.VENDOR))
                .addOnFailureListener(e -> {
                    submitButton.setEnabled(true);
                    submitButton.setText(label);
                    Toast.makeText(this, "Could not submit: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
