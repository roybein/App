package com.example.sheetshowtry.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class SheetShowActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_show);

        ImageView imageView = (ImageView) findViewById(R.id.image_sheet_show);
        imageView.setImageBitmap(SheetManualFormatActivity.frags.get(0));
    }
}