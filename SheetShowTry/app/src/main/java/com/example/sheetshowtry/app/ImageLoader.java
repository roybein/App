package com.example.sheetshowtry.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ImageLoader {

    public final String LOG_TAG = "ImageLoader";
    public  List<URL> urls;
    public List<Bitmap> bitmaps;
    private FileCache fileCache;

    public ImageLoader(List<URL> urls, List<Bitmap> bitmaps, FileCache fileCache) {
        this.bitmaps = bitmaps;
        this.fileCache = fileCache;
        this.urls = urls;
    }

    public void loadImage() {
        for (URL url : urls) {
            Log.d(LOG_TAG, url.toString());
            File f = fileCache.getFile(url.toString());
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Referer", "http://www.ccjt.net/");
                InputStream is = con.getInputStream();
                OutputStream os = new FileOutputStream(f);
                CopyStream(is, os);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                if (bitmap != null) {
                    Log.d(LOG_TAG, "got bitmap");
                    bitmaps.add(bitmap);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void CopyStream(InputStream is, OutputStream os) {
        final int bufSize = 1024;
        int count = -1;
        try {
            byte[] buf = new byte[bufSize];
            while (true) {
                count = is.read(buf, 0, bufSize);
                if (count == -1)
                    break;
                os.write(buf, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
