package com.example.texttovoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Aarushi";
    private static final int OPEN_FILE = 1;
    private static final int SAVE_FILE = 2;
    EditText txtFileContents;
    TextToSpeech tts;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void open() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_FILE);
    }

    public void save() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "newfile.txt");
        startActivityForResult(intent, SAVE_FILE);
    }

    public void speak() {
        if (txtFileContents.getText().toString().trim().length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("Nothing to speak. Please type or record some text.");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                        String str = txtFileContents.getText().toString();
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            });
        }
    }

    public void clear(){
        txtFileContents.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtFileContents = findViewById(R.id.txtFileContents);

        Button button1 = findViewById(R.id.btnOpen);
        button1.setOnClickListener(handler);
        Button button2 = findViewById(R.id.btnSave);
        button2.setOnClickListener(handler);
        Button button3 = findViewById(R.id.btnSpeak);
        button3.setOnClickListener(handler);
        Button button4 = findViewById(R.id.btnClear);
        button4.setOnClickListener(handler);

        verifyStoragePermissions(this);
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
               try {
                    Uri uri = data.getData();

                   Cursor returnCursor =
                           getContentResolver().query(uri, null, null, null, null);
                   int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                   returnCursor.moveToFirst();
                   String filename = returnCursor.getString(nameIndex);
                   returnCursor.close();

                    FileInputStream stream = new FileInputStream(new File(Environment.getExternalStorageDirectory(),filename));
                    InputStreamReader reader = new InputStreamReader(stream);
                    BufferedReader br = new BufferedReader(reader);
                    StringBuffer buffer = new StringBuffer();
                    String s = br.readLine();
                    while (s != null) {
                        buffer.append(s + "\n");
                        s = br.readLine();
                    }
                    txtFileContents.setText(buffer.toString().trim());
                    br.close();
                    reader.close();
                    stream.close();
            } catch (Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setTitle("Error");
                    builder.setMessage(ex.getMessage());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }else if(requestCode==2){
            if (resultCode == RESULT_OK) {
               try {
                    Uri uri = data.getData();

                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String filename = returnCursor.getString(nameIndex);
                returnCursor.close();

                    FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),filename));
                    OutputStreamWriter writer = new OutputStreamWriter(stream);
                    BufferedWriter bw = new BufferedWriter(writer);
                    bw.write(txtFileContents.getText().toString(), 0,
                            txtFileContents.getText().toString().length());
                    txtFileContents.setText("");
                    bw.close();
                    writer.close();
                    stream.close();
                } catch (Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setTitle("Error");
                    builder.setMessage(ex.getMessage());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            Button b = (Button) v;
            if (b.getId() == R.id.btnOpen) {
                open();
            }
            if (b.getId() == R.id.btnSave) {
                save();
            }
            if (b.getId() == R.id.btnSpeak) {
                speak();
            }
            if (b.getId() == R.id.btnClear) {
                clear();
            }
        }
    };
}
