package com.example.jitheshgugan.myapplication_gps;

import android.Manifest;
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
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Home extends AppCompatActivity {

    private LocationManager locManager;
    private LocationListener li;
    public final int MY_PERMISSIONS_REQUEST_LOCATION=2;
    Context cont;
    TextView view1;

    public Display mDisplay;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cont = getApplicationContext();
        view1 = (TextView) findViewById(R.id.view1);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                //Right in here is where you put code to read the current sensor values and
                //update any views you might have that are displaying the sensor information
                //You'd get accelerometer values like this:
                Log.i("Sensor", event.sensor.getName() + " value: " + event.values[0]);
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float mSensorX = 0, mSensorY = 0;
                    switch (mDisplay.getRotation()) {
                        case Surface.ROTATION_0:
                            mSensorX = event.values[0];
                            mSensorY = event.values[1];
                            break;
                        case Surface.ROTATION_90:
                            mSensorX = -event.values[1];
                            mSensorY = event.values[0];
                            break;
                        case Surface.ROTATION_180:
                            mSensorX = -event.values[0];
                            mSensorY = -event.values[1];
                            break;
                        case Surface.ROTATION_270:
                            mSensorX = event.values[1];
                            mSensorY = -event.values[0];
                    }
                    view1.setText("X: " + event.values[0] + "\nY: " + event.values[1]);
                }
            }
        };
        mSensorManager.registerListener(sensorEventListener,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        Button functionally;
            functionally = (Button)findViewById(R.id.button1);

            // CALL FUNCTION ON BUTTON CLICK METHOD.
            functionally.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    functionally();
                }
            });

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
