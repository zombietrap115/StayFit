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

/**
 * Activity zur Eingabe von Gewichtsinformationen.
 */
public class WeightInputActivity extends AppCompatActivity {

    // Deklaration von Klassenvariablen
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

    /**
     * Wird aufgerufen, wenn die Aktivität startet.
     * Initialisiert die Benutzeroberfläche, Datenbank und richtet Button-Listener ein.
     *
     * @param savedInstanceState Wenn nicht null, wird diese Aktivität aus einem zuvor gespeicherten Zustand neu erstellt.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_input);

        // Datenbank initialisieren
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        // UI-Elemente finden
        weightInput = findViewById(R.id.weightInput);
        saveButton = findViewById(R.id.saveButton);
        cameraButton = findViewById(R.id.camera);

        // Kameraberechtigungen prüfen und sonst anfordern
        if(ContextCompat.checkSelfPermission(WeightInputActivity.this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WeightInputActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE );
        }

        // OnClickListener für Kamera einrichten
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Methode zum Starten der Kamera
                dispatchTakePictureIntent();
            }
        });

        // OnClickListener für Speichern-Knopf
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gewichtseingabe abrufen
                String input = weightInput.getText().toString();

                // Überprüfen, ob Eingabe leer ist
                if(input.isEmpty()){
                    Toast.makeText(WeightInputActivity.this, "Please enter weight", Toast.LENGTH_SHORT).show();
                } else {
                    // Gewicht und Datum speichern
                    float gewicht = Float.parseFloat(input); //String in Float umwandeln
                    String datum = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); //Aktuelles Datum in dem Format speichern
                    AppDatabase.WeightEntry eintrag = new AppDatabase.WeightEntry(0, datum, gewicht); // Objekt mit den Daten erstellen

                    // Daten in einem separaten Thread in die Datenbank schreiben
                    new Thread(() -> {
                        db.weightEntryDao().insertAll(eintrag);
                    }).start();

                    Toast.makeText(WeightInputActivity.this, "Weight saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Daten aus der Datenbank abrufen und in der RecyclerView anzeigen
        db.weightEntryDao().getAll().observe(this, weightEntries -> {
            Collections.reverse(weightEntries); // Datensatzreihenfolge umdrehen
            weightEntryItems = new ArrayList<>();

            // Für jeden Datensatz in der Collection in die ArrayList hinzufügen
            for (AppDatabase.WeightEntry entry : weightEntries) {
                weightEntryItems.add(new WeightEntryItem(entry.id, entry.date, entry.weight));
            }

            // RecyclerView finden
            RecyclerView weightList = findViewById(R.id.weightList);
            weightList.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WeightEntryAdapter(weightEntryItems); //Dem Adapter alle Datensätze übergeben
            weightList.setAdapter(adapter);
        });

        deleteButton = findViewById(R.id.deleteButton);

        // OnClickListener für Löschen-Knopf
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Jeder Eintrag wird durchgelaufen
                for (int i = weightEntryItems.size() - 1; i >= 0; i--) {
                    WeightEntryItem item = weightEntryItems.get(i);

                    // und überpüft ob es ausgewählt wurde
                    if (item.selected) {
                        // wenn ja, in einem neuen Thread entfernen aus Datenbank entfernen
                        new Thread(() -> {
                            AppDatabase.WeightEntry entry = new AppDatabase.WeightEntry(item.id, item.date, item.weight);
                            db.weightEntryDao().delete(entry);
                        }).start();
                        // Aus RecyclerView entfernen
                        weightEntryItems.remove(i);
                    }
                }
                // Adapter aktualisieren
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Startet die Kamera-App, um ein Foto aufzunehmen.
     */
    private void dispatchTakePictureIntent() {

        // Intent zum Aufnehmen eines Fotos
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Überprüfen, ob eine Kamera-App verfügbar ist
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Datei für das Foto erstellen
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Überprüft ob Datei ertsellt wurde
            if (photoFile != null) {
                // Foto-URI erstellen und Intent übergeben
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.stayfit.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Kamera-App starten
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Erstellt eine temporäre Datei zum Speichern des Fotos.
     *
     * @return Die erstellte Datei.
     * @throws IOException Wenn ein Fehler beim Erstellen der Datei auftritt.
     */
    private File createImageFile() throws IOException {

        // Zeitstempel für den Dateinamen generieren
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Pfad der Datei speichern
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Wird aufgerufen, wenn die Kamera-App ein Foto gemacht hat.
     *
     * @param requestCode Der Integer-Code, der die Anfrage identifiziert.
     * @param resultCode  Der Integer-Code, der das Ergebnis identifiziert.
     * @param data        Ein Intent, der die Rückgabedaten enthält.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Überprüfen, ob das Ergebnis von der Kamera-App stammt
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Nachdem ein Foto aufgenommen wurde, wird ein URI des Bildes erstellt.
            Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
            // CropActivity wird gestratet und das Bild übergeben
            CropImage.activity(imageUri).start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Überprüfen, ob das Ergebnis von der CropImage-Aktivität stammt
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Erhalten der URI des zugeschnittenen Bildes
                Uri resultUri = result.getUri();
                try {
                    // Konvertieren der URI in ein Bitmap-Objekt
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    // Aufruf der Methode, um Text aus dem Bild zu extrahieren
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Extrahiert Text aus einem Bitmap-Bild.
     *
     * @param bitmap Das Bitmap-Bild, aus dem Text extrahiert werden soll.
     */
    private void getTextFromImage(Bitmap bitmap) {

        // Texterkennung initialisieren
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();

        // Überprüfen, ob Texterkennung verfügbar ist
        if (!recognizer.isOperational()) {
            Toast.makeText(WeightInputActivity.this, "No weight found", Toast.LENGTH_SHORT).show();
        } else {
            // Erstellen eines Frame-Objekts aus der Bitmap, das zur Texterkennung verwendet wird
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            // Durchführen der Texterkennung
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            // Schleife durch alle erkannten Textblöcke und Anhängen des Textes an stringBuilder
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            // Gefundenen Text im Gewichtseingabefeld anzeigen
            weightInput.setText(stringBuilder.toString());
        }
    }
}