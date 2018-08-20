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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private Location currentLocation;
    public static int upload = 0;
    protected Bitmap bitmap, circularBitmap;
    protected String title;
    protected String description;
    protected String cost;
    public static Map<String, MarkerInfo> markerInfoMap;
    int count=0;

    public static String UserId;


    private ClusterManager<com.example.aksha.newb.Marker> mClusterManager;


    Double myLatitude;
    Double myLongitude;
    Uri imageUri;
    private FirebaseAuth firebaseAuth;
    ImageButton imgNavButton;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    public void setCurrentLocation(Location current) {
        this.currentLocation = current;
    }
    private static final int CONTENT_REQUEST=1337;
    private File output=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerInfoMap = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setNavigationViewListner();
        UserId = getIntent().getStringExtra("UserId");
        FloatingActionButton b = findViewById(R.id.CameraActionButton);
        final NavigationView navigationView =  findViewById(R.id.navigation_view);
        View hView =  navigationView.getHeaderView(0);
//        ImageView user_nav_image = hView.findViewById(R.id.imageViewProfilePicture);
//        try{
//            user_nav_image.setImageURI(firebaseAuth.getCurrentUser().getPhotoUrl());
//        }
//        catch(NullPointerException e){
//            Log.e("User Has no image", e.getLocalizedMessage());
//            user_nav_image.setImageResource(R.drawable.ic_launcher_foreground);
//        }
        TextView nav_user = hView.findViewById(R.id.textViewUserId);
        nav_user.setText(firebaseAuth.getCurrentUser().getEmail());
        EditText et = findViewById(R.id.editTextSearchBar);

        imgNavButton = findViewById(R.id.imageButton);
        imgNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drwaerLayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET, Manifest.permission.CAMERA
            }, 200);
            return;
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
                imageUri = Uri.fromFile(new File(filename));
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 123);
            }
        });


        reference = firebaseDatabase.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                markerInfoMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MarkerInfo markerInfo = new MarkerInfo();
                    ArrayList<String> bitmapUrl = new ArrayList<>();
                    String userId = new String();
                    try {
                        userId = snapshot.child("Id").getValue().toString();
                        markerInfo.setTotalImages(Integer.parseInt(snapshot.child("TotalImages").getValue().toString()));
                        for (int i=0; i < markerInfo.getTotalImages(); i++){
                            bitmapUrl.add(snapshot.child("Bitmap"+i).getValue().toString());
                        }
                        markerInfo.setBitmapUrl(bitmapUrl);
                        markerInfo.setDescription(snapshot.child("Description").getValue().toString());
                        markerInfo.setCost(snapshot.child("Cost").getValue().toString());
                        cost = snapshot.child("Cost").getValue().toString();
                        markerInfo.setLongitude(snapshot.child("LocationLong").getValue().toString());
                        markerInfo.setLatitude(snapshot.child("LocationLati").getValue().toString());
                        markerInfo.setId(snapshot.child("Id").getValue().toString());
                        markerInfo.setTitle(snapshot.child("Title").getValue().toString());
                        title = snapshot.child("Title").getValue().toString();
                        if (!markerInfoMap.containsKey(userId)){
                            MapsActivity.markerInfoMap.put(userId, markerInfo);
                        }
                        setMarker(markerInfo);
                    }catch(Exception e){
                        Log.e("Exception Caught","UserId Not found");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Value from DB", "OnCancelledCalled");
            }
        });

    }

    private void setUpCluster(Double longitude, Double latitude) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(longitude, latitude), 2));
        mClusterManager = new ClusterManager<com.example.aksha.newb.Marker>(this, mMap);
        
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        addItems(longitude, latitude);
    }

    private void addItems(Double longitude, Double latitude) {
//        for (int i = 0; i < 10; i++) {
//            double offset = i / 60d;
//            latitude = latitude + offset;
//            longitude = longitude + offset;
            com.example.aksha.newb.Marker offsetItem = new com.example.aksha.newb.Marker(latitude, longitude);
            mClusterManager.addItem(offsetItem);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, Info.class);
                intent.putExtra("Image", imageUri.toString());
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "There was some problem while Taking the Picture. Try Again", Toast.LENGTH_SHORT).show();
        }

    }



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
        rlp.setMargins(100, 0, 0, 200);


        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert();
        } else {
            myLatitude = tracker.getLatitude();
            myLongitude = tracker.getLongitude();
        }


        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
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

        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        IconGenerator iconGenerator = new IconGenerator(this);
//        iconGenerator.setColor(R.color.Green);
        iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap iconBitmap = iconGenerator.makeIcon(title + " | " + cost);
        MarkerOptions mo = new MarkerOptions().position(current).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).title(markerInfo.getId());
        Marker marker = mMap.addMarker(mo);
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String idMarker = marker.getTitle();
                for (String id : markerInfoMap.keySet()) {
                    if (id.equals(idMarker)){
                        Intent intent = new Intent(getApplicationContext(), MakerClickedLayout.class);
                        intent.putExtra("TotalImages", markerInfoMap.get(id).getTotalImages());
                        intent.putStringArrayListExtra("Bitmap", markerInfoMap.get(id).getBitmapUrl());
                        intent.putExtra("Title", markerInfoMap.get(id).getTitle());
                        intent.putExtra("Description", markerInfoMap.get(id).getDescription());
                        intent.putExtra("Cost", markerInfoMap.get(id).getCost());
                        intent.putExtra("Id", markerInfoMap.get(id).getId());
                        intent.putExtra("Longitude", markerInfoMap.get(id).getLongitude());
                        intent.putExtra("Latitude", markerInfoMap.get(id).getLatitude());
                        startActivity(intent);
                    }
                }
                return true;
            }
        });


        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

    }

    private void setNavigationViewListner() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_Help: {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.nav_Images: {
                String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
                imageUri = Uri.fromFile(new File(filename));
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 123);
                break;
            }

            case R.id.nav_ListView :{
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.nav_LogOut  :{
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawerLayout = findViewById(R.id.drwaerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}






