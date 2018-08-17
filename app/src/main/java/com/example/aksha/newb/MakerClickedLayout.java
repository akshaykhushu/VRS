package com.example.aksha.newb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MakerClickedLayout extends AppCompatActivity {

    ImageView imageView;
    Bitmap image;
    String longitude;
    String latitude;
    String imageUrl;
    String id;
    ArrayList<String> bitmapUrl;
    int totalImages;
    int current = 0;
    HashMap<String, String> storedImagePath = new HashMap<>();
    ImageButton buttonNextMarkerClicked;
    ImageButton buttonPreviousMarkerClicked;
    StorageReference storageReference;
    File outputFile;
    int i;

    File myDir = null;

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
        buttonNextMarkerClicked = findViewById(R.id.buttonNextMarkerClicked);
        buttonPreviousMarkerClicked = findViewById(R.id.buttonPreviousMarkerClicked);

        eT.setText(getIntent().getStringExtra("Title").toString());
        eTdes.setText(getIntent().getStringExtra("Description").toString());
        eTcost.setText(getIntent().getStringExtra("Cost").toString());
        latitude = getIntent().getStringExtra("Latitude").toString();
        longitude = getIntent().getStringExtra("Longitude").toString();
        id = getIntent().getStringExtra("Id").toString();
        totalImages = getIntent().getIntExtra("TotalImages", 1);
        bitmapUrl = getIntent().getStringArrayListExtra("Bitmap");

        storageReference = FirebaseStorage.getInstance().getReference(id);
        Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);

        Glide.with(getApplicationContext()).load(bitmapUrl.get(0)).into(imageView);

//        Picasso.with(getApplicationContext()).load(bitmapUrl.get(0)).
//                fetch(new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Picasso.with(getApplicationContext()).load(bitmapUrl.get(0)).into(imageView);
//                    }
//
//                    @Override
//                    public void onError() {
//                        Toast.makeText(getApplicationContext(), "Could Not Load Image", Toast.LENGTH_SHORT).show();
//                    }
//                });

        ImageButton imageButton = findViewById(R.id.buttonDirections);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", Float.parseFloat(latitude), Float.parseFloat(longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });


        Bitmap bitmap = BitmapFactory.decodeFile(storedImagePath.get(bitmapUrl.get(0)));
        imageView.setImageBitmap(bitmap);

        buttonNextMarkerClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current >= bitmapUrl.size() - 1) {
                    Toast.makeText(getApplicationContext(), "No More Images", Toast.LENGTH_SHORT).show();
                    return;
                }
                current++;
                Glide.with(getApplicationContext()).load(bitmapUrl.get(current)).into(imageView);
//                Picasso.with(getApplicationContext()).load(bitmapUrl.get(current)).
//                        fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Picasso.with(getApplicationContext()).load(bitmapUrl.get(current)).into(imageView);
//                            }
//
//                            @Override
//                            public void onError() {
//                                Toast.makeText(getApplicationContext(), "Could Not Load Image", Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });

        buttonPreviousMarkerClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current <= 0) {
                    Toast.makeText(getApplicationContext(), "No More Images", Toast.LENGTH_SHORT).show();
                    return;
                }
                current--;
                Glide.with(getApplicationContext()).load(bitmapUrl.get(current)).into(imageView);
//                Picasso.with(getApplicationContext()).load(bitmapUrl.get(current)).
//                        fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Picasso.with(getApplicationContext()).load(bitmapUrl.get(current)).into(imageView);
//                            }
//
//                            @Override
//                            public void onError() {
//                                Toast.makeText(getApplicationContext(), "Could Not Load Image", Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });

    }

    public void FullScreen(View view) {
        Intent intent = new Intent(this, FullImageView.class);
        intent.putExtra("image", bitmapUrl.get(current));
        startActivity(intent);
    }

}
