package com.steveatw.iconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private Context mContext;
    private String url = "http://steveisredatw.pythonanywhere.com/customer";
    private EditText name, phone_number, email;
    private Button finishBooking;
    private  String firebase_token;
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mContext = getApplication().getApplicationContext();

        finishBooking = findViewById(R.id.finishBookingBtn);
        name = findViewById(R.id.name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);

        finishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Boolean error = false;
                if( TextUtils.isEmpty(name.getText())){
                    name.setError( "Your name is required!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(phone_number.getText())){
                    phone_number.setError( "Phone number is required!" );
                    error = setError();

                }
                if(!Patterns.PHONE.matcher(phone_number.getText()).matches()){
                    phone_number.setError( "Enter Valid Phone Number!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(email.getText())){
                    email.setError( "Email is required!" );
                    error = setError();

                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError( "Enter Valid Email!" );
                    error = setError();
                }
                if(!error){
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("token", "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    // Get new Instance ID token
                                    firebase_token = task.getResult().getToken();
                                    prefs = getSharedPreferences("com.steveatw.iconnect", MODE_PRIVATE);
                                    prefs.edit().putString("firebase_token", firebase_token).apply();
                                    // Log and toast
                                    String msg = "token generated" + firebase_token;
                                    Log.d("token", msg);
                                    //Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject customer_detail_json = new JSONObject();
                                        customer_detail_json.put("name", name.getText().toString());
                                        customer_detail_json.put("phone_number", phone_number.getText().toString());
                                        customer_detail_json.put("email", email.getText().toString());
                                        customer_detail_json.put("firebase_token", firebase_token);

                                        // Initialize a new RequestQueue instance
                                        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

                                        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
                                                R.style.AppTheme_Dark_Dialog);
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("Loading...");
                                        progressDialog.setContentView(R.layout.progress_bar);
                                        progressDialog.getWindow().setGravity(Gravity.CENTER);
                                        progressDialog.show();


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
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(RegistrationActivity.this, PendingApprovalActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // Do something when error occurred
                                                        Snackbar.make(
                                                                view,
                                                                "Error.",
                                                                Snackbar.LENGTH_LONG
                                                        ).show();
                                                    }
                                                }
                                        );
                                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                0,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        // Add JsonObjectRequest to the RequestQueue
                                        requestQueue.add(jsonObjectRequest);

                                        Log.d("output", customer_detail_json.toString(2));
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        });
    }

    public Boolean setError(){
        return true;
    }

}


