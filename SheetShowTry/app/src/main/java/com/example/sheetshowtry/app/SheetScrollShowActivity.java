package com.example.sheetshowtry.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class SheetScrollShowActivity extends Activity {

    private Bitmap bitmap = null;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    private ImageView sheetScrollShowView = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_scroll_show);

        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);
        sheetScrollShowView.setImageBitmap(bitmap);
        sheetScrollShowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView imageView = (ImageView) v;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        matrix
                }
            }
        });
    }
}

public class ImageShowTouchListener implements View.OnTouchListener {
    private Matrix matrix = new Matrix();
    private PointF startPoint = new PointF();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                startPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP;

                break;
            case MotionEvent.ACTION_MOVE;
                break;
        }

        view.setImageMatrix(matrix);
    }
}