package com.example.communitystore.shared.product;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.communitystore.R;
import com.example.communitystore.core.access.UserRole;
import com.example.communitystore.core.session.AuthSession;
import com.example.communitystore.core.session.SessionManager;
import com.example.communitystore.shared.data.Formatters;
import com.example.communitystore.shared.data.Product;
import com.example.communitystore.shared.data.ProductRepository;
import com.example.communitystore.shared.utils.ThemeUtils;

/**
 * Create or edit a marketplace listing (Student, Faculty and Vendor accounts;
 * Residence uses its own Add Product screen). Saved to Firestore "products".
 *
 * Photos: the free Firebase plan has no Cloud Storage, so a picked photo is
 * only previewed here and listings show a placeholder image.
 */
public class AddListingActivity extends AppCompatActivity {

    private static final String EXTRA_EDIT_ID = "editProductId";

    private static final String[] CONDITIONS = {"New", "Like New", "Good", "Used"};

    private EditText titleInput;
    private EditText priceInput;
    private EditText descriptionInput;
    private EditText locationInput;
    private Spinner categorySpinner;
    private Spinner conditionSpinner;
    private ImageView imagePreview;
    private TextView imageLabel;
    private Button publishButton;

    private String editId;
    private Product editing;

    private final ActivityResultLauncher<String[]> imageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri == null) {
                            return;
                        }
                        imagePreview.setImageURI(uri);
                        imagePreview.setImageTintList(null);
                        imagePreview.setPadding(0, 0, 0, 0);
                        imagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageLabel.setText("Preview only: photos aren't uploaded on the free plan");
                    }
            );

    public static void openNew(Context context) {
        context.startActivity(new Intent(context, AddListingActivity.class));
    }

    public static void openEdit(Context context, String productId) {
        Intent intent = new Intent(context, AddListingActivity.class);
        intent.putExtra(EXTRA_EDIT_ID, productId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        titleInput = findViewById(R.id.titleInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationInput = findViewById(R.id.locationInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        imagePreview = findViewById(R.id.imagePreview);
        imageLabel = findViewById(R.id.imageLabel);
        publishButton = findViewById(R.id.publishButton);

        int color = ThemeUtils.primary(this);
        findViewById(R.id.addListingRoot).setBackgroundColor(color);
        publishButton.setBackgroundTintList(ColorStateList.valueOf(color));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.imagePickerCard).setOnClickListener(v ->
                imageLauncher.launch(new String[]{"image/jpeg", "image/png", "image/webp"})
        );

        setupSpinners();

        findViewById(R.id.previewButton).setOnClickListener(v -> previewProduct());
        publishButton.setOnClickListener(v -> publishProduct());

        editId = getIntent().getStringExtra(EXTRA_EDIT_ID);
        if (editId != null) {
            loadForEdit();
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Product.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CONDITIONS);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void loadForEdit() {
        ((TextView) findViewById(R.id.screenTitle)).setText("Edit Listing");
        publishButton.setText("Save");
        publishButton.setEnabled(false);

        ProductRepository.get(editId)
                .addOnSuccessListener(document -> {
                    publishButton.setEnabled(true);

                    if (!document.exists()) {
                        Toast.makeText(this, "This listing no longer exists.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    editing = Product.fromDocument(document);
                    titleInput.setText(editing.title);
                    priceInput.setText(editing.hasFixedPrice() ? String.valueOf(editing.price) : "");
                    descriptionInput.setText(editing.description);
                    locationInput.setText(editing.location);
                    select(categorySpinner, Product.CATEGORIES, editing.category);
                    select(conditionSpinner, CONDITIONS, editing.condition);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Couldn't load the listing.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void select(Spinner spinner, String[] options, String value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    /** Reads the form; returns null (with an error shown) when invalid. */
    private Product readForm() {
        String title = titleInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim().replace("R", "").replace(",", "");
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Product title is required");
            titleInput.requestFocus();
            return null;
        }

        long price = -1;
        if (!priceText.isEmpty()) {
            try {
                price = Long.parseLong(priceText);
            } catch (NumberFormatException e) {
                priceInput.setError("Enter a whole-number price");
                priceInput.requestFocus();
                return null;
            }

            if (price <= 0) {
                priceInput.setError("Price must be more than R0");
                priceInput.requestFocus();
                return null;
            }
        }

        if (description.isEmpty()) {
            descriptionInput.setError("Tell buyers about the item");
            descriptionInput.requestFocus();
            return null;
        }

        Product product = editing != null ? editing : new Product();
        product.title = title;
        product.price = price;
        product.category = String.valueOf(categorySpinner.getSelectedItem());
        product.condition = "Services".equals(product.category)
                ? ""
                : String.valueOf(conditionSpinner.getSelectedItem());
        product.description = description;
        product.location = location.isEmpty() ? "Campus" : location;
        return product;
    }

    private void previewProduct() {
        Product product = readForm();
        if (product == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(product.title)
                .setMessage(Formatters.price(product.price)
                        + "\n" + product.category
                        + (product.condition.isEmpty() ? "" : " • " + product.condition)
                        + " • " + product.location
                        + "\n\n" + product.description)
                .setPositiveButton(editing == null ? "Publish" : "Save", (d, w) -> publishProduct())
                .setNegativeButton("Keep editing", null)
                .show();
    }

    private void publishProduct() {
        Product product = readForm();
        if (product == null) {
            return;
        }

        String uid = AuthSession.uid();
        if (uid == null) {
            Toast.makeText(this, "Your session expired. Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        SessionManager session = new SessionManager(this);
        UserRole role = session.getUserRole();

        if (editing == null) {
            product.status = Product.STATUS_ACTIVE;
            product.sellerId = uid;
            product.sellerName = session.getDisplayName().isEmpty() ? "Community seller" : session.getDisplayName();
            product.sellerRole = role == null ? "" : role.getValue();
        }

        publishButton.setEnabled(false);
        publishButton.setText(editing == null ? "Publishing..." : "Saving...");

        (editing == null ? ProductRepository.create(product) : ProductRepository.update(product))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            editing == null ? "Listing published!" : "Listing updated.",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    publishButton.setEnabled(true);
                    publishButton.setText(editing == null ? "Publish" : "Save");
                    Toast.makeText(this, "Couldn't save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
