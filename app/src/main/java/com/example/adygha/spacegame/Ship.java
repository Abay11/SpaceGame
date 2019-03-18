package com.example.adygha.spacegame;

import android.content.Context;

public class Ship extends SpaceBody {
    int current_frame;
    int nframes;

    public Ship(Context context) {
        size = 8;
        x=10;
        y=GameView.maxY - size - 1;
        speed = 0.2f;

        nframes = 8;

        int bitmapIDs[] = new int[nframes];

        bitmapIDs[0] = R.drawable.ship_1;
        bitmapIDs[1] = R.drawable.ship_2;
        bitmapIDs[2] = R.drawable.ship_3;
        bitmapIDs[3] = R.drawable.ship_4;
        bitmapIDs[4] = R.drawable.ship_5;
        bitmapIDs[5] = R.drawable.ship_6;
        bitmapIDs[6] = R.drawable.ship_7;
        bitmapIDs[7] = R.drawable.ship_8;

        init(context, nframes, bitmapIDs); // инициализируем корабль
    }

    @Override
    public void update() { // перемещаем корабль в зависимости от нажатой кнопки

        if(GameActivity.isLeftPressed && x >= 0){
            x -= speed;
        }
        if(GameActivity.isRightPressed && x <= GameView.maxX - size){
            x += speed;
        }
        if(GameActivity.isUpPressed && y>=0) //проверяем, была ли нажата кнопка вверх и перемещаем корабль вверх только если он не достиг верхнего края
        {
            y -= speed;
        }
        if(GameActivity.isDownPressed && y<= GameView.maxY - size) //точно так же, спускаем корабль, если есть куда его еще спустить
        {
            y += speed;
        }

        if(++current_frame >= nframes)
        {
            current_frame = 0;
        }

        bitmap = animations[current_frame];
    }
}
