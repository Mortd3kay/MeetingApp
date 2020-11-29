package com.lazysecs.meetingapp.utils.images;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;

import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private GetImageFromAsync bitmapListener;
    @SuppressLint("StaticFieldLeak")
    private CircleImageView imageView;

    public DownloadImageTask(GetImageFromAsync bitmapListener) {
        this.bitmapListener = bitmapListener;
        this.imageView = null;
    }

    public DownloadImageTask(GetImageFromAsync bitmapListener, CircleImageView imageView) {
        this.bitmapListener = bitmapListener;
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (imageView != null)
            imageView.setImageBitmap(result);
        else
            this.bitmapListener.getResult(result);
    }
}