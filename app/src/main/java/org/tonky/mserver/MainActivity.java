package org.tonky.mserver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler.postDelayed(Time, 1); //每隔1s执行
        handler.postDelayed(NetConnect,1);

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private final SensorEventListener mSensorEventListener=new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor. TYPE_LIGHT){
                float temperature=event.values[0];
                TextView lightView = (TextView) findViewById(R.id.CelsiusView);
                lightView.setText(String.valueOf(temperature)+"lux");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };



    public static void updateView(TextView time,TextView date){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date=new Date();
        time.setText(timeFormat.format(current_date));
        date.setText(dateFormat.format(current_date));
    }

    public String isConnected(String ipAddress) {
        try {

                    Runtime runtime = Runtime.getRuntime();
                    Process p = runtime.exec("su");//执行多个命令
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    os.writeBytes("ping -c 1 " + ipAddress + "\n");
                    os.writeBytes("exit\n");
                    os.flush();

                    InputStream is = p.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = is.read()) != -1) {

                        baos.write(i);
                    }

                    String str = baos.toString();
                    TextView connectView = (TextView) findViewById(R.id.ConnectView);

                    if (str.contains("ttl=")) {
                        connectView.setTextColor(Color.parseColor("#3366cc"));
                        return "Connected";
                    }
                    else {
                        connectView.setTextColor(Color.RED);
                        return "Disconnected";
                    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public String getSelfIP() {//获得本机ip地址
        try {
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("ip addr");
            InputStream is = p.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            String str = baos.toString();
            str = str.substring(str.lastIndexOf("inet ")+5);
            str = str.substring(0,str.indexOf("/"));
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }


    final Handler handler = new Handler();//实时刷新
    private Runnable Time = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                final TextView timeView = (TextView) findViewById(R.id.TimeView);
                final TextView dateView = (TextView) findViewById(R.id.DateView);
                updateView(timeView,dateView);//刷新日期时间
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable NetConnect = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 3000);
                final TextView connectView = (TextView) findViewById(R.id.ConnectView);
                final TextView  ipView= (TextView) findViewById(R.id.IpView);

                new Thread(new Runnable() {//在子线程完成操作，在UI线程替换msg
                    public void run() {
                        final String msgConnect = isConnected("www.baidu.com");//baidu.com ip地址
                        final String msgIp = getSelfIP();
                        connectView.post(new Runnable() {
                            @Override
                            public void run() {
                                connectView.setText(msgConnect);
                            }
                        });

                        ipView.post(new Runnable() {
                            @Override
                            public void run() {
                                ipView.setText(msgIp);
                            }
                        });

                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    float x1 = 0;//滚动
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(y1 - y2 > 50) {
                Toast.makeText(MainActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
            } else if(y2 - y1 > 50) {
                Toast.makeText(MainActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
                //这里就可以跳转了
                Intent intent=new Intent(this,SensorViewer.class);  //方法1
                startActivity(intent);
            } else if(x1 - x2 > 50) {
                Toast.makeText(MainActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
            } else if(x2 - x1 > 50) {
                Toast.makeText(MainActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onTouchEvent(event);
    }
}
