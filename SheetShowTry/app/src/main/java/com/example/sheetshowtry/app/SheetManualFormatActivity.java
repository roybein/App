package com.example.sheetshowtry.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class SheetManualFormatActivity extends Activity {

    public static String LOG_TAG = "SheetManualFormat";
    public Rect cropRect = null;
    private Bitmap bitmap = null;
    private SheetFormatView sheetFormatView = null;
    public static ArrayList<Bitmap> frags;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_manual_format);

        bitmap = MainActivity.bitmapList.get(0);
        sheetFormatView = (SheetFormatView) findViewById(R.id.image_sheet);
        sheetFormatView.setImageBitmap(bitmap);
        frags = new ArrayList<Bitmap>();

        Button buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropRect = getCropRect();
                if (cropRect != null) {
                    Log.d(LOG_TAG, "bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());
                    frags.add(0, Bitmap.createBitmap(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height()));
                }

                Intent intent = new Intent(getApplicationContext(), SheetShowActivity.class);
                startActivity(intent);
            }
        });
    }

    public Rect getCropRect() {
        int x = ( bitmap.getWidth() * ( sheetFormatView.formatRect.rect.left - sheetFormatView.getLeft()) )
                / sheetFormatView.getWidth();
        int y = ( bitmap.getHeight() * (sheetFormatView.formatRect.rect.top - sheetFormatView.getTop()) )
                / sheetFormatView.getHeight();

        int width = (bitmap.getWidth() * sheetFormatView.formatRect.rect.width()) / sheetFormatView.getWidth();

        int height = (bitmap.getHeight() * sheetFormatView.formatRect.rect.height()) / sheetFormatView.getHeight();

        if ( x < 0 || y < 0 || width <= 0 || height <= 0 ) {
            //TODO: bad crop
            return null;
        } else {
            Rect rect = new Rect(x, y, x + width, y + height);
            Log.d(LOG_TAG, rect.toString() );

            return rect;
        }
    }

}