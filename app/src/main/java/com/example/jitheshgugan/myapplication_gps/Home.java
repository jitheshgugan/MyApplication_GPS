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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    public class MainActivitygeo
            implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
            ResultCallback<Status> {

        private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
        private static final long GEO_DURATION = 60 * 60 * 1000;
        private static final String GEOFENCE_REQ_ID = "My Geofence";
        private static final float GEOFENCE_RADIUS = 500.0f; // in meters
        private final String TAG = MainActivitygeo.class.getSimpleName();
        // Defined in mili seconds.
        // This number in extremely low, and should be used only for debug
        private final int UPDATE_INTERVAL = 1000;
        private final int FASTEST_INTERVAL = 900;
        private final int GEOFENCE_REQ_CODE = 0;
        private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
        private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";
        Context cont;
        int REQ_PERMISSION = 999;
        private GoogleMap map;
        private GoogleApiClient googleApiClient;
        private Location lastLocation;
        private TextView textLat, textLong;

        // Verify user's response of the permission requested
        private MapFragment mapFragment;
        private LocationRequest locationRequest;
        private Marker locationMarker;
        private Marker geoFenceMarker;
        private PendingIntent geoFencePendingIntent;
        // Draw Geofence circle on GoogleMap
        private Circle geoFenceLimits;

        // Create a Intent send by the notification
        public Intent makeNotificationIntent(Context context, String msg) {
            Intent intent = new Intent(context, MainActivitygeo.class);
            intent.putExtra(NOTIFICATION_MSG, msg);
            return intent;
        }

        protected void onCreate(Bundle savedInstanceState) {

            // initialize GoogleMaps
            initGMaps();

            // create GoogleApiClient
            createGoogleApi();
        }

        // Create GoogleApiClient instance
        private void createGoogleApi() {
            Log.d(TAG, "createGoogleApi()");
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder((Activity) cont)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        protected void onStart() {
            mapFragment.onStart();

            // Call GoogleApiClient connection when starting the Activity
            googleApiClient.connect();
        }

        protected void onStop() {
            mapFragment.onStop();

            // Disconnect GoogleApiClient when stopping Activity
            googleApiClient.disconnect();
        }

        // Check for permission to access Location
        private boolean checkPermissiongeo() {
            Log.d(TAG, "checkPermission()");
            // Ask for permission if it wasn't granted yet
            return (ContextCompat.checkSelfPermission((Activity) cont, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        }

        // Asks for permission
        private void askPermission() {
            Log.d(TAG, "askPermission()");
            ActivityCompat.requestPermissions((Activity) cont, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION);
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            Log.d(TAG, "onRequestPermissionsResult()");
            mapFragment.onRequestPermissionsResult();
            switch (requestCode) {
                case REQ_PERMISSION: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted
                        getLastKnownLocation();

                    } else {
                        // Permission denied
                        permissionsDenied();
                    }
                    break;
                }
            }
        }

        // App cannot work without the permissions
        private void permissionsDenied() {
            Log.w(TAG, "permissionsDenied()");
            // TODO close app and warn user
        }

        // Initialize GoogleMaps
        private void initGMaps() {
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // Callback called when Map is ready
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady()");
            map = googleMap;
            map.setOnMapClickListener(this);
            map.setOnMarkerClickListener(this);
        }

        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "onMapClick(" + latLng + ")");
            markerForGeofence(latLng);
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
            return false;
        }

        // Start location Updates
        private void startLocationUpdates() {
            Log.i(TAG, "startLocationUpdates()");
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged [" + location + "]");
            lastLocation = location;
            writeActualLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        // GoogleApiClient.ConnectionCallbacks connected
        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, "onConnected()");
            getLastKnownLocation();
            recoverGeofenceMarker();
        }

        // GoogleApiClient.ConnectionCallbacks suspended
        @Override
        public void onConnectionSuspended(int i) {
            Log.w(TAG, "onConnectionSuspended()");
        }

        // GoogleApiClient.OnConnectionFailedListener fail
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.w(TAG, "onConnectionFailed()");
        }

        // Get last known location
        private void getLastKnownLocation() {
            Log.d(TAG, "getLastKnownLocation()");
            if (checkPermission()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (lastLocation != null) {
                    Log.i(TAG, "LasKnown location. " +
                            "Long: " + lastLocation.getLongitude() +
                            " | Lat: " + lastLocation.getLatitude());
                    writeLastLocation();
                    startLocationUpdates();
                } else {
                    Log.w(TAG, "No location retrieved yet");
                    startLocationUpdates();
                }
            } else askPermission();
        }

        private void writeActualLocation(Location location) {
            textLat.setText("Lat: " + location.getLatitude());
            textLong.setText("Long: " + location.getLongitude());

            markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        private void writeLastLocation() {
            writeActualLocation(lastLocation);
        }

        private void markerLocation(LatLng latLng) {
            Log.i(TAG, "markerLocation(" + latLng + ")");
            String title = latLng.latitude + ", " + latLng.longitude;
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            if (map != null) {
                if (locationMarker != null)
                    locationMarker.remove();
                locationMarker = map.addMarker(markerOptions);
                float zoom = 14f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                map.animateCamera(cameraUpdate);
            }
        }

        private void markerForGeofence(LatLng latLng) {
            Log.i(TAG, "markerForGeofence(" + latLng + ")");
            String title = latLng.latitude + ", " + latLng.longitude;
            // Define marker options
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(title);
            if (map != null) {
                // Remove last geoFenceMarker
                if (geoFenceMarker != null)
                    geoFenceMarker.remove();

                geoFenceMarker = map.addMarker(markerOptions);

            }
        }

        // Start Geofence creation process
        private void startGeofence() {
            Log.i(TAG, "startGeofence()");
            if (geoFenceMarker != null) {
                Geofence geofence = createGeofence(geoFenceMarker.getPosition(), GEOFENCE_RADIUS);
                GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
                addGeofence(geofenceRequest);
            } else {
                Log.e(TAG, "Geofence marker is null");
            }
        }

        // Create a Geofence
        private Geofence createGeofence(LatLng latLng, float radius) {
            Log.d(TAG, "createGeofence");
            return new Geofence.Builder()
                    .setRequestId(GEOFENCE_REQ_ID)
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setExpirationDuration(GEO_DURATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }

        // Create a Geofence Request
        private GeofencingRequest createGeofenceRequest(Geofence geofence) {
            Log.d(TAG, "createGeofenceRequest");
            return new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();
        }

        private PendingIntent createGeofencePendingIntent() {
            Log.d(TAG, "createGeofencePendingIntent");
            if (geoFencePendingIntent != null)
                return geoFencePendingIntent;

            Intent intent = new Intent(this, GeofenceTransitionService.class);
            return PendingIntent.getService(
                    this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Add the created GeofenceRequest to the device's monitoring list
        private void addGeofence(GeofencingRequest request) {
            Log.d(TAG, "addGeofence");
            if (checkPermission())
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        request,
                        createGeofencePendingIntent()
                ).setResultCallback(this);
        }

        @Override
        public void onResult(@NonNull Status status) {
            Log.i(TAG, "onResult: " + status);
            if (status.isSuccess()) {
                saveGeofence();
                drawGeofence();
            } else {
                // inform about fail
            }
        }

        private void drawGeofence() {
            Log.d(TAG, "drawGeofence()");

            if (geoFenceLimits != null)
                geoFenceLimits.remove();

            CircleOptions circleOptions = new CircleOptions()
                    .center(geoFenceMarker.getPosition())
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150))
                    .radius(GEOFENCE_RADIUS);
            geoFenceLimits = map.addCircle(circleOptions);
        }

        // Saving GeoFence marker with prefs mng
        private void saveGeofence() {
            Log.d(TAG, "saveGeofence()");
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putLong(KEY_GEOFENCE_LAT, Double.doubleToRawLongBits(geoFenceMarker.getPosition().latitude));
            editor.putLong(KEY_GEOFENCE_LON, Double.doubleToRawLongBits(geoFenceMarker.getPosition().longitude));
            editor.apply();
        }

        // Recovering last Geofence marker
        private void recoverGeofenceMarker() {
            Log.d(TAG, "recoverGeofenceMarker");
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

            if (sharedPref.contains(KEY_GEOFENCE_LAT) && sharedPref.contains(KEY_GEOFENCE_LON)) {
                double lat = Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LAT, -1));
                double lon = Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LON, -1));
                LatLng latLng = new LatLng(lat, lon);
                markerForGeofence(latLng);
                drawGeofence();
            }
        }

        // Clear Geofence
        private void clearGeofence() {
            Log.d(TAG, "clearGeofence()");
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    createGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // remove drawing
                        removeGeofenceDraw();
                    }
                }
            });
        }

        private void removeGeofenceDraw() {
            Log.d(TAG, "removeGeofenceDraw()");
            if (geoFenceMarker != null)
                geoFenceMarker.remove();
            if (geoFenceLimits != null)
                geoFenceLimits.remove();
        }


        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }
}




