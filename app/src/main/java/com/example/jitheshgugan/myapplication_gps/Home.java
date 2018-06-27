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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

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
        view4 = (TextView) findViewById(R.id.view4);
        checkForLocationPermission();
        textView3 = (TextView) findViewById(R.id.textView3);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


        SensorEventListener sensorEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }


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
                    //Log.i("List val", "Speed"+apacc.toString());
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
            //   mSensorManager.unregisterListener(SensorEventListener,mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            li = new speed();
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, li);


        }
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


    /*public static class mGeofenceList extends Activity implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        // Internal List of Geofence objects. In a real app, these might be provided by an API based on
        // locations within the user's proximity.
        List<Geofence> mGeofenceList;

        // These will store hard-coded geofences in this sample app.
        private SimpleGeofence mAndroidBuildingGeofence;
        private SimpleGeofence mYerbaBuenaGeofence;

        // Persistent storage for geofences.
        private SimpleGeofenceStore mGeofenceStorage;

        private LocationServices mLocationService;
        // Stores the PendingIntent used to request geofence monitoring.
        private PendingIntent mGeofenceRequestIntent;
        private GoogleApiClient mApiClient;
        private REQUEST_TYPE mRequestType;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Rather than displayng this activity, simply display a toast indicating that the geofence
            // service is being created. This should happen in less than a second.
            if (!isGooglePlayServicesAvailable()) {
                Log.e(Tag, "Google Play services unavailable.");
                finish();
                return;
            }

            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mApiClient.connect();

            // Instantiate a new geofence storage area.
            mGeofenceStorage = new SimpleGeofenceStore(this);
            // Instantiate the current List of geofences.
            mGeofenceList = new ArrayList<Geofence>();
            createGeofences();
        }

        *//**
         * In this sample, the geofences are predetermined and are hard-coded here. A real app might
         * dynamically create geofences based on the user's location.
     *//*
        public void createGeofences() {
            // Create internal "flattened" objects containing the geofence data.
            mAndroidBuildingGeofence = new SimpleGeofence(ANDROID_BUILDING_ID, ANDROID_BUILDING_LATITUDE, ANDROID_BUILDING_LONGITUDE, ANDROID_BUILDING_RADIUS_METERS, GEOFENCE_EXPIRATION_TIME, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
            mYerbaBuenaGeofence = new SimpleGeofence(YERBA_BUENA_ID, YERBA_BUENA_LATITUDE, YERBA_BUENA_LONGITUDE, YERBA_BUENA_RADIUS_METERS, GEOFENCE_EXPIRATION_TIME, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

            // Store these flat versions in SharedPreferences and add them to the geofence list.
            mGeofenceStorage.setGeofence(ANDROID_BUILDING_ID, mAndroidBuildingGeofence);
            mGeofenceStorage.setGeofence(YERBA_BUENA_ID, mYerbaBuenaGeofence);
            mGeofenceList.add(mAndroidBuildingGeofence.toGeofence());
            mGeofenceList.add(mYerbaBuenaGeofence.toGeofence());
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // If the error has a resolution, start a Google Play services activity to resolve it.
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult((Activity) this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("Available", "Exception while resolving connection error.", e);
                }
            } else {
                int errorCode = connectionResult.getErrorCode();
                Log.e(" Available", "Connection to Google Play services failed with error code " + errorCode);
            }
        }

        *//**
         * Once the connection is available, send a request to add the Geofences.
     *//*
        @Override
        public void onConnected(Bundle connectionHint) {
            // Get the PendingIntent for the geofence monitoring request.
            // Send a request to add the current geofences.
            mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofenceList, mGeofenceRequestIntent);
            // Toast.makeText(this, getString(R.string.start_geofence_service), Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (null != mGeofenceRequestIntent) {
                LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
            }
        }

        *//**
         * Checks if Google Play services is available.
         *
         * @return true if it is.
     *//*
        @SuppressLint({"LogTagMismatch", "LongLogTag"})
        private boolean isGooglePlayServicesAvailable() {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (ConnectionResult.SUCCESS == resultCode) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d("Not Available", "Google Play services is available.");
                }
                return true;
            } else {
                Log.e("Google Play services is unavailable.", "Not Available");
                return false;
            }
        }

        */

    /**
         * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
         * transition occurs.
     *//*
        private PendingIntent getGeofenceTransitionPendingIntent() {
            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Defines the allowable request types (in this example, we only add geofences).
        private enum REQUEST_TYPE {
            ADD
        }

    }*/
    private void createGeofence(Location location, int radius) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(cont);
        List<Geofence> geofenceList = new ArrayList<>();
        geofenceList.add(new Geofence.Builder()
                .setRequestId("limit_1_fence")
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius).build());

    }

}
