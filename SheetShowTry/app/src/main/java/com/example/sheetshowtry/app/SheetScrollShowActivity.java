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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.sheetshowtry.app.craft.FitAxisEnum;

import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class SheetScrollShowActivity extends Activity {

    private Bitmap bitmap;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    public Rect imageViewRect;
    private ImageView sheetScrollShowView;
    private ViewTreeObserver vto;
    private Sheet sheet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sheet_scroll_show);

        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        Log.i(null, "get bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());

        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);

        sheet = new Sheet(bitmap, sheetScrollShowView);
        sheet.convertBitmapToFullScreen();
        sheet.show();

        vto = sheetScrollShowView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //vto.removeOnGlobalLayoutListener(this);
                imageViewRect = new Rect();
                sheetScrollShowView.getDrawingRect(imageViewRect);
                Log.i(null, "get imageViewRect=" + imageViewRect.toShortString());
                sheet.setImageViewRect(imageViewRect);
                sheet.initOffsetRect();
                //sheetScrollShowView.setOnTouchListener(new ImageShowTouchListener());
                sheetScrollShowView.setOnTouchListener(new SheetOnTouchListener(sheet));
            }
        });
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

    class ImageTimeLineRecorder {
    }

    class Sheet {
        private ImageView imageView;
        private Rect imageViewRect;
        private Bitmap bmp;
        private RectF offsetRect;
        private Matrix matrix;
        private PointF offsetVect;
        private FitAxisEnum fitAxis = FitAxisEnum.FitAxisNone;

        protected Sheet(Bitmap bmp, ImageView view) {
            this.imageView = view;
            this.bmp = bmp;
            offsetRect = new RectF();
            matrix = new Matrix();
            offsetVect = new PointF();
        }

        protected void setImageViewRect(Rect rect) {
            this.imageViewRect = rect;
        }
        protected void convertBitmapToFullScreen() {
            Display display = getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            display.getSize(screenSize);
            float scaleRate = (float) screenSize.x/bmp.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleRate, scaleRate);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        protected void getImageViewRect() {
            imageView.getDrawingRect(imageViewRect);
        }

        protected void initOffsetRect() {
            Log.d(null, "imageViewRect: " + imageViewRect.width() + "," + imageViewRect.height());
            Log.d(null, "bmp: " + bmp.getWidth() + "," + bmp.getHeight());
            offsetRect.left = imageViewRect.width() - bmp.getWidth();
            offsetRect.right = 0;
            offsetRect.top = imageViewRect.height() - bmp.getHeight();
            offsetRect.bottom = 0;
            Log.i(null, "init offset rectangle: " + offsetRect.toString());
        }

        protected void setFitAxis(FitAxisEnum fitAxis) {
            this.fitAxis = fitAxis;
        }

        protected void show() {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageView.setImageBitmap(bmp);
        }

        protected void move(PointF moveVect) {
            Log.d(null, "move by vector: " + moveVect.toString() + " from offsetVect: " + offsetVect.toString());
            PointF newOffsetVect = new PointF();
            if (fitAxis.equals(FitAxisEnum.FitAxisX)) {
                Log.d(null, "fix axis x");
                moveVect.x = 0;
            } else if (fitAxis.equals(FitAxisEnum.FitAxisY)) {
                Log.d(null, "fix axis y");
                moveVect.y = 0;
            }
            newOffsetVect.set(offsetVect.x + moveVect.x, offsetVect.y + moveVect.y);
            if (offsetVect.x + moveVect.x < offsetRect.left) {
                Log.d(null, "left exceeded");
                newOffsetVect.x = offsetRect.left;
            }
            if (offsetVect.x + moveVect.x > offsetRect.right) {
                Log.d(null, "right exceeded");
                newOffsetVect.x = offsetRect.right;
            }
            if (offsetVect.y + moveVect.y < offsetRect.top) {
                Log.d(null, "top exceeded");
                newOffsetVect.y = offsetRect.top;
            }
            if (offsetVect.y + moveVect.y > offsetRect.bottom) {
                Log.d(null, "bottom exceeded");
                newOffsetVect.y = offsetRect.bottom;
            }
            Log.d(null, "newoffsetVect: " + newOffsetVect.toString());

            matrix.postTranslate(newOffsetVect.x - offsetVect.x , newOffsetVect.y - offsetVect.y);
            offsetVect.set(newOffsetVect);
        }

        protected void showWithMatrix() {
            imageView.setImageMatrix(matrix);
            matrix.set(imageView.getImageMatrix());
        }

    }

    class SheetMoveRecoder {

        private LinkedList<Timestamp> TSList;

        private long sTime;

        protected long getTS() {
            long cTime = System.currentTimeMillis();
            return sTime - cTime;
        }

        protected void addSTS(PointF moveVect) {

        }

        protected void insertSTS(long time, PointF moveVect) {

        }


    }

    class SheetOnTouchListener implements View.OnTouchListener {

        Sheet sheet;
        PointF sp;

        public SheetOnTouchListener(Sheet sheet) {
            this.sheet = sheet;
            sp = new PointF();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(null, "ACTION_DOWN");
                    sp.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(null, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(null, "ACTION_MOVE");
                    PointF moveVect = new PointF(event.getX() - sp.x, event.getY() - sp.y);
                    Log.d(null, "moveVect: " + moveVect.toString());
                    sp.set(event.getX(), event.getY());
                    sheet.move(moveVect);
            }

            sheet.showWithMatrix();
            return true;
        }
    }

    class ImageShowTouchListener implements View.OnTouchListener {
        private Matrix matrix = new Matrix();
        private PointF startPoint = new PointF();
        private float offsetY = 0f;
        private float offsetBoundMargin = imageViewRect.height() * 0.2f;
        private float offsetBoundTop = 0f - (bitmap.getHeight() - imageViewRect.height()) - offsetBoundMargin;
        private float offsetBoundBottom = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ImageView imageView = (ImageView) v;
            Log.i(null, "ImageView: " + imageView.getLeft() + "," + imageView.getTop());

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(null, "ACTION_DOWN");
                    matrix.set(imageView.getImageMatrix());
                    Log.i(null, "matrix at ACTION_DOWN" + matrix.toString());
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