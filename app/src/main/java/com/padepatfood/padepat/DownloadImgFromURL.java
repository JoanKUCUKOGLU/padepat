package com.padepatfood.padepat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImgFromURL extends AsyncTask<URL, Integer, Void> {

    Context myContext;

    File directory;

    String imageName;

    File mypath;

    FileOutputStream fos = null;

    private Integer id;
    private String url;

    DownloadImgFromURL(Context myContext, Integer id, String url) {
        this.myContext = myContext;
        this.id = id;
        this.url = url;
        this.imageName= "IMG_RECIPE_" + id + ".png";
        this.directory = new File(this.myContext.getFilesDir(), "recipesImages");
        Boolean created = directory.mkdir();
        this.mypath = new File(directory,imageName);
    }

    @Override
    protected Void doInBackground(URL... urls) {
        try {
            URL myUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
