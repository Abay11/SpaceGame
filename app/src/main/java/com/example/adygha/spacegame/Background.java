package com.example.adygha.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Background {
    Bitmap bitmap;
    Bitmap bitmapReversed;

    int width;
    int height;
    boolean reversedFirst;
    float speed;

    int yClip;
//    int startY;
//    int endY;

    Background(Context context, int screenWidth, int screenHeight){
         // Make a resource id out of the string of the file name
         int resID = context.getResources().getIdentifier("background",
            "drawable", context.getPackageName());

         // Load the bitmap using the id


        // Which version of background (reversed or regular)
        // is currently drawn first (on left)
        reversedFirst = false;

        //Initialise animation variables.

        // Where to clip the bitmaps
        // Starting at the first pixel
        yClip = 0;

        //Position the background vertically
//        startY = sY * (screenHeight / 100);
//        endY = eY * (screenHeight / 100);
        speed = 1;

        width = screenWidth;
        height = screenHeight;

        // Create the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
        bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight,true);

        //Create a mirror image of the background (horizontal flip)
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        bitmapReversed = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
}

    public void update()
    {
        // Move the clipping position and reverse if necessary
        yClip += speed;
        if (yClip >= height) {
            yClip = 0;
            reversedFirst = !reversedFirst;
        }/* else if (xClip < 0) {
            xClip = width;
            reversedFirst = !reversedFirst;
        }*/
    }
}
