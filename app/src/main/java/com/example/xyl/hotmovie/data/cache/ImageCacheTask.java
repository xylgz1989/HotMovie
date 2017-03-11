package com.example.xyl.hotmovie.data.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xyl.tool.FileUtil;

import java.io.File;

/**
 * Created by xyl on 2017/2/2 0002.
 */

public class ImageCacheTask implements Target{

    private String name;
    private Context mCtx;
    private static final String TAG = "ImageCacheTask";
    private ImageView imageView;

    public ImageCacheTask(String name, ImageView imageView) {
        this.name = name;
        this.imageView = imageView;
        this.mCtx = imageView.getContext();
    }

    public ImageCacheTask(String name, Context mCtx) {
        this.name = name;
        this.mCtx = mCtx;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        String imageName = name;
        if(imageView != null){
            imageView.setImageBitmap(bitmap);
        }
        if(FileUtil.isExternalStorageWritable()){
            File tempCacheDir = FileUtil.getAvailableCacheDir(mCtx);
//            FileOutputStream fos = null;
            try {
                File cachedImg = FileUtil.saveBitmap(bitmap,imageName,tempCacheDir);
                Log.i(TAG, "file name:"+cachedImg.toString()+";file size: "+cachedImg.getTotalSpace());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
