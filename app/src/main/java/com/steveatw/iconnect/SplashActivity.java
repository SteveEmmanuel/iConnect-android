package com.steveatw.iconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Intent intent;
    private String url = "http://192.168.0.174:8080/checkapproval";
    private Context mContext;
    private String firebase_token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mContext = getApplicationContext();

        prefs = getSharedPreferences("com.steveatw.iconnect", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // First run
            prefs.edit().putBoolean("firstrun", false).apply();

            intent = new Intent(SplashActivity.this, MainActivity.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            },1000);
        }
        else{
            // Not first run
            // Check if approved
            try{
                prefs = getSharedPreferences("com.steveatw.iconnect", MODE_PRIVATE);
                firebase_token = prefs.getString("firebase_token", null);
                JSONObject customer_detail_json = new JSONObject();
                customer_detail_json.put("firebase_token", firebase_token);

                RequestQueue requestQueue = Volley.newRequestQueue(mContext);

                // Initialize a new JsonObjectRequest instance
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        customer_detail_json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Do something with response
                                // Process the JSON

                                // Get the JSON array
                                if(response.has("error")){
                                    //not approvede
                                    intent = new Intent(SplashActivity.this, PendingApprovalActivity.class);
                                }
                                else{

                                    intent = new Intent(SplashActivity.this, QRCodeDisplayActivity.class);
                                    intent.putExtra("firebase_token", firebase_token);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                // Do something when error occurred

                            }
                        }
                );

                // Add JsonObjectRequest to the RequestQueue
                requestQueue.add(jsonObjectRequest);
            }catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}