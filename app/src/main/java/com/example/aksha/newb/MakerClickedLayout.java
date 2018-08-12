package com.example.aksha.newb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MakerClickedLayout extends AppCompatActivity {

    ImageView imageView;
    Bitmap image;
    String longitude;
    String latitude;
    String imageUrl;
    String id;
    StorageReference storageReference;
    File outputFile;
    //    Matrix matrix = new Matrix();
//    Float scale = 10f;
//    ScaleGestureDetector SGD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maker_clicked_layout);
//        SGD = new ScaleGestureDetector(this, new ScaleListener());
        EditText eT = findViewById(R.id.editTextNameMarkerClicked);
        EditText eTdes = findViewById(R.id.editTextDescriptionMarkerClicked);
        EditText eTcost = findViewById(R.id.editTextCostMarkerClicked);
        imageView = findViewById(R.id.imageViewMarkerClicked);

        eT.setText(getIntent().getStringExtra("Title").toString());
        eTdes.setText(getIntent().getStringExtra("Description").toString());
        eTcost.setText(getIntent().getStringExtra("Cost").toString());
        latitude = getIntent().getStringExtra("Latitude").toString();
        longitude = getIntent().getStringExtra("Longitude").toString();
        imageUrl = getIntent().getStringExtra("Bitmap").toString();
        id = getIntent().getStringExtra("Id").toString();

        storageReference = FirebaseStorage.getInstance().getReference(id);
        StorageReference url = storageReference.child("Image");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Picasso.with(MakerClickedLayout.this).load(Uri.parse(imageUrl)).into(imageView);
        progressDialog.dismiss();

        ImageButton imageButton = findViewById(R.id.buttonDirections);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
//                startActivity(intent);
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", Float.parseFloat(latitude), Float.parseFloat(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    public void FullScreen(View view){
        Intent intent = new Intent(this, FullImageView.class);
        intent.putExtra("image", imageUrl);
        startActivity(intent);
    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scale = scale * detector.getScaleFactor();
//            scale = Math.max(0.1f, Math.min(scale, 5f));
//            matrix.setScale(scale, scale);
//            imageView.setImageMatrix(matrix);
//            return true;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        SGD.onTouchEvent(event);
//        return true;
//    }
}
