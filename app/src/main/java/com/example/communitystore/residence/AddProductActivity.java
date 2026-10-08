package com.example.communitystore.residence;

import com.example.communitystore.R;

import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Lets a Residence account publish a product (Figma: Add New Product).
 */
public class AddProductActivity extends AppCompatActivity {

    private static final long MAX_IMAGE_SIZE =
            5 * 1024 * 1024;

    private static final String[] CATEGORIES = {
            "Textbooks",
            "Electronics",
            "Clothing",
            "Furniture",
            "Food",
            "Services",
            "Other"
    };

    private static final String[] CONDITIONS = {
            "New",
            "Like New",
            "Good",
            "Fair"
    };

    private static final String[] LOCATIONS = {
            "Cape Town",
            "Bellville",
            "Mowbray",
            "Wellington",
            "Granger Bay"
    };

    private final Uri[] photoUris =
            new Uri[3];

    private ImageView[] photoImages;
    private ImageView[] photoPluses;

    private EditText titleInput;
    private EditText priceInput;
    private EditText descriptionInput;

    private TextView categoryInput;
    private TextView conditionInput;

    private AutoCompleteTextView locationInput;

    private Button publishButton;

    private int pendingSlot =
            -1;

    private final ActivityResultLauncher<PickVisualMediaRequest>
            photoLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    this::onPhotoPicked
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_product
        );

        ResidenceUi.applyEdgeToEdge(
                this,
                findViewById(R.id.addProductRoot),
                null
        );

        titleInput =
                findViewById(R.id.titleInput);

        priceInput =
                findViewById(R.id.priceInput);

        descriptionInput =
                findViewById(R.id.descriptionInput);

        categoryInput =
                findViewById(R.id.categoryInput);

        conditionInput =
                findViewById(R.id.conditionInput);

        locationInput =
                findViewById(R.id.locationInput);

        publishButton =
                findViewById(R.id.publishButton);

        photoImages =
                new ImageView[]{
                        findViewById(R.id.photoImage1),
                        findViewById(R.id.photoImage2),
                        findViewById(R.id.photoImage3)
                };

        photoPluses =
                new ImageView[]{
                        findViewById(R.id.photoPlus1),
                        findViewById(R.id.photoPlus2),
                        findViewById(R.id.photoPlus3)
                };

        View[] photoSlots = {
                findViewById(R.id.photoSlot1),
                findViewById(R.id.photoSlot2),
                findViewById(R.id.photoSlot3)
        };

        for (int i = 0; i < photoSlots.length; i++) {

            int slot = i;

            photoSlots[i].setOnClickListener(
                    v -> pickPhoto(slot)
            );

            photoSlots[i].setOnLongClickListener(v -> {

                removePhoto(slot);
                return true;
            });
        }

        findViewById(R.id.backButton)
                .setOnClickListener(
                        v -> finish()
                );

        categoryInput.setOnClickListener(v ->
                showChoiceDialog(
                        "Select category",
                        CATEGORIES,
                        categoryInput
                )
        );

        conditionInput.setOnClickListener(v ->
                showChoiceDialog(
                        "Select condition",
                        CONDITIONS,
                        conditionInput
                )
        );

        locationInput.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        LOCATIONS
                )
        );

        locationInput.setOnClickListener(
                v -> locationInput.showDropDown()
        );

        findViewById(R.id.previewButton)
                .setOnClickListener(
                        v -> showPreview()
                );

        publishButton.setOnClickListener(
                v -> validateAndPublish()
        );

        updatePhotoSlots();
    }

    /**
     * Formats a price for display, e.g. 24750 -> "R24,750" and 175.5 -> "R175.50".
     */
    public static String formatPrice(
            @Nullable Double price
    ) {

        if (price == null) {
            return "R0";
        }

        DecimalFormat format =
                new DecimalFormat(
                        price % 1 == 0
                                ? "#,##0"
                                : "#,##0.00",
                        DecimalFormatSymbols.getInstance(Locale.US)
                );

        return "R" + format.format(price);
    }

    private void pickPhoto(
            int slot
    ) {

        pendingSlot = slot;

        photoLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(
                                ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE
                        )
                        .build()
        );
    }

    private void onPhotoPicked(
            @Nullable Uri uri
    ) {

        if (uri == null
                || pendingSlot < 0) {
            return;
        }

        if (!isFileSizeValid(uri)) {

            Toast.makeText(
                    this,
                    "Each photo must be 5MB or smaller.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        photoUris[pendingSlot] = uri;
        pendingSlot = -1;

        updatePhotoSlots();
    }

    private void removePhoto(
            int slot
    ) {

        if (photoUris[slot] == null) {
            return;
        }

        photoUris[slot] = null;

        updatePhotoSlots();

        Toast.makeText(
                this,
                "Photo removed.",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Shows chosen photos and puts the "+" on the first empty slot, as in the design.
     */
    private void updatePhotoSlots() {

        boolean plusShown = false;

        for (int i = 0; i < photoUris.length; i++) {

            boolean hasPhoto =
                    photoUris[i] != null;

            photoImages[i].setVisibility(
                    hasPhoto
                            ? View.VISIBLE
                            : View.GONE
            );

            photoImages[i].setImageURI(
                    hasPhoto
                            ? photoUris[i]
                            : null
            );

            boolean showPlus =
                    !hasPhoto && !plusShown;

            photoPluses[i].setVisibility(
                    showPlus
                            ? View.VISIBLE
                            : View.GONE
            );

            if (showPlus) {
                plusShown = true;
            }
        }
    }

    private void showChoiceDialog(
            String title,
            String[] options,
            TextView target
    ) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(options, (dialog, which) -> {

                    target.setText(options[which]);
                    target.setError(null);
                })
                .show();
    }

    private void showPreview() {

        String title =
                text(titleInput);

        Double price =
                parsePrice();

        String message =
                (title.isEmpty() ? "Untitled product" : title)
                        + "\n"
                        + formatPrice(price)
                        + "\n\nCategory: "
                        + orDash(text(categoryInput))
                        + "\nCondition: "
                        + orDash(text(conditionInput))
                        + "\nLocation: "
                        + orDash(text(locationInput))
                        + "\nPhotos: "
                        + selectedPhotos().size()
                        + "\n\n"
                        + orDash(text(descriptionInput));

        new AlertDialog.Builder(this)
                .setTitle("Preview")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void validateAndPublish() {

        String title =
                text(titleInput);

        String category =
                text(categoryInput);

        String condition =
                text(conditionInput);

        String description =
                text(descriptionInput);

        String location =
                text(locationInput);

        Double price =
                parsePrice();

        if (title.isEmpty()) {

            titleInput.setError("Enter a product title");
            titleInput.requestFocus();
            return;
        }

        if (category.isEmpty()) {

            categoryInput.setError("Select a category");

            showChoiceDialog(
                    "Select category",
                    CATEGORIES,
                    categoryInput
            );

            return;
        }

        if (price == null
                || price <= 0) {

            priceInput.setError("Enter a valid price");
            priceInput.requestFocus();
            return;
        }

        if (condition.isEmpty()) {

            conditionInput.setError("Select a condition");

            showChoiceDialog(
                    "Select condition",
                    CONDITIONS,
                    conditionInput
            );

            return;
        }

        if (description.isEmpty()) {

            descriptionInput.setError("Tell buyers about the product");
            descriptionInput.requestFocus();
            return;
        }

        if (location.isEmpty()) {

            locationInput.setError("Enter a location");
            locationInput.requestFocus();
            return;
        }

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Your session expired. Please log in again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put("title", title);
        data.put("category", category);
        data.put("price", price);
        data.put("condition", condition);
        data.put("description", description);
        data.put("location", location);
        data.put("status", "active");
        data.put("sellerId", user.getUid());
        data.put("sellerRole", "residence");
        data.put("createdAt", FieldValue.serverTimestamp());

        publishButton.setEnabled(false);
        publishButton.setText("Publishing...");

        DocumentReference productRef =
                FirebaseFirestore.getInstance()
                        .collection("products")
                        .document();

        uploadPhotos(
                user.getUid(),
                productRef.getId()
        )
                .continueWithTask(task -> {

                    if (!task.isSuccessful()
                            && task.getException() != null) {

                        throw task.getException();
                    }

                    data.put(
                            "imageUrls",
                            task.getResult()
                    );

                    return productRef.set(data);
                })
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Product published!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    publishButton.setEnabled(true);
                    publishButton.setText("Publish");

                    Toast.makeText(
                            this,
                            "Could not publish product: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private Task<List<String>> uploadPhotos(
            String uid,
            String productId
    ) {

        List<Uri> photos =
                selectedPhotos();

        List<Task<Uri>> uploads =
                new ArrayList<>();

        StorageReference folder =
                FirebaseStorage.getInstance()
                        .getReference()
                        .child(
                                "product_images/"
                                        + uid
                                        + "/"
                                        + productId
                        );

        for (int i = 0; i < photos.size(); i++) {

            StorageReference reference =
                    folder.child("photo_" + (i + 1) + ".jpg");

            uploads.add(
                    reference.putFile(photos.get(i))
                            .continueWithTask(task -> {

                                if (!task.isSuccessful()
                                        && task.getException() != null) {

                                    throw task.getException();
                                }

                                return reference.getDownloadUrl();
                            })
            );
        }

        return Tasks.whenAllSuccess(uploads)
                .continueWith(task -> {

                    if (!task.isSuccessful()
                            && task.getException() != null) {

                        throw task.getException();
                    }

                    List<String> urls =
                            new ArrayList<>();

                    for (Object uri : task.getResult()) {

                        urls.add(uri.toString());
                    }

                    return urls;
                });
    }

    private List<Uri> selectedPhotos() {

        List<Uri> photos =
                new ArrayList<>();

        for (Uri uri : photoUris) {

            if (uri != null) {
                photos.add(uri);
            }
        }

        return photos;
    }

    @Nullable
    private Double parsePrice() {

        String value =
                text(priceInput)
                        .replace("R", "")
                        .replace(",", "")
                        .trim();

        if (value.isEmpty()) {
            return null;
        }

        try {

            return Double.parseDouble(value);

        } catch (NumberFormatException e) {

            return null;
        }
    }

    private boolean isFileSizeValid(
            Uri uri
    ) {

        try (AssetFileDescriptor descriptor =
                     getContentResolver()
                             .openAssetFileDescriptor(uri, "r")) {

            if (descriptor == null) {
                return false;
            }

            long length =
                    descriptor.getLength();

            return length == AssetFileDescriptor.UNKNOWN_LENGTH
                    || length <= MAX_IMAGE_SIZE;

        } catch (IOException e) {

            return false;
        }
    }

    private static String text(
            TextView view
    ) {

        return view.getText()
                .toString()
                .trim();
    }

    private static String orDash(
            String value
    ) {

        return value.isEmpty()
                ? "—"
                : value;
    }
}
