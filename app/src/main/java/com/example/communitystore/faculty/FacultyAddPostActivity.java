package com.example.communitystore.faculty;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.communitystore.R;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class FacultyAddPostActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView btnAddImage, imagePlaceholder2, imagePlaceholder3;
    private EditText etPostTitle, etEventDateTime, etLocation, etDescription;
    private AutoCompleteTextView etCategory;
    private MaterialButton btnPreview, btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_post_community);

        initializeViews();
        setupDropdown();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);

        btnAddImage = findViewById(R.id.btnAddImage);
        imagePlaceholder2 = findViewById(R.id.imagePlaceholder2);
        imagePlaceholder3 = findViewById(R.id.imagePlaceholder3);

        etPostTitle = findViewById(R.id.etPostTitle);
        etCategory = findViewById(R.id.etCategory);
        etEventDateTime = findViewById(R.id.etEventDateTime);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);

        btnPreview = findViewById(R.id.btnPreview);
        btnPublish = findViewById(R.id.btnPublish);
    }

    private void setupDropdown() {
        String[] categories = new String[]{"Event", "Announcement"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        if (etCategory != null) {
            etCategory.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnAddImage != null) {
            btnAddImage.setOnClickListener(v ->
                    Toast.makeText(this, "Select image clicked", Toast.LENGTH_SHORT).show()
            );
        }

        if (etEventDateTime != null) {
            etEventDateTime.setOnClickListener(v -> showDatePickerDialog());
        }

        if (btnPreview != null) {
            btnPreview.setOnClickListener(v -> {
                if (validateInputs()) {
                    showPostPreview();
                }
            });
        }

        if (btnPublish != null) {
            btnPublish.setOnClickListener(v -> {
                if (validateInputs()) {
                    publishPost();
                }
            });
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String[] months = {"January", "February", "March", "April", "May", "June",
                            "July", "August", "September", "October", "November", "December"};
                    String formattedDate = selectedDay + " " + months[selectedMonth] + " " + selectedYear;
                    etEventDateTime.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showPostPreview() {
        String title = etPostTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String dateTime = etEventDateTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        String previewSummary = "Title: " + title + "\n" +
                "Category: " + category + " (Appears in All & " + category + ")\n" +
                "Date & Time: " + dateTime + "\n" +
                "Location: " + location + "\n\n" +
                "Description:\n" + description;

        new AlertDialog.Builder(this)
                .setTitle("Post Preview")
                .setMessage(previewSummary)
                .setPositiveButton("Publish", (dialog, which) -> publishPost())
                .setNegativeButton("Back to Edit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void publishPost() {
        String title = etPostTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String dateTime = etEventDateTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("post_title", title);
        resultIntent.putExtra("post_category", category);
        resultIntent.putExtra("post_datetime", dateTime);
        resultIntent.putExtra("post_location", location);
        resultIntent.putExtra("post_description", description);

        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Published to All & " + category + "!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInputs() {
        String title = etPostTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String dateTime = etEventDateTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || dateTime.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}