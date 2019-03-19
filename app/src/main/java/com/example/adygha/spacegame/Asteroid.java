package com.example.adygha.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.Random;

public class Asteroid extends SpaceBody {
    public Asteroid(Context context) {
        Random random = new Random();

        bitmapId = R.drawable.gray_asteroid;

        size = 5;
        y=0;
        x = random.nextInt(GameView.maxX);
        x = x < GameView.maxX - size ? x : x - size ;

        speed = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();

        nframes = 4;

        animations = new Bitmap[nframes];

        int IDs[] = new int[4];
        IDs[0] = R.drawable.gray_asteroid;
        IDs[1] = R.drawable.gray_asteroid2;
        IDs[2] = R.drawable.gray_asteroid3;
        IDs[3] = R.drawable.brown_asteroid;


        int randID = random.nextInt(4);

        Bitmap cbitmap = BitmapFactory.decodeResource(context.getResources(), IDs[randID]);
        bitmap = animations[0] = Bitmap.createScaledBitmap(cbitmap, (int)(size * GameView.unitW), (int)(size * GameView.unitH), false);
        cbitmap.recycle();

        Matrix matrix = new Matrix();

        int width = (int)(size * GameView.unitW);
        int height = (int)(size * GameView.unitH);

        matrix.setRotate(90);

        for(int i=1; i<nframes; ++i)
        {
            animations[i] = Bitmap.createBitmap(animations[i - 1], 0, 0, width, height, matrix, false);
        }


//        init(context);
    }

    @Override
    public void update() {
        y += speed;

        if(++current_frame >= nframes)
            current_frame = 0;

        bitmap = animations[current_frame];
    }

    public boolean isCollision(float shipX, float shipY, float shipSize) {
        return !(((x+size) < shipX) || (x > (shipX+shipSize)) || ((y+size) < shipY) || (y > (shipY+shipSize)));
    }

    public static void increaseSpeed(float value)
    {
        minSpeed += value;
        maxSpeed += value;
    }

    private static float minSpeed = 0.1f; // минимальная скорость
    private static float maxSpeed = 0.3f; // максимальная скорость
}
