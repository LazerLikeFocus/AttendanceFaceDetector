package com.lazerlikefoucs.attendancefacedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.mlkit.vision.common.InputImage;

public class ResultActivity extends AppCompatActivity {

    ImageView photo_iv;
    ImageButton restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        photo_iv = findViewById(R.id.imageView_photo);
        restart = findViewById(R.id.imageView2_reset);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");

        byte data[] = android.util.Base64.decode(message, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        //InputImage output = InputImage.fromBitmap(bmp, 0);

        photo_iv.setImageBitmap(bmp);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}