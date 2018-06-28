package com.example.jitheshgugan.myapplication_gps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private LocationManager locManager;
    private LocationListener li;
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    Context cont;
    KeyStore.Entry entry;
    TextView view1;
    public String mDisplay;
    static private double startTime = 0;
    TextView view2;
    TextView textView3;
    TextView view4;
    Button button;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    static private double pacc = 0;
    static private double ptime = System.currentTimeMillis();
    double apti = 0;
    static private double bacc = pacc;
    ArrayList<Double> apacc = new ArrayList<Double>();
    ArrayList<Double> aptime = new ArrayList<Double>();
    ArrayList<Double> apSysTime = new ArrayList<Double>();
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apacc.add(0, (double) 0);
        aptime.add(0, (double) 0);
        apSysTime.add(ptime);

        cont = getApplicationContext();
        view1 = (TextView) findViewById(R.id.view1);
        view4 = (TextView) findViewById(R.id.view4);
        checkForLocationPermission();
        textView3 = (TextView) findViewById(R.id.textView3);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        button = (Button) findViewById(R.id.button);

        SensorEventListener sensorEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }


            public void onSensorChanged(SensorEvent event) {
                // Log.i("Sensor", event.sensor.getName() + " value: " + event.values[0]);
                float mSensorX = 0, mSensorY = 0, mSensorZ = 0;
                double Sam = 0;
                double Sam1 = 0, dis, spe = 0, time = 0;
                int j = 0;
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
                //view1.setText("Acceleration : " + Math.round(acc));

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
                    Log.i("List val", "Speed" + apacc.toString());
                    //  Log.i("List val", "Time" + aptime.toString());

                    view1.setText("Speed :" + Math.round(spe) + "km/hr");
                }
            }
        };


        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }


    private void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location permission necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) cont, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        } else {
            //  mSensorManager.unregisterListener(SensorEventListener SensorEventListener,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            li = new speed();
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, li);


        }
    }

    private void createGeofence(double latitude, double longitude, int radius) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(cont);
        List<Geofence> geofenceList = new ArrayList<>();
        geofenceList.add(new Geofence.Builder()
                .setRequestId("limit_1_fence")
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build());
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Location permission necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) cont, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // MY_PERMISSIONS_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        geofencingClient.addGeofences(builder.build(), getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(cont, "Fence Created Successfully", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(cont, "Fence creation Failed Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceIntent.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public void buttonClicked(View view) {
        createGeofence(13.035233, 80.254267, 30);
    }

    class speed implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {


            Float thespeed = loc.getSpeed();
            textView3.setText("Speed :" + thespeed + "km/hr");
            view4.setText("Location" + loc.getLatitude());
            Log.i("Location Speed", String.valueOf(thespeed) + "Latitude" + loc.getLatitude());
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

    }
}
