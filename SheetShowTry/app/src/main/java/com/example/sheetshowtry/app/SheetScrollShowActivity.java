package com.example.sheetshowtry.app;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

public class SheetScrollShowActivity extends Activity {

    private Bitmap bitmap;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    private ImageView sheetScrollShowView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_sheet_scroll_show);

        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        Log.i(null, "get bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());
        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);
        sheetScrollShowView.setAdjustViewBounds(true);
        sheetScrollShowView.setScaleType(ImageView.ScaleType.MATRIX);
        sheetScrollShowView.setImageBitmap(bitmap);

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        Matrix matrix = new Matrix();
        float scaleRate = (float) screenSize.x/bitmap.getWidth();
        matrix.postScale(scaleRate, scaleRate);
        sheetScrollShowView.setImageMatrix(matrix);
        sheetScrollShowView.setOnTouchListener(new ImageShowTouchListener());
        //sheetScrollShowView.setOnClickListener(new TestClickListener());
    }

    class TestClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i(null, "onClick");
            Matrix matrix = new Matrix();
            matrix.postScale(2, 2);
            sheetScrollShowView.setImageMatrix(matrix);
        }
    }

    class ImageShowTouchListener implements View.OnTouchListener {
        private Matrix matrix = new Matrix();
        private PointF startPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(null, "ACTION_DOWN");
                    matrix.set(view.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(null, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(null, "ACTION_MOVE");
                    matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    startPoint.set(event.getX(), event.getY());
                    break;
            }

            Log.i(null, "view set image matrix" + matrix.toString());
            view.setImageMatrix(matrix);
            return true;
        }
    }
}