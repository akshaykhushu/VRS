package com.example.aksha.newb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.aksha.newb.*;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<MarkerInfo> listItems;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;

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

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(getApplicationContext(), MakerClickedLayout.class);
                ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
                listItems.get(position).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
                byte[] b = ByteStream.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                intent.putExtra("Bitmap", temp);
                intent.putExtra("Title",listItems.get(position).getTitle());
                intent.putExtra("Description", listItems.get(position).getDescription());
                intent.putExtra("Cost",listItems.get(position).getCost());
                intent.putExtra("Longitude", listItems.get(position).getLongitude());
                intent.putExtra("Latitude", listItems.get(position).getLatitude());
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

            View view = getLayoutInflater().inflate(R.layout.listitem, null);

            ImageView imageView = view.findViewById(R.id.imageViewListItem);
            TextView textViewName = view.findViewById(R.id.textViewListItemName);
            TextView textViewCost = view.findViewById(R.id.textViewListItemCost);

            imageView.setImageBitmap(listItems.get(position).getBitmap());
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
