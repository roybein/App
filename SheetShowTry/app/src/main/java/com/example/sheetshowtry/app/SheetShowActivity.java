package com.example.sheetshowtry.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

public class SheetShowActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_show);

        ImageView imageView = (ImageView) findViewById(R.id.image_sheet_show);
        Bitmap frag = SheetManualFormatActivity.frags.get(0);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        frag = Bitmap.createBitmap(frag, 0, 0, frag.getWidth(), frag.getHeight(), matrix, true);
        imageView.setImageBitmap(frag);

    }
}