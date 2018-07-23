package com.example.aksha.newb;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    public static int upload = 0;
    protected Bitmap bitmap, circularBitmap;
    protected String title;
    protected String description;
    protected String cost;
    public static Map<String, MarkerInfo> markerInfoMap;

    // Construct a FusedLocationProviderClient.
    // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
        markerInfoMap = new HashMap<>();
        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

        try {
            if (requestCode == 123) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");

                Intent intent = new Intent(this, Info.class);
                intent.putExtra("Image", bitmap);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {

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

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.setMargins(30, 0, 0, 40);
    }

    @Override
    protected void onResume() {
        //String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onResume();
        Log.w("onResume : ", String.valueOf(upload));
        reference = firebaseDatabase.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String longitude = snapshot.child("LocationLong").getValue().toString();
                    String latitude = snapshot.child("LocationLati").getValue().toString();
                    byte[] encodeByte = Base64.decode(snapshot.child("Bitmap").getValue().toString(), Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    String android_id = snapshot.child("Id").getValue().toString();
                    Log.w("Bitmap is : ", image.toString());
                    bitmap = image;
                    MarkerInfo markerInfo = new MarkerInfo();
                    markerInfo.setDescription(snapshot.child("Description").getValue().toString());
                    markerInfo.setCost(snapshot.child("Cost").getValue().toString());
                    markerInfo.setBitmap(bitmap);
                    markerInfo.setLongitude(snapshot.child("LocationLong").getValue().toString());
                    markerInfo.setLatitude(snapshot.child("LocationLati").getValue().toString());
                    markerInfo.setId(snapshot.child("Id").getValue().toString());
                    markerInfo.setTitle(snapshot.child("Title").getValue().toString());
                    MapsActivity.markerInfoMap.put(android_id, markerInfo);
                    setMarker(markerInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Value from DB", "OnCancelledCalled");
            }
        });
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void ListView(View view){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    public void setMarker(MarkerInfo markerInfo) {

        LatLng current = new LatLng(Double.parseDouble(markerInfo.getLatitude()), Double.parseDouble(markerInfo.getLongitude()));
        circularBitmap = getRoundedCornerBitmap(markerInfo.getBitmap());
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory.fromBitmap(circularBitmap);
        MarkerOptions mo = new MarkerOptions().position(current).title(markerInfo.getTitle());
        Marker marker = mMap.addMarker(mo);
        marker.showInfoWindow();
        markerInfoMap.put(marker.getId(), markerInfo);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String idMarker = marker.getId();
                for (String id : markerInfoMap.keySet()) {
                    if (id.equals(idMarker)){
                        Intent intent = new Intent(getApplicationContext(), MakerClickedLayout.class);
                        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
                        markerInfoMap.get(id).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
                        byte[] b = ByteStream.toByteArray();
                        String temp = Base64.encodeToString(b, Base64.DEFAULT);
                        intent.putExtra("Bitmap", temp);
                        intent.putExtra("Title", markerInfoMap.get(id).getTitle());
                        intent.putExtra("Description", markerInfoMap.get(id).getDescription());
                        intent.putExtra("Cost", markerInfoMap.get(id).getCost());
                        startActivity(intent);
                    }
                }
                return true;
            }
        });

    }
}






