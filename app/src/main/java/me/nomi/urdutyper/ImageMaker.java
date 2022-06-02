package me.nomi.urdutyper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageMaker {
    private static File pictureFile;

    public static Bitmap getBitmapFromView(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }


    public static Uri saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Nomiii");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Toast.makeText(context, "Failed to create the directory!", Toast.LENGTH_LONG).show();
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
        Date now = new Date();
        Prefs prefs = new Prefs(context);

        prefs.setStringEntry("filename", "UT" + formatter.format(now));
        String filename = pictureFileDir.getPath() + File.separator + prefs.getStringEntry("filename") + ".jpg";
        pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
            Toast.makeText(context, "Saved: " + filename, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "There was an error saving the image", Toast.LENGTH_LONG).show();
        }
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pictureFile);
    }

    public static Uri getImageFromImageView(Context context, ImageView iv, String filename) throws IOException {
        //to get the image from the ImageView (say iv)
        Drawable draw = iv.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(draw.getIntrinsicWidth(),
                draw.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        draw.draw(canvas);


        FileOutputStream outStream = null;
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Nomiii");
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdirs();
            if (!isDirectoryCreated)
                Toast.makeText(context, "Failed to create the directory!", Toast.LENGTH_LONG).show();
            return null;
        }
        File outFile = new File(dir.getPath() + File.separator + filename + ".jpg");
        outStream = new FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", outFile);
    }
}