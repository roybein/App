package com.example.sheetshowtry.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

public class SheetFormatView extends ImageView {

    private static String LOG_TAG = "SheetFormatView";
    private Paint paint = null;
    protected FormatRect formatRect = null;
    int snappedAngleID = -1;

    public SheetFormatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setAlpha(128);
        paint.setStrokeWidth(2);
        formatRect = new FormatRect(50, 50, 300, 300, 120, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(LOG_TAG, "draw rect" + formatRect.rect.toString());
        canvas.drawRect(formatRect.rect, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();

        Log.d(LOG_TAG, "got touch event: " + action);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                snappedAngleID = formatRect.getSnapedAngle(X, Y);
                if (snappedAngleID != -1) {
                    //TODO: snapped an angle, show extend hint anim
                    Log.d(LOG_TAG, "got snapped angle");
                } else {
                    Log.e(LOG_TAG, "not snapped angle");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (snappedAngleID != -1) {
                    if (formatRect.checkAngleMove(snappedAngleID, X, Y)) {
                        formatRect.AngleMove(snappedAngleID, X, Y);
                        formatRect.resizeByAngle();
                        invalidate();
                    } else {
                        //TODO: illegal angle move
                        Log.d(LOG_TAG, "illegal angle move");
                    }
                } else {
                    //TODO: not snapped an angle yet
                    Log.e(LOG_TAG, "not snapped angel");
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return true;
    }

    public class FormatRect {
        private static final int angleBallNum = 4;
        public Rect rect;
        public ArrayList<AngleBall> angleBalls;
        public int boundMargin = 0;

        public FormatRect(int left, int top, int right, int bottom, int boundMargin, int angleBallRadius) {
            rect = new Rect(left, top, right, bottom);
            this.boundMargin = boundMargin;
            angleBalls = new ArrayList<AngleBall>(angleBallNum);
            angleBalls.add(0, new AngleBall(0, left, top, angleBallRadius));
            angleBalls.add(1, new AngleBall(1, right, top, angleBallRadius));
            angleBalls.add(2, new AngleBall(2, left, bottom, angleBallRadius));
            angleBalls.add(3, new AngleBall(3, right, bottom, angleBallRadius));
        }

        public int getSnapedAngle(int x, int y) {
            for ( AngleBall angleBall : angleBalls) {
                if (angleBall.IsInBall(x, y)) {
                    return angleBall.getID();
                }
            }
            // didn't snap any angle
            return -1;
        }

        public int getAlignedAngle(int axis, int srcID) {
            if (axis == Axis.X) {
                // get X aligned angle
                switch (srcID) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 0;
                    case 3:
                        return 1;
                }
            } else if (axis == Axis.Y) {
                // get Y aligned angle
                switch (srcID) {
                    case 0:
                        return 1;
                    case 1:
                        return 0;
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                }
            }
            //TODO: invalid axis
            return -1;

        }

        public boolean checkAngleMove(int id, int x, int y) {
            boolean checkResult = true;
            Log.d(LOG_TAG, x + "," + getRight());
            switch (id) {
                case 0:
                    if (x > (rect.right - boundMargin) || y > (rect.bottom - boundMargin) ) {
                        checkResult = false;
                    }
                    break;
                case 1:
                    if (x < (rect.left + boundMargin) || y > (rect.bottom - boundMargin) ) {
                        checkResult = false;
                    }
                    break;
                case 2:
                    if (x > (rect.right - boundMargin) || y < (rect.top + boundMargin) ) {
                        checkResult = false;
                    }
                    break;
                case 3:
                    if (x < (rect.left + boundMargin) || y < (rect.top + boundMargin) ) {
                        checkResult = false;
                    }
                    break;
                default:
                    //TODO: invalid angle ID
                    checkResult = false;
            }
            if (!checkResult) {
                Log.d(LOG_TAG, "illegal move");
            }
            return checkResult;
        }

        public void AngleMove(int id, int dstX, int dstY) {
            AngleBall angleBall = angleBalls.get(id);
            angleBall.setX(dstX);
            angleBall.setY(dstY);
            angleBall = angleBalls.get(getAlignedAngle(Axis.X, id));
            angleBall.setX(dstX);
            angleBall = angleBalls.get(getAlignedAngle(Axis.Y, id));
            angleBall.setY(dstY);
        }

        public void resizeByAngle() {
            this.rect.set(angleBalls.get(0).getX(), angleBalls.get(0).getY(), angleBalls.get(3).getX(), angleBalls.get(3).getY());
            Log.d(LOG_TAG, "resize rect by angle to: " + this.rect.toString());
        }
    }

    public class Axis {
        public static final int X = 0;
        public static final int Y = 1;
    }

    public class AngleBall {
        private int ID;
        private int x;
        private int y;
        private int radius;

        public AngleBall(int id, int x, int y, int radius) {
            this.ID = id;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
        public int getID() {
            return ID;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getRadius() {
            return radius;
        }

        public void setID(int id) {
            this.ID = id;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public void initBall(int id, int x, int y, int radius) {
            this.ID = id;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public void moveBallTo(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        boolean IsInBall(int x, int y) {
            Log.d(LOG_TAG, "IsInBall:" + x +"," + y + ",");
            int distance = (int) Math.sqrt( Math.pow( Math.abs(x - getX()), 2) + Math.pow( Math.abs(y - getY()), 2) );
            Log.d(LOG_TAG, "distance: " + distance + "radius: " + radius);
            return (distance < getRadius());
        }
    }

}