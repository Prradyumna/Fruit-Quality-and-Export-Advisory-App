package com.example.fruit_quality;

import android.annotation.SuppressLint;
import android.content.Intent;


import android.content.pm.PackageManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class UserDashbord extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    String Address;
    String City;
    Button btn;
    TextView txtweather;
    String username;
    String output;
    String cityname;
    DecimalFormat df = new DecimalFormat("#.##");
   ImageView btncreate,btnshow,btnfruit,btnquality;

    ImageView imageview;

    private  final String url= "https://api.openweathermap.org/data/2.5/weather";
    private final String appid="344e1e820e1290966af44de92ac71502";


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashbord);

        txtweather = findViewById(R.id.txtweather);

        btncreate = findViewById(R.id.adddriver);
        btnfruit = findViewById(R.id.showcom);
        btnquality = findViewById(R.id.showfeedback);
        btnshow = findViewById(R.id.tips);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Toast.makeText(getApplicationContext(),"Location",Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location !=null)
                            {
                                Geocoder geocoder = new Geocoder(UserDashbord.this, Locale.getDefault());
                                List<android.location.Address> addressList = null;
                                try {
                                    addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


                                    assert addressList != null;
                                    //txt.setText(addressList.get(0).getAddressLine(0));
                                    City = addressList.get(0).getLocality();
                                    Toast.makeText(getApplication(), City,Toast.LENGTH_SHORT).show();

                                   getweather();


                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        }
                    });
        }else {

            askpermission();

        }

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), CreateProfile.class);
                startActivity(intent);
            }
        });
        btnfruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Fruit_Qualification.class);
                startActivity(intent);
            }
        });
        btnquality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), quality_classification.class);
                startActivity(intent);
            }
        });

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Showprofile.class);
                startActivity(intent);
            }
        });


    }

    private void askpermission() {

        ActivityCompat.requestPermissions(UserDashbord.this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION
        }, 100);
    }

    private void getweather() {

       String tempurl ="https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid=344e1e820e1290966af44de92ac71502";

       // String tempurl ="https://api.openweathermap.org/data/2.5/weather?q="+"mumbai"+"&appid=344e1e820e1290966af44de92ac71502";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
//                                Log.d("response",response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("weather");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        String Des = jsonObject1.getString("description");
                        JSONObject jsonObject2 = jsonObject.getJSONObject("main");
                        double temp = jsonObject2.getDouble("temp") - 273.15;
                        cityname = jsonObject.getString("name");
                        output =   df.format(temp) + "°C" +"\n" + Des;
                        System.out.println(output);
                        System.out.println(cityname);

                        txtweather.setText(output);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(UserDashbord.this, "response is null", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserDashbord.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
}