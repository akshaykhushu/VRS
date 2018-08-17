package com.example.aksha.newb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.example.aksha.newb.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<MarkerInfo> listItems;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    File outputFile;
    ImageView imageView;
    private StorageReference storageReference;
    Context currentContext;
    ProgressDialog progressDialog;
    StorageReference url;
    File outpurtDir;
    int selected;
    TextView textViewName;
    TextView textViewCost;
    View view;

    public void MapView(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.dynamicList);
        listItems = new ArrayList<>(MapsActivity.markerInfoMap.values());
        EditText et = findViewById(R.id.editTextSearchBar);
        et.setFocusableInTouchMode(true);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        currentContext = getApplicationContext();
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(getApplicationContext(), MakerClickedLayout.class);
                intent.putStringArrayListExtra("Bitmap", listItems.get(position).getBitmapUrl());
                intent.putExtra("Title",listItems.get(position).getTitle());
                intent.putExtra("Description", listItems.get(position).getDescription());
                intent.putExtra("Cost",listItems.get(position).getCost());
                intent.putExtra("Longitude", listItems.get(position).getLongitude());
                intent.putExtra("Latitude", listItems.get(position).getLatitude());
                intent.putExtra("Id", listItems.get(position).getId());
                startActivity(intent);
            }
        });

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MapsActivity.markerInfoMap.size();
        }

        @Override
        public Object getItem(int position) {
            return 0;

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.listitem, null);
            imageView = view.findViewById(R.id.imageViewListItem);
            textViewName = view.findViewById(R.id.textViewListItemName);
            textViewCost = view.findViewById(R.id.textViewListItemCost);
            Glide.with(ListActivity.this).load(Uri.parse(listItems.get(position).getBitmapUrl().get(0))).into(imageView);
//            Picasso.with(ListActivity.this).load(Uri.parse(listItems.get(position).getBitmapUrl().get(0))).into(imageView);
            textViewName.setText(listItems.get(position).getTitle());
            textViewCost.setText(listItems.get(position).getCost());
            return view;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
