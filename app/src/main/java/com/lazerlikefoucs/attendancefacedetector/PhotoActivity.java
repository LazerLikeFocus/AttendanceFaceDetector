package com.lazerlikefoucs.attendancefacedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;

public class PhotoActivity extends AppCompatActivity {

    private ImageButton camera, reset;
    private TextView people;
    private Bitmap bitmap;
    private ImageView photo;
    private ProgressBar progressBar;

    private CameraKitView cameraKitView;
    private InputImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        camera = findViewById(R.id.imageButton_camera);
        cameraKitView = findViewById(R.id.camerakit);
        photo = findViewById(R.id.imageView_photo_test);
        reset = findViewById(R.id.imageView2_reset_test);
        people = findViewById(R.id.textView_people_test);
        progressBar = findViewById(R.id.progressBar);

        photo.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        people.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);


        cameraKitView.setFacing(CameraKit.FACING_BACK);

        camera.setOnClickListener(view -> decode());

        reset.setOnClickListener(view -> resetall());

    }

    private void resetall() {
        photo.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        people.setVisibility(View.INVISIBLE);
        camera.setVisibility(View.VISIBLE);

        cameraKitView.onResume();
    }

    private void decode() {
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                //cameraKitView.onStop();
                cameraKitView.onPause();
                Bitmap itmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = Bitmap.createScaledBitmap(itmap, cameraKitView.getWidth(), cameraKitView.getHeight(), false );
                image = InputImage.fromBitmap(bitmap, 0);

                progressBar.setVisibility(View.VISIBLE);
                pythonInitialize();
            }
        });
    }

    //python
    private void pythonInitialize() {

        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();

        Bitmap imp = image.getBitmapInternal();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        final PyObject pyObj = py.getModule("face");
        PyObject obj = pyObj.callAttr("main", encoded);
        PyObject obj2 = pyObj.callAttr("num");

        String stringToPython = obj.toString();
        byte[] data = android.util.Base64.decode(stringToPython, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        InputImage output = InputImage.fromBitmap(bmp, 0);

        photo.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        people.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        camera.setVisibility(View.INVISIBLE);

        photo.setImageBitmap(bmp);

        people.setText(obj2.toString());

        //Intent intent = new Intent(PhotoActivity.this, ResultActivity.class);
        //intent.putExtra("message", stringToPython);
        //startActivity(intent);
    }

    //camera kit functions
    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //back presssed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}