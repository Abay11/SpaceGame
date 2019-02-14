package com.example.adygha.spacegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{

    private final String TAG="SPACE_GAME";

    private ArrayList<Asteroid> asteroids = new ArrayList<>(); // тут будут харанится астероиды
    private final int ASTEROID_INTERVAL = 1000000000; // время через которое появляются астероиды (в итерациях)
    private int currentTime = 0;

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
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private ControlButtons controls;

    private Context context;

    private AlertDialog gameoverDialog=null;

    public GameView(Context context) {
        super(context);
        this.context=context;

        //инициализируем обьекты для рисования
        surfaceHolder = getHolder();
        paint = new Paint();

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
            draw();
            checkCollision();
            checkIfNewAsteroid();
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

                //выделяем координаты кнопок на общей кнопке управления
                float horizPixelsOfOnePiece = controls.size / 3 * unitW;
                float vertPixelsOfOnePiece = controls.size / 3 * unitW;

                float upTopY = controls.y * unitH; //координата верхней границы кнопки ВВЕРХ
                float upBottomY = upTopY + vertPixelsOfOnePiece; //координата нижней границы кнопки ВНИЗ

                float downTopY = upBottomY + vertPixelsOfOnePiece; //коорд верхней границы кнопки ВНИЗ
                float downBottomY = downTopY + vertPixelsOfOnePiece; //коорд нижней границы кнопки ВНИЗ

                float leftLeftX = controls.x * unitW; //координата левой границы кнопки ВЛЕВО
                float leftRightX = leftLeftX + horizPixelsOfOnePiece; //координата правой границы кнопки ВЛЕВО

                float rightLeftX = leftRightX + horizPixelsOfOnePiece; //координата левой границы кнопки ВПРАВО
                float rightRightX = rightLeftX + horizPixelsOfOnePiece; //координата правой границы кнопки ВПРАВО

                if((upTopY <= touchY && touchY <= upBottomY)
                        && (leftRightX <= touchX && touchX <= rightLeftX)) //кнопка вверх
                {
                    Log.d(TAG, "Command: UP!");
                }
                else if((downTopY <= touchY && touchY <= downBottomY)
                        && (leftRightX <= touchX && touchX <= rightLeftX)) //кнопка вниз
                {
                    Log.d(TAG, "Command: DOWN!");
                }
                else if((leftLeftX <= touchX && touchX <= leftRightX)
                        && (upBottomY <= touchY && touchY <= downTopY)) //кнопка влево
                {
                    Log.d(TAG, "Command: LEFT!");
                }
                else if((rightLeftX <= touchX && touchX <= rightRightX)
                        && (upBottomY <= touchY && touchY <= downTopY)) //кнопка вправо
                {
                    Log.d(TAG, "Command: RIGHT!");
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
                    builder.setMessage("GAME OVER!");
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
            }

            canvas = surfaceHolder.lockCanvas(); // закрываем canvas
            canvas.drawColor(Color.YELLOW); // заполняем фон чёрным

            ship.drow(paint, canvas); // рисуем корабль
            controls.drow(paint, canvas); //рисуем кнопки

            for(Asteroid asteroid: asteroids){ // рисуем астероиды
                asteroid.drow(paint, canvas);
            }

            //добавляем кнопки управления
            surfaceHolder.unlockCanvasAndPost(canvas); // открываем canvas
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
                // игрок проиграл
                gameRunning = false; // останавливаем игру
                }
                // TODO добавить анимацию взрыва
            }
    }

    private void checkIfNewAsteroid(){ // каждые 50 итераций добавляем новый астероид
        if(currentTime >= ASTEROID_INTERVAL){
            Asteroid asteroid = new Asteroid(getContext());
            asteroids.add(asteroid);
            currentTime = 0;
        }else{
            ++currentTime;
        }
    }
}
