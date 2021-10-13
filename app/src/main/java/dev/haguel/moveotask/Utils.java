package dev.haguel.moveotask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String longToDateFormatter(long timeInMillis, boolean withHours){
        if (withHours){
            return new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(timeInMillis);
        }
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(timeInMillis);
    }

}
