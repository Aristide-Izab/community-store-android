package com.example.communitystore.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
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
 * Residence registration step 3: residence details + appointment letter (PDF).
 * Reached after the residence administrator's email address is verified.
 */
public class ResidenceVerificationActivity extends AppCompatActivity {

    private EditText residenceNameInput;
    private EditText wardenEmailInput;
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
        setContentView(R.layout.activity_auth_residence_verification);

        if (AuthSession.uid() == null) {
            Toast.makeText(this, "Please log in to continue your registration.", Toast.LENGTH_LONG).show();
            AuthSession.logoutToWelcome(this);
            return;
        }

        new SessionManager(this).saveUserRole(UserRole.RESIDENCE);

        residenceNameInput = findViewById(R.id.residenceNameInput);
        wardenEmailInput = findViewById(R.id.wardenEmailInput);
        selectedFileText = findViewById(R.id.selectedFileText);
        submitButton = findViewById(R.id.submitButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        TextView reviewInfo = findViewById(R.id.reviewInfo);

        if (!AccountStatus.DEMO_AUTO_APPROVE) {
            reviewInfo.setText("A residence team member will confirm your role within 1–2 working days.");
            submitButton.setText("Submit for Approval");
        } else {
            submitButton.setText("Submit & Activate");
        }

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Toast.makeText(this, "You can finish this step later by logging in.", Toast.LENGTH_LONG).show();
            AuthSession.logoutToWelcome(this);
        });

        uploadButton.setOnClickListener(v -> documentLauncher.launch(new String[]{"application/pdf"}));
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {

        String residenceName = residenceNameInput.getText().toString().trim();
        String wardenEmail = wardenEmailInput.getText().toString().trim();

        if (residenceName.isEmpty()) {
            residenceNameInput.setError("Enter the residence or campus house");
            residenceNameInput.requestFocus();
            return;
        }

        if (wardenEmail.isEmpty()) {
            wardenEmailInput.setError("Enter the warden email");
            wardenEmailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(wardenEmail).matches()) {
            wardenEmailInput.setError("Enter a valid email address");
            wardenEmailInput.requestFocus();
            return;
        }

        if (pickedFile == null) {
            Toast.makeText(this, "Please choose your appointment letter (PDF).", Toast.LENGTH_SHORT).show();
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
        fields.put("residenceName", residenceName);
        fields.put("wardenEmail", wardenEmail);

        VerificationDocuments.submit(
                        UserRole.RESIDENCE,
                        pickedFile,
                        "Appointment letter",
                        fields
                )
                .addOnSuccessListener(unused -> VerificationDocuments.finish(this, UserRole.RESIDENCE))
                .addOnFailureListener(e -> {
                    submitButton.setEnabled(true);
                    submitButton.setText(label);
                    Toast.makeText(this, "Could not submit: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
