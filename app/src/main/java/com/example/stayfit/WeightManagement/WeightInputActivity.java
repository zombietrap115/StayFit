package com.example.stayfit.WeightManagement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import com.example.stayfit.R;
import com.example.stayfit.WeightManagement.Database.AppDatabase;
import com.example.stayfit.WeightManagement.Database.WeightEntryAdapter;
import com.example.stayfit.WeightManagement.Database.WeightEntryItem;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(WeightInputActivity.this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
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
}

/*try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/