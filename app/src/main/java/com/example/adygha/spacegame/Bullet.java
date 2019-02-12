package com.example.adygha.spacegame;

import android.content.Context;

import java.util.Random;

public class Bullet extends SpaceBody {
    public Bullet(Context context) {
        Random random = new Random();

        bitmapId = R.drawable.bullet;
        y=GameView.maxY - size - 1;
        x = 7;
        size = radius*2;
        speed = (float) 0.2;

        init(context);
    }

    @Override
    public void update() {
        y -= speed;
    }

    public boolean isCollision(float bulletX, float bulletY, float bulletSize) {
        return !(((x+size) < bulletX)||(x > (bulletX+bulletSize))||((y+size) < bulletY)||(y > (bulletY+bulletSize)));
    }

    private int radius = 1; // радиус
    private float minSpeed = (float) 0.5; // минимальная скорость
    private float maxSpeed = (float) 0.5; // максимальная скорость



}
