package com.example.adygha.spacegame;

import android.content.Context;

public class Bullet extends SpaceBody {

    boolean isExploded;
    //нужен чтобы отразить столкновение пули с астероидом,
    // чтобы запустить анимацю взрыва

    boolean isTimeToLeft; // нужен чтобы показать, что пришло время пуле покинуть нас:(
    //на самом деле, служит для того, чтобы после того как проиграем всю анимацию взрыва,
    //сказать игре, что можно удалить эту пулю с экрана

    public Bullet(Context context, float shipX, float shipY) {
        bitmapId = R.drawable.bullet;
        y = shipY;
        x = shipX;
        size = 5;
        speed = 0.3f;

        isExploded = false;
        isTimeToLeft = false;

        nframes = 18;

        int bitmapIDs[] = new int[nframes];

        bitmapIDs[0] = R.drawable.bullet;
        bitmapIDs[1] = R.drawable.bullet_1;
        bitmapIDs[2] = R.drawable.bullet_2;
        bitmapIDs[3] = R.drawable.bullet_3;
        bitmapIDs[4] = R.drawable.bullet_4;
        bitmapIDs[5] = R.drawable.bullet_5;
        bitmapIDs[6] = R.drawable.bullet_6;
        bitmapIDs[7] = R.drawable.bullet_7;
        bitmapIDs[8] = R.drawable.bullet_8;
        bitmapIDs[9] = R.drawable.bullet_9;
        bitmapIDs[10] = R.drawable.bullet_10;
        bitmapIDs[11] = R.drawable.bullet_11;
        bitmapIDs[12] = R.drawable.bullet_12;
        bitmapIDs[13] = R.drawable.bullet_13;
        bitmapIDs[14] = R.drawable.bullet_14;
        bitmapIDs[15] = R.drawable.bullet_15;
        bitmapIDs[16] = R.drawable.bullet_16;
        bitmapIDs[17] = R.drawable.bullet_17;

        init(context, nframes, bitmapIDs);
    }

    public boolean getIsExploded()
    {
        return isExploded;
    }

    public void setExploded()
    {
        isExploded = true;
    }

    public boolean getIsTimeToLeft()
    {
        return isTimeToLeft;
    }

    @Override
    public void update() {
        if(isExploded)
        {
            //если пуля взорвалась, запускаем анимацию взрыва
            if(++current_frame >= nframes)
            {
                isTimeToLeft = true;
            }
            else
            {
                bitmap = animations[current_frame];
            }
        }
        else
        {
            //если же нет, просто двигаем его вперед
            y -= speed;
        }

    }
}