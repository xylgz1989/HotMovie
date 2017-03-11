package com.xyl.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xyl on 2017/2/2 0002.
 */

public class FileUtil {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getAvailableCacheDir(Context ctx) {
        if (isExternalStorageWritable()) {
            return ctx.getExternalCacheDir();
        } else {
            return ctx.getCacheDir();
        }
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            bos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteDir(File directory) {
        if (directory != null){
            if (directory.isFile()) {
                directory.delete();
                return;
            }

            if (directory.isDirectory()) {
                File[] childFiles = directory.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    directory.delete();
                    return;
                }

                for (int i = 0; i < childFiles.length; i++) {
                    deleteDir(childFiles[i]);
                }
                directory.delete();
            }
        }
    }

    public static File saveBitmap(Bitmap bitmap, String fileName, File baseFile) {
        FileOutputStream bos = null;
        File imgFile = new File(baseFile, "/" + fileName);
        try {
            bos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    public static File getBaseFile(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File f = new File(Environment.getExternalStorageDirectory(),
                    filePath);
            if (!f.exists())
                f.mkdirs();
            return f;
        } else {
            return null;
        }
    }

    /**
     * 由指定的路径和文件名创建文件
     */
    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


}
