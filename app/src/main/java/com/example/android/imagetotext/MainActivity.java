package com.example.android.imagetotext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    Button btn;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn= findViewById(R.id.button);
        txt= findViewById(R.id.text);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);

    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(MainActivity.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text= getImageText();
                Log.e("sonal",text);
                txt.setText(text);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("Hello ","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission

        }
    }


    public String getImageText(){
        File file= new File(Environment.getExternalStorageDirectory().toString()+"/TestFile");
        File gpxfile = new File(file, "sample.txt");
        FileWriter writer = null;
        String finalText="";
        try {
            writer = new FileWriter(gpxfile);

            String path = Environment.getExternalStorageDirectory().toString()+"/TestPictures";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                String inputFileName= files[i].getAbsolutePath();
                Log.d("Files", "FileName:" + files[i].getName());
                Bitmap bitmap = BitmapFactory.decodeFile(inputFileName);
                OcrDetectorProcessor ocrText= new OcrDetectorProcessor(getApplicationContext(),bitmap);
                String text= ocrText.getOcrText();
                Log.e("sonal",i+" "+text);
                finalText+="Page "+i+":\n"+text+"\n\n";
                writer.append("Page "+i+":\n");
                writer.append(text+"\n\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalText;
    }
}
