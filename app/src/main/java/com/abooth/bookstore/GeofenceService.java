package com.abooth.bookstore;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceService extends IntentService {

    public static final String TAG = "GeofenceService";

    public GeofenceService() {
        super(TAG);
    }

    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            //TODO: Handle Error
        } else {

            int transition = event.getGeofenceTransition();
            //List<Geofence> geofences = event.getTriggeringGeofences();
            //Geofence geofence = geofences.get(0);
            //String requestId = geofence.getRequestId();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d(TAG, "Entering geofence BookStore" );  //ADD CUSTOM LOGIC HERE.  LIKE BUTTON VISIBLE/INVISIBLE
                showToast("Welcome to the MSU Bookstore!  You may check in on the main screen.");
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d(TAG, "Exiting geofence BookStore" );  //ADD CUSTOM LOGIC HERE.  LIKE BUTTON VISIBLE/INVISIBLE
            }
        }
    }
}
