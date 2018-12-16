package com.example.sohel.mapmarker;

import android.app.usage.UsageEvents;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MarkerActivity";
    public GoogleMap mGoogleMap;
    private EditText mSearch;
    private ArrayList<LatLng> platlngs = new ArrayList<>();
    private static final float DEFAULT_ZOOM = 5f;
    static int i = 0;
    static int j = 0;
    ArrayList<Float> dis = new ArrayList<>();
    String[] str = new String[]{"A","B","C","D"};

    int[] intArray = new int[] {3645,2620,2263,2526};

    LatLng initCamera = new LatLng(43.6532,79.3832);

    LatLng tPoint;

    float diskms = 0.2f;
    //float minDis = distBetweenPointAndLine(platlngs.get(0).latitude,platlngs.get(0).longitude,platlngs.get(1).latitude,platlngs.get(1).longitude,tPoint.latitude,tPoint.longitude);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        mSearch = (EditText)findViewById(R.id.input);
        initMap();

    }



    private void init(){
        Log.d(TAG,"init : initializing");
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent KeyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || KeyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || KeyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                        geoLocate();
                }
                return false;
            }
        });

    }
    private void geoLocate(){
        Log.d(TAG,"geoLocate : geoLocating");

        String searchString = mSearch.getText().toString();
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch.setText(null);
            }
        });

            Geocoder geocoder = new Geocoder(MarkerActivity.this);
            List<Address> list = new ArrayList<>();

                try {
                    list = geocoder.getFromLocationName(searchString, 1);
                } catch (IOException e) {
                    Log.d(TAG, "geoLocate : IOException" + e.getMessage());
                }
                    Address address = list.get(0);
                    Log.d(TAG, "geoLocate : Found a location" + address.toString());
                    //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
                    placemarker(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0)+"-"+str[i]);
                    platlngs.add(new LatLng(address.getLatitude(), address.getLongitude()));
                    Log.d(TAG,"LalLng Object"+platlngs.get(0));
                    i++;
                    if(i == 4){
                        mSearch.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Four Cities Allowed", Toast.LENGTH_SHORT).show();
                        placePolygon();
                    }
    }
    private void initMap(){
        Log.d(TAG,"initMap : initializing Map");
        SupportMapFragment mMapFragmentMarker = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapMarker);
        mMapFragmentMarker.getMapAsync(MarkerActivity.this);
    }

    private void placePolygon(){
        PolygonOptions poly = new PolygonOptions()
                .add(platlngs.get(0),
                        platlngs.get(1),
                        platlngs.get(2),
                        platlngs.get(3),
                        platlngs.get(0));

        // Get back the mutable Polygon
        Polygon polygon = mGoogleMap.addPolygon(poly);
        //polygon.setFillColor(Color.GREEN);
        polygon.setFillColor(0x35008000);
        polygon.setStrokeColor(Color.RED);
        polygon.setClickable(true);
        distance();
        mGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Toast.makeText(MarkerActivity.this, "Total Distance A-B-C-D :"+diskms/1000+"Kms", Toast.LENGTH_SHORT).show();
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (j == 0){
                    Toast.makeText(MarkerActivity.this, "Distance is:"+intArray[0]+"Kms", Toast.LENGTH_SHORT).show();
                    j++;
                }else if(j == 1){
                    Toast.makeText(MarkerActivity.this, "Distance is:"+intArray[1]+"Kms", Toast.LENGTH_SHORT).show();
                    j++;
                }else if(j == 2){
                    Toast.makeText(MarkerActivity.this, "Distance is:"+intArray[2]+"Kms", Toast.LENGTH_SHORT).show();
                    j++;
                }else if(j == 3){
                    Toast.makeText(MarkerActivity.this, "Distance is:"+intArray[3]+"Kms", Toast.LENGTH_SHORT).show();
                    j++;
                }else {
                    Toast.makeText(MarkerActivity.this, "Distance is: 2178 Kms", Toast.LENGTH_SHORT).show();
                }

            }
        });
        
    }

    private void distance(){
        Location loc1 = new Location("");
        loc1.setLatitude(platlngs.get(0).latitude);
        loc1.setLongitude(platlngs.get(0).longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(platlngs.get(1).latitude);
        loc2.setLongitude(platlngs.get(1).longitude);

        Location loc3 = new Location("");
        loc3.setLatitude(platlngs.get(2).latitude);
        loc3.setLongitude(platlngs.get(2).longitude);

        Location loc4 = new Location("");
        loc4.setLatitude(platlngs.get(3).latitude);
        loc4.setLongitude(platlngs.get(3).longitude);


        dis.add(loc1.distanceTo(loc2));
        dis.add(loc2.distanceTo(loc3));
        dis.add(loc3.distanceTo(loc4));
        dis.add(loc4.distanceTo(loc1));

       for(Float f: dis){
           diskms += f;
       }
    }

    private void placemarker(LatLng latLng,String title) {
        MarkerOptions marker = new MarkerOptions().position(latLng).title(title);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mGoogleMap.addMarker(marker);
        if (i == 0) {
            Toast.makeText(this, "Three Cities Left", Toast.LENGTH_SHORT).show();
        }else if(i == 1){
            Toast.makeText(this, "Two Cities Left", Toast.LENGTH_SHORT).show();
        }else if(i == 2){
            Toast.makeText(this, "One Cities Left", Toast.LENGTH_SHORT).show();
        }
    }
    private void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG,"moveCamera : lat :"+latLng.latitude + "lng : "+latLng.longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions marker = new MarkerOptions().position(latLng).title(title);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mGoogleMap.addMarker(marker);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onMapReady : Map is ready");
        mGoogleMap = googleMap;
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(initCamera));
        init();


    }
}
