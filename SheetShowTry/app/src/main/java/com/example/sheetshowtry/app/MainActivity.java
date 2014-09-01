package com.example.sheetshowtry.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements IActivity {

    public Button buttonLoadImage;
    public Button btnShowScrollImage;
    public TextView textView;
    public ImageView imageView;
    public static FileCache fileCache;
    public static List<Bitmap> bitmapList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ActivityManager.addActivity(this);
        fileCache = new FileCache(getApplicationContext());
        bitmapList = new ArrayList<Bitmap>();
        buttonLoadImage = (Button) findViewById(R.id.button_load_image);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task(Task.TaskID.LOAD_IMAGE, null);
                Worker worker = new Worker(0, task);
            }
        });

        btnShowScrollImage = (Button) findViewById(R.id.button_scroll_show);
        btnShowScrollImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SheetScrollShowActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... params) {
//        textView = (TextView) findViewById(R.id.text_load_image);
//        textView.setText(params[0].toString());
//        imageView = (ImageView) findViewById(R.id.image_sheet);
//        imageView.setImageBitmap(bitmapList.get(0));

        Intent intent = new Intent(this, SheetManualFormatActivity.class);
        intent.putExtra("bitmap", "dummy string");
        startActivity(intent);
    }
}

