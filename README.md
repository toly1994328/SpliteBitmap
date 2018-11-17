#### 零、前言
>1.第一次接触粒子是在html5的canvas，说是html的canvas，倒不如说是JavaScript的canvas，毕竟核心都在js。  
2.经过长久的酝酿，感觉Java实现粒子运动好像也不是什么难事，今天将用Android作为视口，带你领略粒子的炫酷。  
3.关于性能方面，我想只要合理控制粒子的消失，还是可以接受的。只要不是无限级别，和游戏比起来，这点性能九牛一毛啦。  
4.粒子效果的核心有三个点：收集粒子、更改粒子、显示粒子  
5.为了纯粹，本文只实现下图的粒子效果:

![粒子效果](https://upload-images.jianshu.io/upload_images/9414344-f8514775af20c7de.gif?imageMogr2/auto-orient/strip)

#### 一、文字的粒子化思路

##### 1.资源准备

>经过我的思索，既然可以用二维数组实现数字的粒子化:[见:Android原生绘图之炫酷倒计时](https://www.jianshu.com/p/dd8e325b2ae3)，  
那么一个Bitmap不是天然包含一个二维的像素数组吗？二话不说，将图片调成黑字无底，遍历添加。

![准备图片.png](https://upload-images.jianshu.io/upload_images/9414344-46ec5116616f9785.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---

##### 2.粒子对象

```
/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:21:51<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：粒子对象
 */
public class Ball implements Cloneable {
    public float aX;//加速度
    public float aY;//加速度Y
    public float vX;//速度X
    public float vY;//速度Y
    public float x;//点位X
    public float y;//点位Y
    public int color;//颜色
    public float r;//半径
    public long born;//诞生时间

    public Ball clone() {
        Ball clone = null;
        try {
            clone = (Ball) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
```

---

##### 3.对粒子的收集

>这里遍历一下bitmap将所有的黑色像素收集到粒子集合中：


```
//成员变量：
private List<Ball> mBalls = new ArrayList<>();//粒子集合
//加载图片
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.thank_you);
for (int i = 0; i < bitmap.getWidth(); i++) {
    for (int j = 0; j < bitmap.getHeight(); j++) {
        int pixel = bitmap.getPixel(i, j);
        if (pixel < 0) {//此处过滤掉其他颜色，避免全部产生粒子
            Ball ball = new Ball();//产生粒子---每个粒子拥有随机的一些属性信息
            ball.vX = (float) (Math.pow(-1, Math.ceil(Math.random() 
            ball.vY = rangeInt(-15, 35);
            ball.aY = 0.98f;
            ball.x = i * 4;
            ball.y = j * 4;
            ball.color = pixel;
            ball.born = System.currentTimeMillis();
            mBalls.add(ball);
        }
          mColArr[i][j] = bitmap.getPixel(i, j);
    }
}
```

---

##### 4.粒子的显示
>也就是将粒子集合中的每个粒子绘制出来,非常简单  
但这时它已经不是文字或图片了，而是可操纵的粒子，是不是很兴奋


```
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();
    canvas.translate(mCoo.x, mCoo.y);
    for (Ball ball : mBalls) {//绘制小球集合
        mPaint.setColor(ball.color);
        canvas.drawCircle(ball.x, ball.y, 2, mPaint);
    }
    canvas.restore();
}
```

---
#### 二、粒子的运动思路

>结核运动学的一点知识，让小球拥有位移，速度，加速度的模拟，来实现运动，这里不过多赘述  
[我的这篇文章讲得非常细致](https://juejin.im/post/5bee10376fb9a04a0e2cc4c2)。


![thank_you.gif](https://upload-images.jianshu.io/upload_images/9414344-f911e2145cc7420b.gif?imageMogr2/auto-orient/strip)

##### 1.粒子的状态更新：
>其实也不复杂，就是在恒定时间流下对位移和速度进行运动学的累加

```
/**
 * 更新小球
 */
private void updateBall() {
    for (int i = 0; i < mBalls.size(); i++) {
        Ball ball = mBalls.get(i);
        ball.x += ball.vX;
        ball.y += ball.vY;
        ball.vY += ball.aY;
        ball.vX += ball.aX;
    }
}
```

##### 2.粒子的湮灭
>昨天在思考怎么能够更好控制粒子的湮灭呢?
粒子的湮灭说起来就是在一定的条件下将粒子从集合中移除，今早突然灵光一闪,可以用时间啊!

```
/**
 * 更新小球
 */
private void updateBall() {
    for (int i = 0; i < mBalls.size(); i++) {
        Ball ball = mBalls.get(i);
        if (System.currentTimeMillis() - mClickTime > 3000) 
            mBalls.remove(i);
        }
        ball.x += ball.vX;
        ball.y += ball.vY;
        ball.vY += ball.aY;
        ball.vX += ball.aX;
    }
}
```

##### 3.时间数字流：

```
//初始化时间流ValueAnimator
mAnimator = ValueAnimator.ofFloat(0, 1);
mAnimator.setRepeatCount(-1);
mAnimator.setDuration(2000);
mAnimator.addUpdateListener(animation -> {
    updateBall();//更新小球位置
    invalidate();
});
```

##### 4.点击事件

```
@Override
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mClickTime = System.currentTimeMillis();//记录点击时间
            mAnimator.start();
            break;
    }
    return true;
}
```

>这样粒子运动的效果就实现了，当然你也可以用任意的图片来进行粒子运动  
关于Bitmap的粒子运动会新写一篇来详细的论述，敬请期待。
---
#### 三、粒子动画结束监听:
>现在到了粒子全部湮灭的监听了,在一张图片的所有粒子湮灭后进入下一个图片：  
很容易想到在移除粒子是监听粒子集合是否为空

##### 1.成员变量准备

```
private List<Ball> mBalls = new ArrayList<>();//粒子集合
private ValueAnimator mAnimator;//时间流
private long mRunTime;//粒子运动时刻
private boolean isOK;//结束的flag
private int curBitmapIndex = 0;//当前图片索引
private Bitmap[] mBitmaps;//图片数组
```

##### 2.图片粒子化方法方法封装

```
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
```


##### 3.准备一个图片数组:

```
//加载图片数组
mBitmaps = new Bitmap[]{
        BitmapFactory.decodeResource(getResources(), R.mipmap.thank_you),
        BitmapFactory.decodeResource(getResources(), R.mipmap.bye),
        BitmapFactory.decodeResource(getResources(), R.mipmap.go_on)
};
bitmap2Ball(mBitmaps[curBitmapIndex]);//初始化第一张图
```

##### 4.监听图片碎裂未完成，进行下一张
>isOK这个flag让我头疼了几分钟，总算找到了何时的位置

```
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
```

![粒子效果](https://upload-images.jianshu.io/upload_images/9414344-f8514775af20c7de.gif?imageMogr2/auto-orient/strip)


>至此，本文的效果就已经实现了，是不是没有想象中的那么复杂，相信你也可以做到


---

#### 四、后记:

>这些天感谢柚子茶的帮助，无以为报，以文记之，祝掘金越来越好，帮助更多的技术开发者。   
本文`捷文规范`，不会再做任何修改。项目源码见[→_→ ~ GitHub ~ ←_←](https://note.youdao.com/)  
----------------------------2018.11.17--捷特 &安徽