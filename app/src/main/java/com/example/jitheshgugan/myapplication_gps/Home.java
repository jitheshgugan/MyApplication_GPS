package com.example.jitheshgugan.myapplication_gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private LocationManager locManager;
    private LocationListener li;
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    Context cont;
    TextView view1;
    public String mDisplay;
    static private double startTime = 0;
    TextView view2;
    TextView textView3;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    static private double pacc = 0;
    static private double ptime = System.currentTimeMillis();
    double apti = 0;
    static private double bacc = pacc;
    ArrayList<Double> apacc = new ArrayList<Double>();
    ArrayList<Double> aptime = new ArrayList<Double>();
    ArrayList<Double> apSysTime = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apacc.add(0, (double) 0);
        aptime.add(0, (double) 0);
        apSysTime.add(ptime);

        cont = getApplicationContext();
        view1 = (TextView) findViewById(R.id.view1);

        textView3 = (TextView) findViewById(R.id.textView3);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        SensorEventListener sensorEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @SuppressLint("SetTextI18n")
            public void onSensorChanged(SensorEvent event) {
                // Log.i("Sensor", event.sensor.getName() + " value: " + event.values[0]);
                float mSensorX = 0, mSensorY = 0, mSensorZ = 0;
                double Sam = 0;
                double Sam1 = 0, dis, spe = 0, time = 0;
                int j = 0;
                if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    mSensorZ = event.values[2];
                    Sam = mSensorX;
                    /*{
                        if (Sam < mSensorY) {
                            Sam = mSensorY;
                        } else if (Sam < mSensorZ) {
                            Sam = mSensorZ;
                        }
                    }
                    {
                        if (mSensorX == Sam) {
                            mSensorX = 0;
                        } else if (mSensorY == Sam) {
                            mSensorY = 0;
                        } else if (mSensorZ == Sam) {
                            mSensorZ = 0;
                        }
                    }*/
                }

                double acc = (Math.sqrt((mSensorX * mSensorX) + (mSensorY * mSensorY) + (mSensorZ * mSensorZ)));
                view1.setText("Acceleration : " + Math.round(acc));

                {

                    double measureTime = System.currentTimeMillis();

                    time = measureTime - ptime;

                    apacc.add(acc);
                    aptime.add(time);
                    apSysTime.add(measureTime);

                    Double accSum = Double.valueOf(0);

                    for (Double accEle : apacc) {
                        accSum += accEle;
                    }

                    Double timeSum = Double.valueOf(0);

                    for (Double timeEle : aptime) {
                        timeSum += timeEle;
                    }

                    spe = accSum * 3600 / timeSum;
                    if (aptime.size() >= 10) {
                        apacc.remove(0);
                        aptime.remove(0);
                        apti = aptime.get(0);
                        List<Double> tempTime = new ArrayList<>();
                        for (Double apt : aptime) {
                            apt -= apti;
                            tempTime.add(apt);
                        }
                        aptime.clear();
                        aptime.addAll(tempTime);
                        apSysTime.remove(0);
                        ptime = apSysTime.get(0);
                    }


                    /*Upload data to server*/


                    //view2.setText("Speed :" + Math.round(pspe) + "km/hr");
                    //Log.i("List val", "Speed"+apacc.toString());
                    Log.i("List val", "Time" + aptime.toString());

                    textView3.setText("Speed :" + Math.round(spe) + "km/hr");
                }
            }
        };


        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }



    private void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location permission necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, this, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_CAMERA is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

        }
    }
}
