package com.example.aksha.newb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Info extends AppCompatActivity {

    protected Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bitmap = (Bitmap) getIntent().getParcelableExtra("Image");
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

    public void Upload(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Image", bitmap);
        startActivity(intent);
    }
}
