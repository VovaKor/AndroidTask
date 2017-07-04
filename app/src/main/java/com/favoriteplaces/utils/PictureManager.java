package com.favoriteplaces.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by vova on 04.07.17.
 */

public class PictureManager {
    public static final String SEPARATOR = "_";
    public static final String FILE_DATE_TEMPLATE = "yyyyMMdd_HHmmss";
    public static final String FILE_PREFIX = "JPEG_";
    public static final String FILE_EXTENSION = ".jpg";
    public static final String SLASH = "/";
    public static final int THUMBNAIL_WIDTH_BIG = 150;
    public static final int THUMBNAIL_HEIGHT_BIG = 150;
    public static final float CORRECTION = 0.5f;
    public static final int THUMBNAIL_WIDTH_SMALL = 60;
    public static final int THUMBNAIL_HEIGHT_SMALL = 60;
    private static PictureManager INSTANCE = null;

    public static PictureManager getInstance() {
        if (INSTANCE == null){
            INSTANCE = new PictureManager();
        }
        return INSTANCE;
    }

    private PictureManager() {
    }


    public void addPictureToGallery(String path, Activity activity) {
        // rotateImage(mCurrentPhotoPath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }
    public File createTempFile() throws IOException {
        String timeStamp = new SimpleDateFormat(FILE_DATE_TEMPLATE, Locale.getDefault()).format(new Date());
        String imageFileName = FILE_PREFIX + timeStamp + SEPARATOR;

        File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    FILE_EXTENSION,         /* suffix */
                    getStorageDirectory()      /* directory */
            );
        return image;
    }

    public Bitmap createBigImageBitmap(String fileName, float density) {

        String currentPhotoPath = getStorageDirectory().getAbsolutePath()+SLASH+fileName;
        // Get the dimensions of the View

        int targetW = (int) (THUMBNAIL_WIDTH_BIG * density + CORRECTION);
        int targetH = (int) (THUMBNAIL_HEIGHT_BIG * density + CORRECTION);
        Bitmap bitmap = getBitmap(currentPhotoPath, targetW, targetH);

        return bitmap;
    }

    public Bitmap createSmallImageBitmap(String fileName, float density) {
        String currentPhotoPath = getStorageDirectory().getAbsolutePath()+SLASH+fileName;
        // Get the dimensions of the View
        int targetW = (int) (THUMBNAIL_WIDTH_SMALL * density + CORRECTION);
        int targetH = (int) (THUMBNAIL_HEIGHT_SMALL * density + CORRECTION);
        Bitmap bitmap = getBitmap(currentPhotoPath, targetW, targetH);
        return bitmap;
    }

    public Bitmap getBitmap(String path, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        return BitmapFactory.decodeFile(path, bmOptions);
    }

    private File getStorageDirectory() {
        return   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    }
    private void rotateImage(String mCurrentPhotoPath) {
        String photopath = mCurrentPhotoPath;
        Bitmap bmp = BitmapFactory.decodeFile(photopath);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(mCurrentPhotoPath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Bitmap createImageBitmap(String fileName, int targetHeight, int targetWidth) {
        String currentPhotoPath = getStorageDirectory().getAbsolutePath()+SLASH+fileName;
        Bitmap bitmap = getBitmap(currentPhotoPath, targetWidth, targetHeight);
        return bitmap;
    }
}
