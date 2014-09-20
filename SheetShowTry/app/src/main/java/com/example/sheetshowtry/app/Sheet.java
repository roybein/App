package com.example.sheetshowtry.app;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.example.sheetshowtry.app.craft.FitAxisEnum;
import com.example.sheetshowtry.app.craft.MsgWhatEnum;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class Sheet {
    private final static String LOG_TAG = "Musibox";

    private Rect imageViewRect;
    private Bitmap bmp;
    private RectF offsetRect;
    private Matrix matrix;
    private PointF offsetVect;
    private FitAxisEnum fitAxis = FitAxisEnum.FitAxisNone;
    public ArrayList<PointF> offsetVectList;

    protected SheetMoveRecorder recorder;
    private Handler UIHandler = null;

    protected Sheet(Bitmap bmp, Handler UIHandler) {
        this.UIHandler = UIHandler;
        this.bmp = bmp;
        offsetRect = new RectF();
        matrix = new Matrix();
        offsetVect = new PointF();
        offsetVectList = new ArrayList<PointF>();
        recorder = new SheetMoveRecorder(this);
    }

    public void setUiHandler(Handler handler) {
        this.UIHandler = handler;
    }

    public PointF getOffsetVect() {
        return offsetVect;
    }
    protected void setImageViewRect(Rect rect) {
        this.imageViewRect = rect;
    }

    protected void initOffsetRect() {
        Log.d(LOG_TAG, "imageViewRect: " + imageViewRect.width() + "," + imageViewRect.height());
        Log.d(LOG_TAG, "bmp: " + bmp.getWidth() + "," + bmp.getHeight());
        offsetRect.left = imageViewRect.width() - bmp.getWidth();
        offsetRect.right = 0;
        offsetRect.top = imageViewRect.height() - bmp.getHeight();
        offsetRect.bottom = 0;
        Log.i(LOG_TAG, "init offset rectangle: " + offsetRect.toString());
    }

    protected void setFitAxis(FitAxisEnum fitAxis) {
        this.fitAxis = fitAxis;
    }

    protected void show() {
        UIHandler.obtainMessage(MsgWhatEnum.SHEET_SHOW.ordinal()).sendToTarget();
    }

    protected void showInActivity(ImageView imageView) {
        Log.i(LOG_TAG, "showInActivity");
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageBitmap(bmp);
    }

    protected void move(PointF moveVect) {
        Log.d(LOG_TAG, "move by vector: " + moveVect.toString() +
                " from offsetVect: " + offsetVect.toString());
        PointF newOffsetVect = new PointF();
        if (fitAxis.equals(FitAxisEnum.FitAxisX)) {
            Log.d(LOG_TAG, "fix axis x");
            moveVect.x = 0;
        } else if (fitAxis.equals(FitAxisEnum.FitAxisY)) {
            Log.d(LOG_TAG, "fix axis y");
            moveVect.y = 0;
        }
        newOffsetVect.set(offsetVect.x + moveVect.x, offsetVect.y + moveVect.y);
        if (offsetVect.x + moveVect.x < offsetRect.left) {
            Log.d(LOG_TAG, "left exceeded");
            newOffsetVect.x = offsetRect.left;
        }
        if (offsetVect.x + moveVect.x > offsetRect.right) {
            Log.d(LOG_TAG, "right exceeded");
            newOffsetVect.x = offsetRect.right;
        }
        if (offsetVect.y + moveVect.y < offsetRect.top) {
            Log.d(LOG_TAG, "top exceeded");
            newOffsetVect.y = offsetRect.top;
            //Message msg = handler.obtainMessage(MsgWhatEnum.SHEET_MOVE_REACH_END.ordinal());
            //handler.sendMessage(msg);
        }
        if (offsetVect.y + moveVect.y > offsetRect.bottom) {
            Log.d(LOG_TAG, "bottom exceeded");
            newOffsetVect.y = offsetRect.bottom;
        }
        Log.d(LOG_TAG, "newoffsetVect: " + newOffsetVect.toString());

        matrix.postTranslate(newOffsetVect.x - offsetVect.x , newOffsetVect.y - offsetVect.y);
        offsetVect.set(newOffsetVect);
    }

    protected void showWithMatrix() {
        UIHandler.obtainMessage(MsgWhatEnum.SHEET_SHOW_WITH_MATRIX.ordinal()).sendToTarget();
    }

    protected void showWithMatrixInActivity(ImageView imageView) {
        imageView.setImageMatrix(matrix);
        matrix.set(imageView.getImageMatrix());
    }
}
