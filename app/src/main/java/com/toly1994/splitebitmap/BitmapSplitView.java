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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:17:07<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：
 */
public class BitmapSplitView extends View {
    private Point mCoo = new Point(800, 400);//坐标系
    private Picture mCooPicture;//坐标系canvas元件
    private Picture mGridPicture;//网格canvas元件
    private Paint mHelpPint;//辅助画笔


    private Paint mPaint;//主画笔
    private Path mPath;//主路径
    private Bitmap mBitmap;
    private int[][] mColArr;

    private int d = 3;//复刻的像素边长
    private List<Ball> mBalls = new ArrayList<>();//粒子集合
    private ValueAnimator mAnimator;//时间流
    private long mRunTime;//粒子运动时刻
    private boolean isOK;//结束的flag
    private int curBitmapIndex = 0;//当前图片索引
    private Bitmap[] mBitmaps;//图片数组

    public BitmapSplitView(Context context) {
        this(context, null);
    }

    public BitmapSplitView(Context context, @Nullable AttributeSet attrs) {
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
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.iv_100x100);
//
//
//        int color_0_0 = mBitmap.getPixel(0, 0);
//        L.d(color_0_0 + L.l());
//        mPaint.setColor(color_0_0);


        Bitmap bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, Color.BLACK);
        bitmap.setPixel(1, 0, Color.RED);
        bitmap.setPixel(0, 1, Color.WHITE);
        bitmap.setPixel(1, 1, Color.BLUE);

//        saveBitmap(Environment.getExternalStorageDirectory() + "/testC/toly/px_2x2.png", bitmap);

        int pixel_0_0 = bitmap.getPixel(0, 0);
        int pixel_1_0 = bitmap.getPixel(1, 0);
        int pixel_0_1 = bitmap.getPixel(0, 1);
        int pixel_1_1 = bitmap.getPixel(1, 1);

//        L.d("pixel_0_0:" + pixel_0_0 + L.l());
//        L.d("pixel_1_0:" + pixel_1_0 + L.l());
//        L.d("pixel_0_1:" + pixel_0_1 + L.l());
//        L.d("pixel_1_1:" + pixel_1_1 + L.l());

        printColor("pixel_0_0", pixel_0_0);//黑：a:255, r:0, g:0, b:0
        printColor("pixel_1_0", pixel_1_0);//红:a:255, r:255, g:0, b:0
        printColor("pixel_0_1", pixel_0_1);//白：a:255, r:255, g:255, b:255
        printColor("pixel_1_1", pixel_1_1);//蓝：a:255, r:0, g:0, b:255


        // pixel = -65536;
//        int a = Color.alpha(pixel);
//        int r = Color.red(pixel);
//        int g = Color.green(pixel);
//        int b = Color.blue(pixel);


//        mColArr = new int[bitmap.getWidth()][bitmap.getHeight()];

//        mBalls = initBall(bitmap.getWidth(), bitmap.getHeight());


//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());


//        //将newBitmap加入画板
//        Canvas canvas = new Canvas(newBitmap);
//        //准备画笔
//        Paint paint = new Paint();
//        //将按照原作图片绘制在新图
//        canvas.drawBitmap(bitmap, 0, 0, paint);

        initBall(mBitmap);
    }

    /**
     * 将颜色从int 拆分成argb,并打印出来
     *
     * @param msg
     * @param color
     */
    private void printColor(String msg, int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
//        L.d(msg + "----a:" + a + ", r:" + r + ", g:" + g + ", b:" + b + L.l());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
//        drawBitmapWithStar(canvas);

        for (Ball ball : mBalls) {
            mPaint.setColor(ball.color);
//            canvas.drawRect(
//                    ball.x - d / 2, ball.y - d / 2, ball.x + d / 2, ball.y + d / 2, mPaint);

            canvas.drawCircle(ball.x, ball.y, d/2, mPaint);
        }
        canvas.restore();
//        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
    }

    private void drawBitmapWithStar(Canvas canvas) {
        for (int i = 0; i < mBalls.size(); i++) {
            canvas.save();
            int line = i % mBitmap.getHeight();
            int row = i / mBitmap.getWidth();
            canvas.translate(row * 2 * d, line * 2 * d);
            mPaint.setColor(mBalls.get(i).color);
//            canvas.drawPath(CommonPath.nStarPath(5, d, d / 2), mPaint);
            canvas.restore();

        }
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
            ball.x += ball.vX;
            ball.y += ball.vY;
            ball.vY += ball.aY;
            ball.vX += ball.aX;
        }
    }

    /**
     * 根像素初始化粒子
     *
     * @param bitmap
     * @return
     */
    private void initBall(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                Ball ball = new Ball();//产生粒子---每个粒子拥有随机的一些属性信息
                ball.x = i * d + d / 2;
                ball.y = j * d + d / 2;
                ball.vX = (float) (Math.pow(-1, Math.ceil(Math.random() * 1001)) * 40 * Math.random());
                ball.vY = 0;
                ball.aX = 0.98f;
                ball.color = bitmap.getPixel(i, j);
                ball.born = System.currentTimeMillis();
                mBalls.add(ball);
            }
        }
    }

    /**
     * 配凑黑白颜色
     *
     * @param a
     * @param r
     * @param g
     * @param b
     * @return
     */
    private int blackAndWhite(int a, int r, int g, int b) {
        //拼凑出新的颜色
        int grey = (int) (r * 0.3 + g * 0.59 + b * 0.11);
        if (grey > 255 / 2) {
            grey = 255;
        } else {
            grey = 0;
        }
        return Color.argb(a, grey, grey, grey);
    }

    /**
     * 配凑灰颜色
     *
     * @param a
     * @param r
     * @param g
     * @param b
     * @return
     */
    private int grey(int a, int r, int g, int b) {
        //拼凑出新的颜色
        int grey = (int) (r * 0.3 + g * 0.59 + b * 0.11);
        return Color.argb(a, grey, grey, grey);
    }

    //
    private int reverse(int a, int r, int g, int b) {
        //拼凑出新的颜色
        return Color.argb(a, 255 - r, 255 - g, 255 - b);
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
     *
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

    /**
     * 保存bitmap到本地
     *
     * @param path    路径
     * @param mBitmap 图片
     * @return 路径
     */
    public static String saveBitmap(String path, Bitmap mBitmap) {

        File file = new File(path);
        if (file.isDirectory()) {
            return null;
        }

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }
}