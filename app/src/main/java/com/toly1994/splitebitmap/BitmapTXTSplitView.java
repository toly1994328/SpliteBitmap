package com.toly1994.splitebitmap;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


import java.util.ArrayList;
import java.util.List;


/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:17:07<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：
 */
public class BitmapTXTSplitView extends View {
    private Point mCoo = new Point(600, 300);//坐标系
    private Picture mCooPicture;//坐标系canvas元件
    private Picture mGridPicture;//网格canvas元件
    private Paint mHelpPint;//辅助画笔


    private Paint mPaint;//主画笔
    private Path mPath;//主路径
    private Bitmap mBitmap;
    private int[][] mColArr;



    private List<Ball> mBalls = new ArrayList<>();//粒子集合
    private ValueAnimator mAnimator;//时间流
    private long mRunTime;//粒子运动时刻
    private boolean isOK;//结束的flag
    private int curBitmapIndex = 0;//当前图片索引
    private Bitmap[] mBitmaps;//图片数组

    public BitmapTXTSplitView(Context context) {
        this(context, null);
    }

    public BitmapTXTSplitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//初始化
    }

    private void init() {
        //初始化主画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5);
        //初始化主路径
        mPath = new Path();



        //初始化时间流ValueAnimator
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(2000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            updateBall();//更新小球位置
            invalidate();
        });


        //加载图片数组
        mBitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.mipmap.thank_you),
                BitmapFactory.decodeResource(getResources(), R.mipmap.bye),
                BitmapFactory.decodeResource(getResources(), R.mipmap.go_on)
        };

        bitmap2Ball(mBitmaps[curBitmapIndex]);

//        int color_0_0 = bitmap.getPixel(0, 0);
//        mPaint.setColor(color_0_0);

//        mColArr = new int[bitmap.getWidth()][bitmap.getHeight()];

//        mBalls = initBall(bitmap.getWidth(), bitmap.getHeight());


//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());


//        //将newBitmap加入画板
//        Canvas canvas = new Canvas(newBitmap);
//        //准备画笔
//        Paint paint = new Paint();
//        //将按照原作图片绘制在新图
//        canvas.drawBitmap(bitmap, 0, 0, paint);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        for (Ball ball : mBalls) {
            mPaint.setColor(ball.color);
            canvas.drawCircle(ball.x, ball.y, 2, mPaint);
        }

        //TODO ----drawSomething
        canvas.restore();
//        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRunTime = System.currentTimeMillis();//记录点击时间
                mAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
//                mAnimator.pause();
                break;
        }
        return true;
    }


    /**
     * 更新小球
     */
    private void updateBall() {
        for (int i = 0; i < mBalls.size(); i++) {
            Ball ball = mBalls.get(i);
            if (System.currentTimeMillis() - mRunTime > 2000) {
                mBalls.remove(i);
            }

            if (mBalls.isEmpty()) {//表示本张已结束
                if (curBitmapIndex == 2) {
                    mAnimator.end();
                    return;
                }
                curBitmapIndex++;
                bitmap2Ball(mBitmaps[curBitmapIndex]);

                isOK = true;

                invalidate();
                mRunTime = System.currentTimeMillis();
                mAnimator.pause();
            }

            if (isOK) {//如果本张结束---返回掉
                isOK = false;
                return;
            }
            
            ball.x += ball.vX;
            ball.y += ball.vY;
            ball.vY += ball.aY;
            ball.vX += ball.aX;
        }
    }


    private List<Ball> initBall(int w, int h) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Ball ball = new Ball();
                ball.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
                ball.vY = rangeInt(-15, 35);
                ball.aY = 0.98f;
                ball.x = i * 4;
                ball.y = j * 4;
                ball.color = mColArr[i][j];
                mBalls.add(ball);
            }
        }
        return mBalls;
    }

    /**
     * 获取范围随机整数：如 rangeInt(1,9)
     *
     * @param s 前数(包括)
     * @param e 后数(包括)
     * @return 范围随机整数
     */
    public static int rangeInt(int s, int e) {
        int max = Math.max(s, e);
        int min = Math.min(s, e) - 1;
        return (int) (min + Math.ceil(Math.random() * (max - min)));
    }

    /**
     * 将一个图片粒子化
     * @param bitmap
     */
    public void bitmap2Ball(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                if (pixel < 0) {//此处过滤掉其他颜色，避免全部产生粒子
                    Ball ball = new Ball();//产生粒子---每个粒子拥有随机的一些属性信息
                    ball.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1000)) * 20 * Math.random());
                    ball.vY = rangeInt(-15, 35);
                    ball.aY = 0.98f;
                    ball.x = i * 4;
                    ball.y = j * 4;
                    ball.color = pixel;
                    ball.born = System.currentTimeMillis();
                    mBalls.add(ball);
                }
            }
        }
    }
}