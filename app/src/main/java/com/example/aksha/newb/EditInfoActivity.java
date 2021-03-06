package com.example.aksha.newb;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditInfoActivity extends AppCompatActivity {

    EditText nameEdit;
    EditText costEdit;
    EditText descEdit;
    ArrayList<String> bitmapUrl;
    ArrayList<Uri> downloadUrl;
    ImageView imageView;
    Button updateButton;
    Button cancelButton;
    ImageButton imageButtonMoreImages;
    ImageButton nextImageButton;
    ImageButton prevImageButton;
    int current = 0;
    Uri imageUri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String imageStr;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    double latitude;
    double longitude;
    String android_id;
    Spinner currencySelectEdit;
    FloatingActionButton deleteImageButton;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        nameEdit = findViewById(R.id.nameEditTextEdit);
        costEdit = findViewById(R.id.editTextCostEdit);
        descEdit = findViewById(R.id.editTextDescriptionEdit);
        current=0;
        bitmapUrl = new ArrayList<>();
        imageView = findViewById(R.id.imageViewEdit);
        updateButton = findViewById(R.id.buttonUpdateEdit);
        cancelButton = findViewById(R.id.buttonCancelEdit);
        imageButtonMoreImages = findViewById(R.id.buttonMoreImagesEdit);
        nextImageButton = findViewById(R.id.buttonNextEdit);
        prevImageButton = findViewById(R.id.buttonPreviousEdit);
        currencySelectEdit = findViewById(R.id.spinnerCurrencyEdit);
        deleteImageButton = findViewById(R.id.floatingActionButtonDeleteImage);

        firebaseDatabase = FirebaseDatabase.getInstance();

        nameEdit.setText(getIntent().getStringExtra("Name"));
        String cost = getIntent().getStringExtra("Cost");
        cost = cost.substring(1);
        costEdit.setText(cost);
        descEdit.setText(getIntent().getStringExtra("Desc"));
        bitmapUrl = getIntent().getStringArrayListExtra("BitmapURL");
        id = getIntent().getStringExtra("Id");


        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(id);

        downloadUrl = new ArrayList<>();
        for (String bitmapStr: bitmapUrl){
            downloadUrl.add(Uri.parse(bitmapStr));
        }

        disableDelete();


        Glide.with(getApplicationContext()).load(bitmapUrl.get(0)).into(imageView);

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadUrl.remove(current);
                bitmapUrl.remove(current);
                try{
                    nextImage();
                }
                catch(IndexOutOfBoundsException e){
                    Log.e("No More Images", "asdfdsf");
                    Drawable drawable = getResources().getDrawable(R.drawable.search_layout);
                    imageView.setImageDrawable(drawable);
                }
                disableDelete();
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
            }
        });

        prevImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current <= 0) {
                    current = downloadUrl.size()-1;
                    Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
                    return;
                }
                current--;
                Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageButtonMoreImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
                imageUri = Uri.fromFile(new File(filename));
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 123);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.INTERNET, Manifest.permission.CAMERA
                    }, 200);
                    return;
                }
                String text = currencySelectEdit.getSelectedItem().toString();

                GPSTracker tracker = new GPSTracker(getApplicationContext());
                if (!tracker.canGetLocation()) {
                    tracker.showSettingsAlert();
                } else {
                    latitude = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }


                android_id = MapsActivity.UserId;

                databaseReference = firebaseDatabase.getReference(id);


                Log.e("Longitude", String.valueOf(longitude));
                Log.e("Latitude", String.valueOf(latitude));


                if ((downloadUrl.size() == 0) && (bitmapUrl.size() == 0) ){
                    Toast.makeText(getApplicationContext(), "Please add an Image to continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nameEdit.getText().toString()) || TextUtils.isEmpty((nameEdit.getText().toString().trim()))){
                    Toast.makeText(getApplicationContext(), "Name is a required Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(descEdit.getText().toString()) || TextUtils.isEmpty((descEdit.getText().toString().trim()))){
                    Toast.makeText(getApplicationContext(), "Description is a required Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(costEdit.getText().toString()) || TextUtils.isEmpty((costEdit.getText().toString().trim()))){
                    Toast.makeText(getApplicationContext(), "Cost is a required Field", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.child("TotalImages").setValue(String.valueOf(downloadUrl.size()));
                for (int i=0;i<downloadUrl.size();i++){
                    databaseReference.child("Bitmap"+i).setValue(downloadUrl.get(i).toString());
                }
                databaseReference.child("LocationLong").setValue(String.valueOf(longitude));
                databaseReference.child("LocationLati").setValue(String.valueOf(latitude));
                databaseReference.child("Title").setValue(nameEdit.getText().toString());
                databaseReference.child("Description").setValue(descEdit.getText().toString());
                databaseReference.child("Cost").setValue(text + costEdit.getText().toString());
                databaseReference.child("Id").setValue(id);
                MapsActivity.upload = 1;
//                MapsActivity.mClusterManager.removeItem(MapsActivity.markerInfoMap.get(id));
//                MapsActivity.mClusterManager.cluster();
//                MapsActivity.markerInfoMap.clear();

//                MapsActivity.mMap.clear();
//                MapsActivity.mClusterManager.clearItems();
//                MapsActivity.mClusterManager.cluster();
//                MapsActivity.markerInfoMap.clear();
//                MapsActivity.mClusterManager.clearItems();
//                MapsActivity.mClusterManager.cluster();
//                MapsActivity.mClusterManager.addItems(MapsActivity.markerInfoMap.values());
//                MapsActivity.mClusterManager.cluster();

                finish();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this will always start your activity as a new task
                startActivity(intent);
            }
        });



    }

    public void disableDelete(){
        if (downloadUrl.size() < 1 ||  bitmapUrl.size() < 1){
            deleteImageButton.setVisibility(View.INVISIBLE);
        }
        else {
            deleteImageButton.setVisibility(View.VISIBLE);
        }
    }

    public void nextImage(){
        if (current >= downloadUrl.size() - 1) {
//                    Toast.makeText(getApplicationContext(), "No More Images", Toast.LENGTH_SHORT).show();
            current = 0;
            Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
            return;
        }
        current++;
        Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {

                disableDelete();

                imageStr = Environment.getExternalStorageDirectory() + "/test/testfile.jpg";
                File actualImage = new File(imageStr);
                File compressedImageFile = null;
                try {
                    compressedImageFile = new Compressor(this).compressToFile(actualImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference fileRef = storageReference.child("Image"+downloadUrl.size());
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Uri file = Uri.fromFile(compressedImageFile);
                fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl.add(taskSnapshot.getDownloadUrl());
                        Glide.with(getApplicationContext()).load(taskSnapshot.getDownloadUrl()).into(imageView);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Could Not Upload Data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {

        }

    }
}
