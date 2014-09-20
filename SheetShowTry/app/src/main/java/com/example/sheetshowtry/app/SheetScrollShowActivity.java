package com.example.sheetshowtry.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sheetshowtry.app.craft.FitAxisEnum;
import com.example.sheetshowtry.app.craft.MsgWhatEnum;

import java.io.File;

public class SheetScrollShowActivity extends Activity {

    private final static String LOG_TAG = "Musibox";

    private Bitmap bitmap;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    public Rect imageViewRect;
    private ImageView sheetScrollShowView;
    private ViewTreeObserver vto;
    private Sheet sheet;
    public Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sheet_scroll_show);

        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);
        Button btnRecordStart = (Button) findViewById(R.id.sheet_record_start_button);
        Button btnRecordStop = (Button) findViewById(R.id.sheet_record_stop_button);

        btnRecordStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.recorder.startRecord();
            }
        });

        btnRecordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.recorder.stopRecord();
                Log.i(LOG_TAG, "record result: " + sheet.offsetVectList.toString());
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MsgWhatEnum what = MsgWhatEnum.values()[msg.what];
                switch (what) {
                    case SHEET_SHOW:
                        Log.i(LOG_TAG, "SHEET_SHOW");
                        sheet.showInActivity(sheetScrollShowView);
                        break;
                    case SHEET_SHOW_WITH_MATRIX:
                        sheet.showWithMatrixInActivity(sheetScrollShowView);
                        break;
                }
            }
        };

        init();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                init()
//            }
//        }).start();
    }

    protected void init() {
        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        Log.i(LOG_TAG, "get bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());
        bitmap = convertBitmapToFullScreen(bitmap);

        sheet = new Sheet(bitmap, handler);
        sheet.show();

        vto = sheetScrollShowView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                postLayoutInit();
            }
        });
    }

    protected void postLayoutInit() {
        imageViewRect = new Rect();
        sheetScrollShowView.getDrawingRect(imageViewRect);
        Log.i(LOG_TAG, "get imageViewRect=" + imageViewRect.toShortString());
        sheet.setImageViewRect(imageViewRect);
        sheet.initOffsetRect();
        sheet.setFitAxis(FitAxisEnum.FitAxisX);
        sheetScrollShowView.setOnTouchListener(new SheetOnTouchListener(sheet));
    }

    protected Bitmap convertBitmapToFullScreen(Bitmap bitmap) {
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        float scaleRate = (float) screenSize.x/bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        return bitmap;
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
                    Log.d(LOG_TAG, "ACTION_DOWN");
                    sp.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(LOG_TAG, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(LOG_TAG, "ACTION_MOVE");
                    PointF moveVect = new PointF(event.getX() - sp.x, event.getY() - sp.y);
                    Log.d(LOG_TAG, "moveVect: " + moveVect.toString());
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
        private float offsetBoundTop = 0f - (bitmap.getHeight() - imageViewRect.height()) -
                offsetBoundMargin;
        private float offsetBoundBottom = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ImageView imageView = (ImageView) v;
            Log.i(LOG_TAG, "ImageView: " + imageView.getLeft() + "," + imageView.getTop());

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(LOG_TAG, "ACTION_DOWN");
                    matrix.set(imageView.getImageMatrix());
                    Log.i(LOG_TAG, "matrix at ACTION_DOWN" + matrix.toString());
                    startPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(LOG_TAG, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(LOG_TAG, "ACTION_MOVE");
                    //matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    float distanceY = event.getY() - startPoint.y;
                    Log.i(LOG_TAG, "distanceY=" + distanceY + " " +
                            "recent image offset=" + offsetY );
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

            Log.i(LOG_TAG, "view set image matrix" + matrix.toString());

            imageView.setImageMatrix(matrix);
            return true;
        }
    }
}

