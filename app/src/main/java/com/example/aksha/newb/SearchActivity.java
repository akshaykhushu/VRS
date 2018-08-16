package com.example.aksha.newb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    EditText searchField;
    ImageButton searchButton;
    RecyclerView resultList;
    DatabaseReference databaseReference;
    ArrayList<String> titleList;
    ArrayList<String> costList;
    ArrayList<ArrayList<String>> bitmapList2D;
    ArrayList<String> bitmapList;
    ArrayList<String> descriptionList;
    ArrayList<String> uidList;
    ArrayList<String> latiList;
    ArrayList<String> longList;
    SearchAdapter searchAdapter;
    public static HashMap<String, MarkerInfoSearch> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchField = findViewById(R.id.editTextSearchBar);
        resultList = findViewById(R.id.dynamicList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        databaseReference = FirebaseDatabase.getInstance().getReference();

        titleList = new ArrayList<>();
        costList = new ArrayList<>();
        bitmapList2D = new ArrayList<ArrayList<String>>();
        descriptionList = new ArrayList<>();
        hashMap = new HashMap<>();
        bitmapList = new ArrayList<>();
        latiList = new ArrayList<>();
        longList = new ArrayList<>();
        uidList = new ArrayList<>();


        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }
                else {

                    titleList.clear();
                    costList.clear();
                    bitmapList.clear();
                    descriptionList.clear();
                    latiList.clear();
                    longList.clear();
                    resultList.removeAllViews();
                    hashMap.clear();
                }

            }
        });
    }

    public void MapView(View view){
        Intent intent = new Intent(this, MapsActivitySearch.class);
        intent.putExtra("Search", searchField.getText().toString());
        startActivity(intent);
    }

    private void setAdapter(final String searchedString) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int counter = 0;


                titleList.clear();
                costList.clear();
                bitmapList.clear();
                descriptionList.clear();
                latiList.clear();
                longList.clear();
                hashMap.clear();
                resultList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String title = snapshot.child("Title").getValue(String.class);
                    String cost = snapshot.child("Cost").getValue(String.class);
                    //String bitmap = snapshot.child("Bitmap").getValue(String.class);
                    String description = snapshot.child("Description").getValue(String.class);
                    String longitude = snapshot.child("LocationLong").getValue(String.class);
                    String latitude = snapshot.child("LocationLati").getValue(String.class);
                    String id = snapshot.child("Id").getValue(String.class);
                    Integer totalImages = Integer.parseInt(snapshot.child("TotalImages").getValue().toString());
                    for (int i=0; i < totalImages; i++){
                        bitmapList.add(snapshot.child("Bitmap"+i).getValue().toString());
                    }
                    bitmapList2D.add(bitmapList);
                    
                    
                    MarkerInfoSearch markerInfoSearch = new MarkerInfoSearch();
                    markerInfoSearch.setBitmapUrl(bitmapList2D);
                    markerInfoSearch.setTitle(title);
                    markerInfoSearch.setDescription(description);
                    markerInfoSearch.setLatitude(latitude);
                    markerInfoSearch.setLongitude(longitude);
                    markerInfoSearch.setCost(cost);
                    markerInfoSearch.setId(id);
                    markerInfoSearch.setTotalImages(totalImages);

                    if(title.toLowerCase().contains(searchedString.toLowerCase())){
                        titleList.add(title);
                        costList.add(cost);
                        bitmapList.add(bitmapList2D);
                        descriptionList.add(description);
                        latiList.add(latitude);
                        longList.add(longitude);
                        uidList.add(id);
                        hashMap.put(id, markerInfoSearch);
                        counter++;
                    }else if (description.toLowerCase().contains(searchedString.toLowerCase())){
                        titleList.add(title);
                        costList.add(cost);
                        bitmapList.add(bitmapList2D);
                        descriptionList.add(description);
                        latiList.add(latitude);
                        longList.add(longitude);
                        uidList.add(id);
                        hashMap.put(id, markerInfoSearch);
                        counter++;
                    }

                    if(counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, titleList, costList, bitmapList2D, descriptionList, uidList, latiList, longList);
                resultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
