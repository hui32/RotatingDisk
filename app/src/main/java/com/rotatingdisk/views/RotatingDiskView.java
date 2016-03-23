package com.rotatingdisk.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.rotatingdisk.R;

/**
 * by liuhui 2015-11-12
 * */
public class RotatingDiskView extends View implements Runnable {
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    //private Thread mThread;
    /** 奖项总数 默认9*/
    private int itemCount;
    /** 单奖项块所占角度*/
    private float itemAngle;
    /** 转盘图片*/
    private Bitmap turnPlateBitmap;

    private int bmWidth;
            //,bmHeight;

    private double vWidth;

    /** 转盘转动位置，角度*/
    private Matrix matrix = new Matrix();
    private float Angel;
    /**
     * 中奖各种计算参数  maxAngel=转动到中奖的角度
     */
    float maxAngel = 0.0f;

    /** 是否已经接收到停止命令*/
    private boolean isStop;
    /** 线程是否存活*/
    private boolean isRunning;
    private int mSpeed = 15;

    private float baseScale;
    public RotatingDiskView(Context context) {
        this(context, null);
        //this.bitMapResource = bitMapResource;
    }
    public RotatingDiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatingDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        /** 获取自定义属性 */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.luck_rotate);
        itemCount = ta.getInt(R.styleable.luck_rotate_lr_count,9);
        /* 转盘图片资源 默认为定义图片*/
        int bitMapResource = ta.getResourceId(R.styleable.luck_rotate_lr_platesrc, R.drawable.turnplate);
        ta.recycle();

        if (itemCount==0||bitMapResource==0) {
            Log.e(TAG, "error: can't find lr_platesrc or lr_count");
            return;
        }

        itemAngle = 360/itemCount;
        //获得图片的宽高
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        turnPlateBitmap = BitmapFactory.decodeResource(getResources(), bitMapResource);
        //注意，宽高必须相等，不然图片转动会有问题
        bmWidth = turnPlateBitmap.getWidth();
        //mThread = new Thread(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        matrix.reset();

        canvas.drawColor(Color.TRANSPARENT);//背景色设置为透明
        //pre  前乘，  post  后乘  ，set  重置
        // 设置转轴位置
        matrix.setTranslate((float) bmWidth / 2, (float) bmWidth / 2);
        // 开始转
        matrix.preRotate(Angel);
        // 转轴还原
        matrix.preTranslate(-(float) bmWidth / 2, -(float) bmWidth / 2);
        // 将位置送到view的中心
        matrix.postTranslate((float) (vWidth - bmWidth) / 2, (float) (vWidth - bmWidth) / 2);

        canvas.drawBitmap(turnPlateBitmap, matrix, null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "widthMeasureSpec->" + widthMeasureSpec + " heightMeasureSpec->" + heightMeasureSpec);

        if (widthMeasureSpec!=heightMeasureSpec){
            Log.e(TAG, "error please ensure height of imageview equal width of imageview");
        }

        //只根据控件的左padding
        double maxWidth = Math.sqrt(2)*bmWidth;
        int defpading =(int)(bmWidth-maxWidth);
        vWidth = Math.sqrt(2)*bmWidth+defpading;
        //因为图片会旋转，设置图片的
        setMeasuredDimension((int) vWidth, (int) vWidth);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                if (maxAngel != 0 && Angel > maxAngel) {
                    maxAngel +=360;
                    Angel +=mSpeed;
                } else {
                    if (maxAngel - Angel > mSpeed){
                        Angel +=mSpeed;
                    }else{
                        Angel = maxAngel;
                        if(!isStop){
                            maxAngel +=360;
                        }else {
                            Angel = maxAngel;
                        }
                    }
                }
                if (Angel==maxAngel){
                    isRunning = false;
                }
                this.postInvalidate();
                Thread.sleep(20);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /** 判断停止按钮是否可用*/
    public boolean isShouldStop(){
        return isStop;
    }

    /** 开始,不会停止*/
    public void startRotation(int defindex){
        isStop = false;
        isRunning = true;
        getNeedRotationAngle(defindex);
        new Thread(this).start();

    }

    /** 开始,在index位置停止*/
    public void startWithStopDefined(int defindex){
        isStop = true;
        isRunning = true;
        getNeedRotationAngle(defindex);
        new Thread(this).start();

    }

    /**
     * 获得停止指令
     *
     * 获取当前的角度，并设置停止角度
     * @param index 从 0 -- (itemCount-1)
     */
    public void setStopIndex(int index){
        isStop = true;
        getNeedRotationAngle(index);
    }

    /** 根据当前旋转角度和奖项需要旋转角度，计算剩余旋转差值*/
    void getNeedRotationAngle(int index){
        //设置奖项被选中时，转盘需要旋转的角度
        float targetAngle = itemAngle*(itemCount-index);
        //取余,获得当前实际角度
        float currentAngle = Angel%360;

        /*Random random = new Random();
        //随机增加一定角度,该值不能大于itemAngle/2
        float addRandom = -20+random.nextFloat()*30;
        targetAngle+=addRandom;*/

        //设置剩余需要旋转的角度
        float difAngle = targetAngle - currentAngle;
        //固定三圈360*3，后在加上当前的角度差（必须要加只少一个360，防止difAngle为负数）
        maxAngel = Angel + 360*3 +difAngle;
    }

    public void setSpeed(int mSpeed){
        this.mSpeed = mSpeed;
    }
}
