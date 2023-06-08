package com.example.stayfit.WeightManagement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.stayfit.R;
import com.example.stayfit.WeightManagement.Database.AppDatabase;
import com.example.stayfit.WeightManagement.Database.WeightEntryAdapter;
import com.example.stayfit.WeightManagement.Database.WeightEntryItem;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightInputActivity extends AppCompatActivity {
    AppDatabase db;
    private List<WeightEntryItem> weightEntryItems = new ArrayList<>();
    private WeightEntryAdapter adapter;
    EditText weightInput;
    Bitmap bitmap;
    Button saveButton;
    Button cameraButton;
    Button deleteButton;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_input);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        weightInput = findViewById(R.id.weightInput);
        saveButton = findViewById(R.id.saveButton);
        cameraButton = findViewById(R.id.camera);

        if(ContextCompat.checkSelfPermission(WeightInputActivity.this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WeightInputActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE );
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        saveButton.setOnClickListener(v -> {

            String input = weightInput.getText().toString();

            if(input.isEmpty()){
                Toast.makeText(WeightInputActivity.this, "Please enter weight", Toast.LENGTH_SHORT).show();
            }else {
                float weight = Float.parseFloat(input);
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                AppDatabase.WeightEntry entry = new AppDatabase.WeightEntry(0, date, weight);

                new Thread(() -> {
                    db.weightEntryDao().insertAll(entry);
                }).start();

                Toast.makeText(WeightInputActivity.this, "Weight saved", Toast.LENGTH_SHORT).show();
            }
        });

        db.weightEntryDao().getAll().observe(this, weightEntries -> {
            Collections.reverse(weightEntries);
            weightEntryItems = new ArrayList<>();
            for (AppDatabase.WeightEntry entry : weightEntries) {
                weightEntryItems.add(new WeightEntryItem(entry.id, entry.date, entry.weight));
            }

            RecyclerView weightList = findViewById(R.id.weightList);
            weightList.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WeightEntryAdapter(weightEntryItems);
            weightList.setAdapter(adapter);
        });

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = weightEntryItems.size() - 1; i >= 0; i--) {
                    WeightEntryItem item = weightEntryItems.get(i);
                    if (item.selected) {
                        new Thread(() -> {
                            AppDatabase.WeightEntry entry = new AppDatabase.WeightEntry(item.id, item.date, item.weight);
                            db.weightEntryDao().delete(entry);
                        }).start();
                        weightEntryItems.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.stayfit.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
            CropImage.activity(imageUri).start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(WeightInputActivity.this, "No weight found", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            weightInput.setText(stringBuilder.toString());
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


/*try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/