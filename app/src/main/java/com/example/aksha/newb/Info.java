package com.example.aksha.newb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Info extends AppCompatActivity {
    protected String android_id;

    LocationManager locationManager;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    protected Bitmap bitmap;
    Map<String, Object> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bitmap = (Bitmap) getIntent().getParcelableExtra("Image");
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        map = new HashMap<>();

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

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        DatabaseReference databaseReference = firebaseDatabase.getReference(android_id);
        ByteArrayOutputStream ByteStream = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
//        MarkerInfoClass markerInfoClass = new MarkerInfoClass();
//        markerInfoClass.bitmap =temp;
//        markerInfoClass.Title = eT.getText().toString();
//        markerInfoClass.location = location;
//        databaseReference.setValue(markerInfoClass);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] bytes = bos.toByteArray();
        Log.e("Longitude", String.valueOf(location.getLongitude()));
        Log.e("Latitude", String.valueOf(location.getLatitude()));

        databaseReference.child("Bitmap").setValue(temp);
        databaseReference.child("LocationLong").setValue(String.valueOf(location.getLongitude()));
        databaseReference.child("LocationLati").setValue(String.valueOf(location.getLatitude()));
        databaseReference.child("Title").setValue(eT.getText().toString());
        MapsActivity.upload = 1;
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

}
