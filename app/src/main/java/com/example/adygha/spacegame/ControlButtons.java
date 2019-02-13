package com.example.adygha.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class ControlButtons {
    protected float x; // координаты
    protected float y;
    protected float size;
    protected int bitmapId; //id картинки
    protected Bitmap bitmap; //управление


    public ControlButtons(Context context)
    {
        bitmapId=R.drawable.controls;
        x=1;
        y=22;
        size=5;

        init(context);
    }

    void init(Context context) { // сжимаем картинку до нужных размеров
        Bitmap cBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        bitmap = Bitmap.createScaledBitmap(
                cBitmap, (int)(size * GameView.unitW), (int)(size * GameView.unitH), false);
        cBitmap.recycle();
    }

    void drow(Paint paint, Canvas canvas){ // рисуем картинку
        canvas.drawBitmap(bitmap, x*GameView.unitW, y*GameView.unitH, paint);
    }
}
