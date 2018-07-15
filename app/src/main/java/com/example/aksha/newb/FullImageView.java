package com.example.aksha.newb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class FullImageView extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);
        imageView = findViewById(R.id.imageViewFullScreen);
        byte[] encodeByte = Base64.decode(getIntent().getStringExtra("image"), Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        imageView.setImageBitmap(image);
    }
}
