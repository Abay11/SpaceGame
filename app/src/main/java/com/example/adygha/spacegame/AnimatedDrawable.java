package com.example.adygha.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AnimatedDrawable extends Drawable {
    protected Bitmap animations[];
    int current_frame;
    int nframes;

    void init(Context context, int nframes, int bitmapIDs[])
    {
        this.nframes = nframes;

        current_frame = 0;

        animations = new Bitmap[nframes];

        for(int i=0; i<this.nframes; ++i)
        {
            Bitmap cbitmap = BitmapFactory.decodeResource(context.getResources(),  bitmapIDs[i]);
            animations[i] = Bitmap.createScaledBitmap(cbitmap, (int)(size * GameView.unitW), (int)(size * GameView.unitH), false);
            cbitmap.recycle();
        }

        bitmap = animations[current_frame];
    }
}
