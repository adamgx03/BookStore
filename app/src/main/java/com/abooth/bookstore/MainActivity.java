package com.abooth.bookstore;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainActivity extends Activity {


    public static final String TAG = "MainActivity";
    public static final String GEOFENCE_ID = "BookStore";

    //INSTANCE VARIABLE FOR GOOGLE API CLIENT
    GoogleApiClient googleApiClient = null;

    //BUTTON INITIALIZATION
    private Button button;
    private Button textBooksBTN;
    private Button generalBookBTN;
    private Button giftsBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LINK TO BUTTON
        button = (Button) findViewById(R.id.checkInButton);
        textBooksBTN = (Button) findViewById(R.id.TextBooksButton);
        generalBookBTN = (Button) findViewById(R.id.GeneralBooksButton);
        giftsBTN = (Button) findViewById(R.id.GiftsButton);


        //ON CLICK LISTENER AND TOAST
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Staff will prepare your order", Toast.LENGTH_LONG).show();
            }
        });

        //ON CLICK FOR BUTTON LINKS TO BOOKSTORE
        textBooksBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url1 = "https://www.missouristatebookstore.com/Textbooks.htm";
                Intent x = new Intent(Intent.ACTION_VIEW);
                x.setData(Uri.parse(url1));
                startActivity(x);
            }
        });
        generalBookBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url2 = "https://www.missouristatebookstore.com/GeneralBooks.htm";
                Intent y = new Intent(Intent.ACTION_VIEW);
                y.setData(Uri.parse(url2));
                startActivity(y);
            }
        });
        giftsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url3 = "https://shop.missouristatebookstore.com/c-9-souvenirs-gifts.aspx";
                Intent z = new Intent(Intent.ACTION_VIEW);
                z.setData(Uri.parse(url3));
                startActivity(z);
            }
        });


        //REQUEST FINE AND COARSE LOCATION PERMISSIONS
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);

        //CALL GOOGLE API CLIENT
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(TAG, "Connected to GoogleApiClient");
                startLocationMonitoring(); //******************* FROM CLASS BOARD

                startGeofenceMonitoring();
            }
            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Suspended connection to GoogleApiClient");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "Failed to connect to GoogleApiClient - " + result.getErrorMessage());
            }
        }).build();
    }

    //TO ENSURE GOOGLE PLAY SERVICES IS INSTALLED.  WILL SHOW ERROR MESSAGE IF NOT INSTALLED
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services not available - show dialog to ask user to download it");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d(TAG, "Google Play Services is available - no action is required");
        }
    }

    //API CALLED WHEN APPLICATION IS IN THE FOREGROUND
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();
        googleApiClient.reconnect();
    }

    //API STOPPED WHEN APPLICATION IS IN THE BACKGROUND.
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop called");
        super.onStop();
        stopGeofenceMonitoring();       //StopGeoFence Monitoring
        googleApiClient.disconnect();
    }

    //START LOCATION SERVICES AND LISTENER
    private void startLocationMonitoring() {
        Log.d(TAG, "startLocation called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)         //MILLISECONDS.  APP WILL RECEIVE LOCATION UPDATES AT THIS INTERVAL
                    .setFastestInterval(5000)
                    //.setNumUpdates(5)  //SET NUMBER OF UPDATES.  THIS LINE IS COMMENTED OUT ON PURPOSE.
                    //THIS IS JUST SAYING THAT THE SERVICE WILL BE LISTENING INDEFINITELY
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Location update lat/long " + location.getLatitude() + " " +
                            location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, "Security Exception - " + e.getMessage());
        }
    }

    //START GEOFENCE CALLS
    private void startGeofenceMonitoring() {
        Log.d(TAG, "startMonitoring called");
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(GEOFENCE_ID)
                    .setCircularRegion(37.2006451, -93.278497, 2500) //LAT, LONG, RADIUS
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)    //HOQ QUICKLY APP REACTS TO A GEOFENCE EVENT
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)   // EVENTS TO START FROM GEOFENCE
                    .build();

            //GEOFENCE REQUEST OBJECT
            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence).build();

            //INSTANTIATE THE INTENT GeofenceService.class
            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            this.startService(intent);  //*************COMMENTING THIS OUT STOPS GEOFENCESERVICE CLASS FROM FIRING

            if (!googleApiClient.isConnected()) {
                Log.d(TAG, "GoogleApiClient is not connected");
            } else {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent) //(googleApiClient, geofenceRequest, pendingIntent) ***remove geofencerequest
                        .setResultCallback(new ResultCallback<Status>() {

                            //DETERMINE IF GEOFENCE WAS SUCCESSFUL OR NOT
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Successfully added geofence");
                                } else {
                                    Log.d(TAG, "Failed to add geofence + " + status.getStatus());
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException = " + e.getMessage());
        }
    }

    //WHEN/IF APP STOPS GEOFENCE MONITORING
    private void stopGeofenceMonitoring() {
        Log.d(TAG, "stopMonitoring called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);
    }
}
