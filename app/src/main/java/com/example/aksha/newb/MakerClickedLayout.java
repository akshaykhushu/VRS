package com.example.aksha.newb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MakerClickedLayout extends AppCompatActivity {

    ImageView imageView;
    Bitmap image;
//    Matrix matrix = new Matrix();
//    Float scale = 10f;
//    ScaleGestureDetector SGD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maker_clicked_layout);
//        SGD = new ScaleGestureDetector(this, new ScaleListener());

        EditText eT = findViewById(R.id.editTextNameMarkerClicked);
        EditText eTdes = findViewById(R.id.editTextDescriptionMarkerClicked);
        EditText eTcost = findViewById(R.id.editTextCostMarkerClicked);
        imageView = findViewById(R.id.imageViewMarkerClicked);
        eT.setText(getIntent().getStringExtra("Title").toString());
        eTdes.setText(getIntent().getStringExtra("Description").toString());
        eTcost.setText(getIntent().getStringExtra("Cost").toString());
        byte[] encodeByte = Base64.decode(getIntent().getStringExtra("Bitmap"), Base64.DEFAULT);
        image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        imageView.setImageBitmap(image);
    }

    public void FullScreen(View view){
        Intent intent = new Intent(this, FullImageView.class);
        ByteArrayOutputStream ByteStream = new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        intent.putExtra("image", temp);
        startActivity(intent);
    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scale = scale * detector.getScaleFactor();
//            scale = Math.max(0.1f, Math.min(scale, 5f));
//            matrix.setScale(scale, scale);
//            imageView.setImageMatrix(matrix);
//            return true;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        SGD.onTouchEvent(event);
//        return true;
//    }
}
