package com.example.android.imagetotext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class  OcrDetectorProcessor {
    String ocrText;

    public String getOcrText() {
        return ocrText;
    }

    public OcrDetectorProcessor(Context context, Bitmap bitmap){
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(context).build();
        if (!txtRecognizer.isOperational()) {
//            txtView.setText(R.string.error_prompt);
            Log.e("sonal","Error");
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray items = txtRecognizer.detect(frame);
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
                strBuilder.append("/");
                for (Text line : item.getComponents()) {
                    //extract scanned text lines here
                    Log.v("lines", line.getValue());
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        Log.v("element", element.getValue());

                    }
                }
            }
            String text= strBuilder.toString().substring(0, strBuilder.toString().length() - 1);
            this.ocrText=text;
//            Log.e("sonal",text);
//            txtView.setText(strBuilder.toString().substring(0, strBuilder.toString().length() - 1));
        }
    }

}