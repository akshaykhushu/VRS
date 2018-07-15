package com.example.aksha.newb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    public static int upload=0;
    protected Bitmap bitmap;
    protected  String title;
    protected  String description;
    protected String cost;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    LocationManager locationManager;
    LocationListener locationListener;

    public void setCurrentLocation(Location current) {
        this.currentLocation = current;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton b = findViewById(R.id.CameraActionButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 123);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == 123) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");

                Intent intent = new Intent(this, Info.class);
                intent.putExtra("Image", bitmap);
                startActivity(intent);
                finish();
            }
        }catch(Exception e){

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET, Manifest.permission.CAMERA
            }, 200);
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("onResume : ", String.valueOf(upload) );
            reference = firebaseDatabase.getReference();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> map = (HashMap) snapshot.getValue();
                        Log.w("Title", snapshot.child("Title").getValue().toString());
                        //Log.w("Bitmap", snapshot.child("Bitmap").getValue().toString());
                        //Log.w("Location", snapshot.child("Location").getValue().toString());

                        String longitude = snapshot.child("LocationLong").getValue().toString();
                        String latitude = snapshot.child("LocationLati").getValue().toString();
                        //HashMap<String, Object> map = new HashMap<>();
                        byte[] encodeByte = Base64.decode(snapshot.child("Bitmap").getValue().toString(), Base64.DEFAULT);
                        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        Log.w("Bitmap is : ", image.toString());
                        bitmap = image;

                        //bitmap = Bitmap.createBitmap((Bitmap)snapshot.child("Bitmap").getValue());
                        title = snapshot.child("Title").getValue().toString();
                        description = snapshot.child("Description").getValue().toString();
                        cost = snapshot.child("Cost").getValue().toString();

                        setMarker(longitude, latitude, bitmap, title, description, cost);
                        //Log.w("Value from DB", image.toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Value from DB", "OnCancelledCalled");
                }
            });
        }


    public void setMarker(String longitude, String latitude, final Bitmap bitmap, String Title, final String description, String costo) {
        Log.w("Value from DB", "SetMarker chala");
        Log.w("Image is : ", bitmap.toString());

        Log.e("Longitude", longitude);
        Log.e("Latitude", latitude);

        LatLng current = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMap.addMarker(new MarkerOptions().position(current).icon(bitmapDescriptor).title(Title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), MakerClickedLayout.class);
                ByteArrayOutputStream ByteStream = new  ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
                byte [] b=ByteStream.toByteArray();
                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                intent.putExtra("Bitmap",temp);
                Log.e("Title marker clicked:  ", title);
                Log.e("desc marker clicked:  ", description);
                Log.e("cost marker clicked:  ", cost);
                intent.putExtra("Title", title);
                intent.putExtra("Description", description);
                intent.putExtra("Cost", cost);
                startActivity(intent);
                return true;
            }
        });

    }

}


