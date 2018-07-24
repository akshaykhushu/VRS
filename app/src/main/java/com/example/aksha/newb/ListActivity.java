package com.example.aksha.newb;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<MarkerInfo> listItems;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.dynamicList);
        listItems = new ArrayList<>(MapsActivity.markerInfoMap.values());
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MapsActivity.markerInfoMap.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
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
