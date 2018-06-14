package com.example.jitheshgugan.myapplication_gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Home extends AppCompatActivity {

    private LocationManager locManager;
    private LocationListener li;
    public final int MY_PERMISSIONS_REQUEST_LOCATION=2;
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


        mSensorManager.registerListener(sensorEventListener,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);


            }
        //li = new speed();

    protected void functionally() {

        checkCallingOrSelfPermission();
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                Float thespeed = loc.getSpeed();
                Toast.makeText(cont, String.valueOf(thespeed), Toast.LENGTH_LONG).show();
                view1.setText(thespeed.toString());
            }

            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onProviderEnabled(String arg0) {
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            }
        });
    }


    private void checkCallingOrSelfPermission() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ContextCompat.checkSelfPermission(cont,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) cont,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {


                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions((Activity) cont,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }


            }

            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    Float thespeed = loc.getSpeed();
                    Toast.makeText(cont, String.valueOf(thespeed), Toast.LENGTH_LONG).show();
                    view1.setText(thespeed.toString());
                }

                @Override
                public void onProviderDisabled(String arg0) {
                }

                @Override
                public void onProviderEnabled(String arg0) {
                }

                @Override
                public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                }
            });
        }
    }










    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted)
                        Toast.makeText( cont,"Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
                    else {

                        Toast.makeText( cont,"Permission not Granted, Now you can not access location data and camera.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(cont)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
