package com.jby.hanwei;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.jby.hanwei.main.HomeActivity;
import com.jby.hanwei.other.MySingleton;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jby.hanwei.url.UrlManager.AttendancePath;
import static com.jby.hanwei.url.UrlManager.locationPath;


public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Button attendanceButton;
    private ProgressBar attendanceProgressBar;
    //    map setting
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //your current location
    private Location currentLocation;
    //your company location
    private LatLng companyLocation;
    //location permission purpose
    public static int LOCATION_REQUEST = 500;
    //avoid double load company location
    private boolean load = false;
    private Handler handler;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        attendanceButton = findViewById(R.id.activity_attendance_check_in_button);
        attendanceProgressBar = findViewById(R.id.activity_attendance_check_progress_bar);
        handler = new Handler();
    }

    private void objectSetting() {
        attendanceButton.setOnClickListener(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Attendance");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkingStatus();
                    }
                },200);
                buildGoogleApiClient();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_attendance_check_in_button:
                if(attendanceButton.getText().toString().equals("Check In"))
                    alearDialog("Check In", "Are you that you want to check in now?");
                else
                    alearDialog("Check Out", "Are you that you want to check out now?");
                break;
        }
    }

    //<-----------------------------------Location setting-------------------------------------------------------->

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        if (mGoogleApiClient != null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(!load){
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() { getCompanyLocation();
                                        }
                                    },200);
                load = true;
            }
            checkingDistance();
        }
    }

    private void checkingDistance() {
        Float distance;
        if(companyLocation != null){
            distance = getDistance();
            if(distance < 1 && attendanceButton.getText().toString().equals("Check In") || attendanceButton.getText().toString().equals("Check Out"))
                attendanceButton.setEnabled(true);
            else
                attendanceButton.setEnabled(false);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            getCompanyLocation();
        }
    }

    public void getCompanyLocation() {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + locationPath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("haha", "haha: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            String longitude = jsonObject.getString("longitude");
                            String latitude = jsonObject.getString("latitude");
                            companyLocation = getLatLongFromString(longitude, latitude);
                            checkingDistance();
                            break;
                        default:
                            Toast.makeText(AttendanceActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AttendanceActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
        attendanceProgressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) buildGoogleApiClient();
        else checkLocationPermission();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission. ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission. ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    //convert String pick up into LatLong
    private LatLng getLatLongFromString(String slongitude, String slatidude){
        double latitude = Double.parseDouble(slatidude);
        double longitude = Double.parseDouble(slongitude);
        return new LatLng(latitude, longitude);
    }

    public Float getDistance(){
        Location loc1 = new Location("A");
        loc1.setLatitude(currentLocation.getLatitude());
        loc1.setLongitude(currentLocation.getLongitude());
        Location loc2 = new Location("B");
        loc2.setLatitude(companyLocation.latitude);
        loc2.setLongitude(companyLocation.longitude);
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters / 1609.34f;
    }

    //<-----------------------------------location and  GPS setting-------------------------------------------------------->
    /*---------------------------------------------------check in check out setting----------------------------------------*/

    public void checkingStatus() {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + AttendancePath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("haha", "haha: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            setButtonStatus(true, "Check In");
                            break;
                        case "2":
                            setButtonStatus(true, "Check Out");
                            break;
                        case "3":
                            setButtonStatus(false, "Your attendance was taken");
                            break;
                        default:
                            Toast.makeText(AttendanceActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AttendanceActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("status", "1");
                params.put("user_id", SharedPreferenceManager.getUserID(AttendanceActivity.this));
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    public void takeAttendance() {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + AttendancePath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("haha", "haha: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            if(attendanceButton.getText().toString().equals("Check In")){
                                setButtonStatus(true, "Check Out");
                                Toast.makeText(AttendanceActivity.this, "Check In Successful!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                setButtonStatus(false, "Attendance Taken");
                                Toast.makeText(AttendanceActivity.this, "Check Out Successful!", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            Toast.makeText(AttendanceActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AttendanceActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(attendanceButton.getText().toString().equals("Check In"))
                    params.put("check_in", "1");
                else
                    params.put("check_out", "1");

                params.put("user_id", SharedPreferenceManager.getUserID(AttendanceActivity.this));
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    public void setButtonStatus(boolean enable, String text){
        attendanceButton.setEnabled(enable);
        attendanceButton.setText(text);
    }

    public void alearDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                takeAttendance();
                            }
                        },200);
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
