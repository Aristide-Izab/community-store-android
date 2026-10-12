package com.example.communitystore.vendor;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;

public class VendorAddProductActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText priceInput;
    private EditText descriptionInput;
    private EditText locationInput;
    private Spinner categorySpinner;
    private Spinner conditionSpinner;
    private ImageView imagePreview;
    private TextView imageLabel;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<String[]> imageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri == null) {
                            return;
                        }

                        selectedImageUri = uri;
                        imagePreview.setImageURI(uri);
                        imagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageLabel.setText("Image selected");
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_add_product);

        titleInput = findViewById(R.id.titleInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationInput = findViewById(R.id.locationInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        imagePreview = findViewById(R.id.imagePreview);
        imageLabel = findViewById(R.id.imageLabel);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.imagePickerCard).setOnClickListener(v ->
                imageLauncher.launch(new String[]{"image/jpeg", "image/png", "image/webp"})
        );

        setupSpinners();

        findViewById(R.id.previewButton).setOnClickListener(v -> previewProduct());
        findViewById(R.id.publishButton).setOnClickListener(v -> publishProduct());
    }

    private void setupSpinners() {
        String[] categories = {
                "Textbooks",
                "Electronics",
                "Food",
                "Furniture",
                "Services",
                "Other"
        };

        String[] conditions = {
                "New",
                "Like New",
                "Good",
                "Used"
        };

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                conditions
        );
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void previewProduct() {
        String title = titleInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Enter a title and price first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(
                this,
                title + " • R" + price + " • " + categorySpinner.getSelectedItem(),
                Toast.LENGTH_LONG
        ).show();
    }

    private void publishProduct() {
        String title = titleInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim().replace("R", "").replace(",", "");
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Product title is required");
            return;
        }

        if (priceText.isEmpty()) {
            priceInput.setError("Price is required");
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            priceInput.setError("Enter a valid whole-number price");
            return;
        }

        if (location.isEmpty()) {
            location = "Campus";
        }

        VendorProduct product = new VendorProduct(
                title,
                price,
                String.valueOf(categorySpinner.getSelectedItem()),
                String.valueOf(conditionSpinner.getSelectedItem()),
                description,
                location,
                "Active"
        );

        VendorProductStore.addProduct(this, product);

        Toast.makeText(this, "Product published successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
