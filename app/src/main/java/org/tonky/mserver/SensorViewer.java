package org.tonky.mserver;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import java.util.List;

public class SensorViewer extends AppCompatActivity{

    //获取传感器管理对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_viewer);

        TextView msg = (TextView) findViewById(R.id.Msg);
        msg.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mSensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        String tempString ="";
        for (Sensor s:mSensor) {
            tempString += "\n" + "  设备名称：" + s.getName() + "\n" + "  设备版本：" + s.getVersion() + "\n" + "  供应商："
                    + s.getVendor() + "\n";
        }
        msg.setText(""+tempString+mSensor.size());

    }
}
