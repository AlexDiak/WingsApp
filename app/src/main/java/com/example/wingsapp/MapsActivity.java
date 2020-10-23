package com.example.wingsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wingsapp.Models.FoursquareJSON;
import com.example.wingsapp.Models.FoursquareJSONVenueInfo;
import com.example.wingsapp.Models.Venue;
import com.example.wingsapp.Models.VenueInfo;
import com.example.wingsapp.Retrofit.IMyService;
import com.example.wingsapp.Retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private Geocoder geocoder;
    private IMyService iMyService;
    private List<Venue> venuelist = new ArrayList<>();
    private VenueInfo venuetmp;
    private List<Address> addressList;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

        assert service != null;
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {

            Toast.makeText(getApplicationContext(), "GPS is disabled. Please enable it in your phone's settings", Toast.LENGTH_LONG).show();

        }

        setContentView(R.layout.activity_maps);

        getLocationPermission();

        Retrofit retrofitclient = RetrofitClient.getInstance();
        iMyService = retrofitclient.create(IMyService.class);
        geocoder = new Geocoder(this);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        updateLocationUI();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                mMap.clear();
                getPlaces(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
            }

        });

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Get the current location of the device and set the position of the map.
                getDeviceLocation();


            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));

                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));

                                try {
                                    addressList = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);

                                    TextView text1 = (TextView) findViewById(R.id.textView1);
                                    text1.setText(addressList.get(0).getAddressLine(0));

                                    getPlaces(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            LatLng sydney = new LatLng(-33.852, 151.211);
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(sydney, 14));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getPlaces(double latitude, double longitude) {

        Call<FoursquareJSON> call = iMyService.getVenues(
                "JXN3PQZKHB2O4JJN4D0XRCL4M23NWJRLRWFNIZO0YFJX4UJE",
                "P5S3CFCYBXHQRW5IYDNO34INUIH5Q1VRVIS34SR5OGSZCCBV", "202001011", latitude + "," + longitude, "4bf58dd8d48988d117951735", 1000);

        call.enqueue(new Callback<FoursquareJSON>() {


            @Override
            public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                if (response.isSuccessful()) {

                    venuelist = response.body().getResponse().getVenues();

                    Marker mark;

                    for (Venue ven : venuelist) {

                        mark = mMap.addMarker(new MarkerOptions().position(new LatLng(ven.getLocation().getLat(), ven.getLocation().getLng())));

                        mark.setTag(ven.getId());

                        mMap.setOnMarkerClickListener(m -> {

                            getVenueInfo(Objects.requireNonNull(m.getTag()).toString());

                            return true;
                        });
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Quota is exceeded, try again later", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<FoursquareJSON> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "This is an actual network failure...", Toast.LENGTH_LONG).show();

            }

        });

    }

    private void popupwindow(VenueInfo ven) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.custom_info_box, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(5);
        }
        popupWindow.setAnimationStyle(R.style.Animation);

        // show the popup window
        popupWindow.showAtLocation(popupView, Gravity.TOP, 0, Objects.requireNonNull(getSupportActionBar()).getHeight() + 70);

        TextView mytext = (TextView) popupView.findViewById(R.id.tv_title);
        TextView mytext2 = (TextView) popupView.findViewById(R.id.tv_subtitle);
        TextView mytext3 = (TextView) popupView.findViewById(R.id.category);

        ImageView myimgview = (ImageView) popupView.findViewById(R.id.imageView);

        ImageButton bttn1 = (ImageButton) popupView.findViewById(R.id.imageButton2);

        mytext.setText(ven.getName());

        if (ven.getLocation() != null)
            mytext2.setText(ven.getLocation().getAddress());

        mytext3.setText(ven.getCategories().get(0).getName());

        if (ven.getBestPhoto() != null) {

            Picasso.get().load(ven.getBestPhoto().getPrefix() + "500x500" + ven.getBestPhoto().getSuffix()).into(myimgview);

        }

        bttn1.setOnClickListener(v -> popupWindow.dismiss());

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Intent myIntent = new Intent(MapsActivity.this, VenueActivity.class);
                if (ven.getDescription() != null)
                    myIntent.putExtra("description", ven.getDescription());
                else
                    myIntent.putExtra("description", "No description available.");

                MapsActivity.this.startActivity(myIntent);

                return false;
            }

        });

    }

    private void getVenueInfo(String id) {

        Call<FoursquareJSONVenueInfo> call = iMyService.getVenueInfo(id, "JXN3PQZKHB2O4JJN4D0XRCL4M23NWJRLRWFNIZO0YFJX4UJE",
                "P5S3CFCYBXHQRW5IYDNO34INUIH5Q1VRVIS34SR5OGSZCCBV", "202001011");

        call.enqueue(new Callback<FoursquareJSONVenueInfo>() {


            @Override
            public void onResponse(Call<FoursquareJSONVenueInfo> call, Response<FoursquareJSONVenueInfo> response) {

                if (response.isSuccessful()) {

                    venuetmp = response.body().getResponse().getVenueInfo();

                    popupwindow(venuetmp);

                } else {

                    Toast.makeText(getApplicationContext(), "Quota is exceeded, try again later", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<FoursquareJSONVenueInfo> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "This is an actual network failure...", Toast.LENGTH_LONG).show();

            }

        });

    }

}