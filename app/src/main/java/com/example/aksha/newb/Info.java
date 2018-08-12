package com.example.aksha.newb;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.provider.Settings.Secure;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Info extends AppCompatActivity {
    protected String android_id;

    LocationManager locationManager;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    protected Bitmap bitmap;
    Uri imageUri;
    String imageStr;
    Map<String, Object> map;
    private StorageReference storageReference;
    Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
//        bitmap = getIntent().getParcelableExtra("Image");
        imageStr = Environment.getExternalStorageDirectory() + "/test/testfile.jpg";
        imageUri = Uri.parse(imageStr);
        ImageView imageView = findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeFile(imageStr);
        imageView.setImageBitmap(bitmap);
//        int nh = (int) ( bitmap.getHeight() * (1080.0 / bitmap.getWidth()) );
//        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1080, nh, true);
//        imageView.setImageBitmap(scaled);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        map = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());

        Uri file = Uri.fromFile(new File(imageStr));
        StorageReference fileRef = storageReference.child("Image");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getDownloadUrl() != null){
                    downloadUrl = taskSnapshot.getDownloadUrl();

                } else if(taskSnapshot.getMetadata().getDownloadUrl() != null) {
                    downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                }
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could Not Upload Data", Toast.LENGTH_SHORT).show();
            }
        });

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
        text = text + " ";

        Location location = getLastKnownLocation();
        MapsActivity.myLocation = location;
        android_id = MapsActivity.UserId;
        DatabaseReference databaseReference = firebaseDatabase.getReference(android_id);
//        ByteArrayOutputStream ByteStream = new  ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
//        byte [] b=ByteStream.toByteArray();
//        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        Log.e("Longitude", String.valueOf(location.getLongitude()));
        Log.e("Latitude", String.valueOf(location.getLatitude()));



        databaseReference.child("Bitmap").setValue(downloadUrl.toString());
        databaseReference.child("LocationLong").setValue(String.valueOf(location.getLongitude()));
        databaseReference.child("LocationLati").setValue(String.valueOf(location.getLatitude()));
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
