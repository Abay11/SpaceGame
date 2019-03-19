package com.example.adygha.spacegame;

import android.content.Context;

public class Bullet extends SpaceBody {
    public Bullet(Context context, float shipX, float shipY) {
        bitmapId = R.drawable.bullet;
        y = shipY;
        x = shipX;
        size = 3;
        speed = 0.3f;

        init(context);
    }

    @Override
    public void update() {
        y -= speed;
    }
}