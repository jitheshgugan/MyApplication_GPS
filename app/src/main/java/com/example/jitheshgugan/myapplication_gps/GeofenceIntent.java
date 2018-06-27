package com.example.jitheshgugan.myapplication_gps;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceIntent extends IntentService {

    private final String TAG = GeofenceIntent.class.getSimpleName();
    private GoogleApiAvailability GeofenceErrorMessages;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceIntent(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            // String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.e(TAG, "errorMessage");
            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {


            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(this,
                    geofenceTransition,
                    triggeringGeofences
            );

            Log.i(TAG, "geofenceTransitionDetails");
        } else {
            // Log the error.
            Log.e(TAG, "Doing Nothing");
        }
    }

    private String getGeofenceTransitionDetails(GeofenceIntent geofenceIntent,
                                                int geofenceTransition, List<Geofence> triggeringGeofences) {
        return null;
    }


}