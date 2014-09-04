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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

public class SheetScrollShowActivity extends Activity {

    private Bitmap bitmap;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    private View topLayoutView;
    public Rect topLayoutRect;
    private ImageView sheetScrollShowView;
    private ViewTreeObserver vto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sheet_scroll_show);

        topLayoutView = findViewById(R.id.top_layout);
        vto = topLayoutView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //vto.removeOnGlobalLayoutListener(this);
                topLayoutRect = new Rect();
                topLayoutView.getDrawingRect(topLayoutRect);
                Log.i(null, "get topLayoutRect=" + topLayoutRect.toShortString());
                sheetScrollShowView.setOnTouchListener(new ImageShowTouchListener());
            }
        });


        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        Log.i(null, "get bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        float scaleRate = (float) screenSize.x/bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);
        sheetScrollShowView.setAdjustViewBounds(true);
        sheetScrollShowView.setScaleType(ImageView.ScaleType.MATRIX);
        sheetScrollShowView.setImageBitmap(bitmap);

        //sheetScrollShowView.setOnClickListener(new TestClickListener());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        private float offsetY = 0f;
        private float offsetBoundMargin = topLayoutRect.height() * 0.2f;
        private float offsetBoundTop = 0f - (bitmap.getHeight() - topLayoutRect.height()) - offsetBoundMargin;
        private float offsetBoundBottom = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ImageView imageView = (ImageView) v;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(null, "ACTION_DOWN");
                    matrix.set(imageView.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(null, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(null, "ACTION_MOVE");
                    //matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    float distanceY = event.getY() - startPoint.y;
                    Log.i(null, "distanceY=" + distanceY + " " + "recent image offset=" + offsetY );
                    if ( (offsetY + distanceY) < offsetBoundTop ) {
                        distanceY = offsetBoundTop - offsetY;
                    }

                    if ( (offsetY + distanceY) > offsetBoundBottom ) {
                        distanceY = offsetBoundBottom - offsetY;
                    }

                    matrix.postTranslate(0, distanceY);
                    startPoint.set(event.getX(), event.getY());
                    offsetY += distanceY;
                    break;
            }

            Log.i(null, "view set image matrix" + matrix.toString());

            imageView.setImageMatrix(matrix);
            return true;
        }
    }
}