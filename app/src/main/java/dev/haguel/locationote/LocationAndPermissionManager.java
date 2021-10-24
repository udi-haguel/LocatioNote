package dev.haguel.locationote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationAndPermissionManager {

    private FusedLocationProviderClient apiClient;
    private Location lastLocation;


    private static LocationAndPermissionManager instance;
    public static LocationAndPermissionManager instance() {
        if (instance == null)
            instance = new LocationAndPermissionManager();
        return instance;
    }
    private LocationAndPermissionManager(){}


    public void init(Context context) {
        apiClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void initLocationWithPermission(Activity activity, OnSuccessListener<Location> listener){
        // Init Last Location
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            refreshLastLocation(listener);
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, DatabaseManager.GEO_LOCATION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public void refreshLastLocation (OnSuccessListener<Location> listener) {
        apiClient.getLastLocation().addOnSuccessListener(listener);
//        apiClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//               lastLocation = location;
//            }
//        });
    }



}
