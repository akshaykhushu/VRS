package com.example.aksha.newb;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.provider.Settings.Secure;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Info extends AppCompatActivity {
    protected String android_id;

    LocationManager locationManager;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    protected Bitmap bitmap;
    ImageButton addImages;
    Uri imageUri;
    String imageStr;
    ImageButton buttonNext;
    Location myLocation;
    ImageButton buttonPrevious;
    Map<String, Object> map;
    int current = 0;
    private StorageReference storageReference;
    ArrayList<Uri> downloadUrl = new ArrayList<>();
    ImageView imageView;
    Double longitude;
    Double latitude;


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageStr = Environment.getExternalStorageDirectory() + "/test/testfile.jpg";
        imageUri = Uri.parse(imageStr);
        bitmap = BitmapFactory.decodeFile(imageStr);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 0, locationListener);

        map = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        addImages = findViewById(R.id.buttonMoreImages);
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
                imageUri = Uri.fromFile(new File(filename));
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 123);
            }
        });

        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current >= downloadUrl.size()-1)   {
                    return;
                }
                current++;
                //Picasso.with(Info.this).load(downloadUrl.get(current)).into(imageView);

                Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
//                Picasso.with(getApplicationContext()).load(downloadUrl.get(current)).
//                        fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Picasso.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
//                            }
//
//                            @Override
//                            public void onError() {
//                                Toast.makeText(getApplicationContext(), "Could Not Load Image", Toast.LENGTH_SHORT).show();
//                            }
//                        });

            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current <= 0){
                    return;
                }
                current--;
                Glide.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
//                Picasso.with(getApplicationContext()).load(downloadUrl.get(current)).
//                        fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Picasso.with(getApplicationContext()).load(downloadUrl.get(current)).into(imageView);
//                            }
//
//                            @Override
//                            public void onError() {
//                                Toast.makeText(getApplicationContext(), "Could Not Load Image", Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });


        Uri file = Uri.fromFile(new File(imageStr));
        StorageReference fileRef = storageReference.child("Image" + downloadUrl.size());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();


        fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl.add(taskSnapshot.getDownloadUrl());
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could Not Upload Data", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {


                imageStr = Environment.getExternalStorageDirectory() + "/test/testfile.jpg";

                StorageReference fileRef = storageReference.child("Image"+downloadUrl.size());
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Uri file = Uri.fromFile(new File(imageStr));
                fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl.add(taskSnapshot.getDownloadUrl());
//                        Picasso.with(getApplicationContext()).load(taskSnapshot.getDownloadUrl()).into(imageView);
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

    public void Upload(View view){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET, Manifest.permission.CAMERA
            }, 200);
            return;
        }
        EditText eT = findViewById(R.id.nameEditText);
        EditText eTdes = findViewById(R.id.editTextDescription);
        EditText eTcost = findViewById(R.id.editTextCost);
        Spinner spinner = findViewById(R.id.spinnerCurrency);
        String text = spinner.getSelectedItem().toString();
//        text = text + " ";

        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert();
        } else {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
        }

        //Location location = getLastKnownLocation();
        //MapsActivity.myLocation = myLocation;
        android_id = MapsActivity.UserId;
        DatabaseReference databaseReference = firebaseDatabase.getReference(android_id);
//        Log.e("Longitude", String.valueOf(myLocation.getLongitude()));
        Log.e("Longitude", String.valueOf(longitude));
//        Log.e("Latitude", String.valueOf(myLocation.getLatitude()));
        Log.e("Latitude", String.valueOf(latitude));

        if (TextUtils.isEmpty(eT.getText().toString())){
            Toast.makeText(getApplicationContext(), "Name is a required Field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(eTdes.getText().toString())){
            Toast.makeText(getApplicationContext(), "Description is a required Field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(eTcost.getText().toString())){
            Toast.makeText(getApplicationContext(), "Cost is a required Field", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("TotalImages").setValue(String.valueOf(downloadUrl.size()));
        for (int i=0;i<downloadUrl.size();i++){
            databaseReference.child("Bitmap"+i).setValue(downloadUrl.get(i).toString());
        }
        databaseReference.child("LocationLong").setValue(String.valueOf(longitude));
        databaseReference.child("LocationLati").setValue(String.valueOf(latitude));
        databaseReference.child("Title").setValue(eT.getText().toString());
        databaseReference.child("Description").setValue(eTdes.getText().toString());
        databaseReference.child("Cost").setValue(text + eTcost.getText().toString());
        databaseReference.child("Id").setValue(android_id);
        MapsActivity.upload = 1;
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
