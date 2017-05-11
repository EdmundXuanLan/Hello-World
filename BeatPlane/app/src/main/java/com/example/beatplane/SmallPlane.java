package com.example.beatplane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.beatplane.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
/*小型飞机的类*/
public class SmallPlane extends GameObject{
    private Bitmap smallPlane;
    private Bitmap samllPlaneBomb;
    private int blood; 		// 对象的当前血量
    private int bloodVolume;  // 对象的血量
    private int direction;			//移动的方向
    private int DIR_LEFT = 1;
    private int DIR_RIGHT = 2;
    private int interval;			//发射子弹的间隔
    private float leftBorder;
    private float rightBorder;
    private boolean isFire;			//是否允许射击
    private List<GameObject> bullets;//子弹类
    private MyPlane myplane;		//玩家的飞机类
    private float middle_x;
    private float  middle_y;
    SmallPlane(Resources resources,MainActivity mainActivity) {
        super(resources);
        // TODO Auto-generated constructor stub
        initBitmap();
        this.score = 100;
        interval = 1;
        bullets = new ArrayList<GameObject>();
        for(int i = 0;i < 10;i++){
            BossBullet bullet = new BossBullet(resources);
            bullets.add(bullet);
        }
    }
    //初始化数据
    @Override
    public void setScreenWH(float screen_width,float screen_height){
        super.setScreenWH(screen_width, screen_height);
        for(GameObject obj:bullets){
            obj.setScreenWH(screen_width, screen_height);
        }
        leftBorder = -object_width/2;
        rightBorder = screen_width - object_width/2;
        middle_x = object_x + object_width/2;
        middle_y = object_y + object_height/2;
    }
    //设置对象
    public void setPlane(MyPlane myplane){
        this.myplane = myplane;
    }
    //初始化数据
    @Override
    public void initial(int arg0,float arg1,float arg2,int arg3){
        super.initial(arg0,arg1,arg2,arg3);
        isFire = false;
        bloodVolume = 1;
        blood = bloodVolume;
        Random ran = new Random();
        object_x = ran.nextInt((int)(screen_width - object_width));
        this.speed = ran.nextInt(8) + 8 * arg3;
    }
    //初始化图片
    @Override
    public void initBitmap() {
        // TODO Auto-generated method stub
        smallPlane = BitmapFactory.decodeResource(resources, R.drawable.small);
        object_width = smallPlane.getWidth();		//获得每一帧位图的宽
        object_height = smallPlane.getHeight()/3;		//获得每一帧位图的高
    }

    //初始化子弹对象
    public void initButtle(){
        if(isFire){
            if(interval == 1){
                for(GameObject obj:bullets){
                    if(!obj.isAlive()){
                        obj.initial(0,object_x + object_width/2,object_height,0);
                        break;
                    }
                }
            }
            interval++;
            if(interval >= 10){
                interval = 1;
            }
        }
    }

    //绘图函数
    @Override
    public void drawSelf(Canvas canvas) {
        // TODO Auto-generated method stub
        if(isAlive){
            if(!isExplosion){
                int y = (int) (currentFrame * object_height); // 获得当前帧相对于位图的Y坐标
                canvas.save();
                canvas.clipRect(object_x,object_y,object_x + object_width,object_y + object_height);
                canvas.drawBitmap(smallPlane, object_x, object_y,paint);
                canvas.restore();
                logic();
                shoot(canvas);		//射击
               currentFrame++;
               if(currentFrame >= 4){
                  currentFrame = 0;
                 }

            }
            else{
                int y = (int) (currentFrame * object_height); // 获得当前帧相对于位图的Y坐标
                canvas.save();
                canvas.clipRect(object_x,object_y,object_x + object_width,object_y + object_height);
                canvas.drawBitmap(smallPlane, object_x, object_y - y,paint);
                canvas.restore();
                currentFrame++;
                if(currentFrame >= 3){
                    currentFrame = 0;
                    isExplosion = false;
                    isAlive = false;
                    myplane = null;
                }
            }
        }
    }

    //发射子弹
    public boolean shoot(Canvas canvas){
        if(isFire){
            //遍历子弹的对象
            for(GameObject obj:bullets){
                if(obj.isAlive()){
                    obj.drawSelf(canvas);//绘制子弹
                    if(obj.isCollide(myplane)){
                        myplane.setAlive(false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //释放资源
    @Override
    public void release() {
        for(GameObject obj:bullets){
            obj.release();
        }
        if(!smallPlane.isRecycled()){
            smallPlane.recycle();
        }
        if(!samllPlaneBomb.isRecycled()){
            samllPlaneBomb.recycle();
        }
    }
    // 检测碰撞
    @Override
    public boolean isCollide(GameObject obj) {
        return super.isCollide(obj);
    }
    //对象的逻辑函数
    @Override
    public void logic(){
        if (object_y < screen_height) {
            if(!isFire){
                isFire = true;
                speed = 10;
            }
            object_y += speed;
            Random x = new Random();
            if(screen_height!=0)
            {
              object_x = object_x + x.nextInt(20) + (-x.nextInt(20));
            }
        } else {
            isAlive = false;
        }
    }
    //被攻击的逻辑函数
    @Override
    public void attacked(int harm){
        blood -= harm;
        if (blood <= 0) {
            isExplosion = true;
            currentFrame = 0;
        }
    }
}
