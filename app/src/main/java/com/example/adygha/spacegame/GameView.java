package com.example.adygha.spacegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{

    private final String TAG="SPACE_GAME";

    private ArrayList<Bullet> bullets = new ArrayList<>(); //пули

    private ArrayList<Asteroid> asteroids = new ArrayList<>(); // тут будут харанится астероиды
    private final int ASTEROID_INTERVAL = 50; // время через которое появляются астероиды (в итерациях)
    private int currentTime = 0;

    private int LEVEL_INTERVAL  = 3000; // время через которое скорость падения астероидов увеличиваются.
    private int elapsedTime = 0;

    public static int maxX = 30; // размер по горизонтали, также можно 20
    public static int maxY = 50; // размер по вертикали, также можно 28
    public static float unitW = 0; // пикселей в юните по горизонтали
    public static float unitH = 0; // пикселей в юните по вертикали
    private boolean firstTime = true;
    private volatile boolean gameRunning = true;
    private volatile boolean isEntered=false;
    private volatile boolean replay=true;
    private Ship ship;
    private Thread gameThread = null;
    private Paint paint;
    private Paint alphaPaint;
    private Paint textPaint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private ControlButtons controls;
    private Background background;



    private Context context;

    private AlertDialog gameoverDialog=null;

    private int score;

    public GameView(Context context) {
        super(context);
        this.context=context;

        //инициализируем обьекты для рисования
        surfaceHolder = getHolder();

        paint = new Paint();

        alphaPaint = new Paint();
        alphaPaint.setAlpha(35);

        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);


        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run()
    {
        do{
            onGameCycle();
            while(!isEntered){}
            gameRunning=true;
            isEntered=false;
            asteroids.clear();
        }while(replay);
    }

    private void onGameCycle()
    {
        while (gameRunning)
        {
            update();

            removeHiddenBodies(asteroids);
            removeHiddenBodies(bullets);

            draw();

            checkCollision();

            checkIfNewAsteroid();

            checkIfLevelUp();

            control();
        }
        showGameOverDialog();
    }


    @Override
    public boolean onTouchEvent(MotionEvent motion)
    {
        switch (motion.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: {

                //координаты нажатий
                float touchX = motion.getRawX();
                float touchY = motion.getRawY();

                if(controls.leftRightX < touchX && touchX < controls.rightLeftX)
                {
                    if(controls.upTopY <= touchY && touchY <= controls.upBottomY) //кнопка вверх
                    {
                        GameActivity.isUpPressed = true;
                    }
                    else if(controls.downTopY <= touchY && touchY <= controls.downBottomY) //кнопка вниз
                    {
                        GameActivity.isDownPressed = true;
                    }
                }
                else if(controls.upBottomY < touchY && touchY < controls.downTopY)
                {
                    if(controls.leftLeftX <= touchX && touchX <= controls.leftRightX) //левая кнопка
                    {
                        GameActivity.isLeftPressed = true;
                    }
                    else if(controls.rightLeftX <= touchX && touchX <= controls.rightRightX) //правая кнопка
                    {
                        GameActivity.isRightPressed = true;
                    }
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                //нажатие пользователя прекращено
                GameActivity.isLeftPressed
                        = GameActivity.isRightPressed
                        = GameActivity.isUpPressed
                        = GameActivity.isDownPressed = false;
                break;
            }
        }

        return true;
    }

    private void showGameOverDialog()
    {
        //показать game over
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(gameoverDialog==null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("GAME OVER!").setCancelable(false);
                    builder.setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "play again clicked");
                            replay=true;
                            isEntered=true;
                        }
                    });
                    builder.setNegativeButton(R.string.return_to_main, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "cancel clicked");
                            replay=false;
                            isEntered=true;
                            //return to main activity
                            ((Activity) getContext()).finish();
                        }
                    });
                    gameoverDialog = builder.create();
                }

                gameoverDialog.show();
            }
        });
    }

    private void update() {
        if(!firstTime) {
            ship.update();
            for (Asteroid asteroid : asteroids) {
                asteroid.update();
            }

            for(int i=0; i<bullets.size();)
            {
                if(bullets.get(i).isTimeToLeft)
                    bullets.remove(i);
                else
                {
                    bullets.get(i).update();
                    ++i;
                }
            }

            background.update();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {  //проверяем валидный ли surface

            if(firstTime){ // инициализация при первом запуске
                firstTime = false;
                unitW = surfaceHolder.getSurfaceFrame().width()/maxX; // вычисляем число пикселей в юните
                unitH = surfaceHolder.getSurfaceFrame().height()/maxY;

                ship = new Ship(context); // добавляем корабль
                controls=new ControlButtons(context); //создаем кнопки

                background = new Background(context, surfaceHolder.getSurfaceFrame().width(),
                        surfaceHolder.getSurfaceFrame().height());

                score = 0;
            }

            canvas = surfaceHolder.lockCanvas(); // закрываем canvas

            drawBackground();

            //выводим очки на экран
            canvas.drawText("Scores: " + score, 10, 30, textPaint);

            ship.drow(paint, canvas); // рисуем корабль
            controls.drow(paint, canvas); //рисуем кнопки

            for(Asteroid asteroid: asteroids){ // рисуем астероиды
                asteroid.drow(paint, canvas);
            }

            for(Bullet b : bullets)
                b.drow(paint, canvas);

            //добавляем кнопки управления
            surfaceHolder.unlockCanvasAndPost(canvas); // открываем canvas
        }
    }

    private void drawBackground() {

        // Make a copy of the relevant background
        Background bg = background;

        // define what portion of images to capture and
        // what coordinates of screen to draw them at

        // For the regular bitmap
        Rect from1 = new Rect(0, 0, bg.width, bg.height - bg.yClip);
        Rect to1 = new Rect(0, bg.yClip, bg.width, bg.height);

        // For the reversed background
        Rect from2 = new Rect(0, bg.height - bg.yClip, bg.width, bg.height);
        Rect to2 = new Rect(0, 0, bg.width, bg.yClip);

        //draw the two background bitmaps
        if (!bg.reversedFirst) {
            canvas.drawBitmap(bg.bitmap, from2, to2, paint);
            canvas.drawBitmap(bg.bitmapReversed, from1, to1, paint);
        } else {
            canvas.drawBitmap(bg.bitmap, from1, to1, paint);
            canvas.drawBitmap(bg.bitmapReversed, from2, to2, paint);
        }

    }


    private void control() {
        // 1000ms / 60fps = 17 ms;
        // чтобы достичь 60 fps нужно выполнять отрисовку каждые 17 мс
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkCollision(){ // перебираем все астероиды и проверяем не касается ли один из них корабля
        for (Asteroid asteroid : asteroids) {
            if(asteroid.isCollision(ship.x, ship.y, ship.size)){
                gameRunning = false; // игрок проиграл, останавливаем игру
                return;
                }
                // TODO добавить анимацию взрыва
            }


        //проверяем столкновение астероидов с пулями
        for(int i=0; i<asteroids.size();) {
            for (int j = 0; j < bullets.size(); ) {
                if (asteroids.get(i).isCollision(bullets.get(j).x, bullets.get(j).y, bullets.get(j).size)
                        && !bullets.get(j).getIsExploded()) {
                    //увеличиваем набранные очки
                    ++score;
                    Log.d(TAG, "Score: " + score);

                    //при столкновении, удаляем астероид
                    asteroids.remove(i);

                    //непосредственно здесь пулю удалять не нужно,
                    //но нужно указать ему, что он столкнулся с астероидом,
                    //и что нужно запустить анимацию взрыва
                    bullets.get(j).setExploded();

                    break; //переходим к следующему астероиду
                } else {
                    //если эта пуля не столкнулась с астероидом,
                    // переходим для проверки к следующей пуле
                    j = j + 1;
                }
            }
            i = i + 1;
        }
    }

    private void checkIfNewAsteroid(){ // каждые 50 итераций добавляем новый астероид
        if(currentTime >= ASTEROID_INTERVAL){
            Asteroid asteroid = new Asteroid(getContext());
            asteroids.add(asteroid);

            Bullet bullet = new Bullet(context, ship.x, ship.y);
            bullets.add(bullet);

            currentTime = 0;
        }else{
            ++currentTime;
        }
    }

    private void checkIfLevelUp(){
       if(elapsedTime >= LEVEL_INTERVAL ){
            Asteroid.increaseSpeed(0.2f);
            elapsedTime = 0;
        }else{
            ++elapsedTime;
        }
    }

    private <T> void removeHiddenBodies(ArrayList<T> list)
    {
        for(int i=0; i<list.size(); )
        {
            SpaceBody iter = (SpaceBody)list.get(i);
            if(iter.y < 0 || GameView.maxY < iter.y) //астероид вышел за видимые границы
            {
                Log.d(TAG, "An object deleted");
                list.remove(i);
            }
            else
            {
                ++i;
            }
        }
    }
}
