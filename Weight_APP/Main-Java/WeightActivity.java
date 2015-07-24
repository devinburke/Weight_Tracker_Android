package com.stocktwt.newapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stocktwt.newapp.app.AppConfig;
import com.stocktwt.newapp.app.AppController;
import com.stocktwt.newapp.helper.SQLiteHandler;
import com.stocktwt.newapp.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class WeightActivity extends Activity {
    private static final String TAG = WeightActivity.class.getSimpleName();
    private TextView inputWeight;
    private TextView inputWeightGoal;
    private TextView nameview;
    private Button next;
    private ProgressDialog pDialog;
    private Button back;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        inputWeight = (TextView) findViewById(R.id.currentweight);
        inputWeightGoal = (TextView) findViewById(R.id.weightgoal);
        nameview = (TextView) findViewById(R.id.TextName);
        next = (Button) findViewById(R.id.bnext);
        back = (Button) findViewById(R.id.back);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        final String userid = user.get("uid");
        nameview.setText(name);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }});
        next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String weight = inputWeight.getText().toString();
                String weightgoal = inputWeightGoal.getText().toString();
                if (weight.trim().length() > 0 && weightgoal.trim().length() > 0) {

                    if (weight.length()>3 || weightgoal.length()>3){
                        Toast.makeText(getApplicationContext(), "Weight Cannot Exceed 999 Pounds", Toast.LENGTH_LONG).show();
                   }
                    else{
                        InsertWeight(weight, weightgoal);
                        Intent intent = new Intent(WeightActivity.this, home.class);
                        startActivity(intent);
                        finish();                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Enter Both Weights", Toast.LENGTH_LONG)
                            .show();
                }}
        });}
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(WeightActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void InsertWeight (final String weight, final String weightgoal){
        String tag_string_reg = "reg_weight";
        pDialog.setMessage("Storing Weights...");
        showDialog();
        StringRequest strReg = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Weight Storing Response: " + response.toString());
                        hideDialog();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Logging error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }})  {
            @Override
            protected Map<String, String> getParams() {
                //Posting weight params to weight url
                Map<String, String> params = new HashMap<String, String>();
                HashMap<String, String> user = db.getUserDetails();
                String userid = user.get("uid");
                params.put("tag", "weight");
                params.put("weight", weight);
                params.put("weightgoal", weightgoal);
                params.put("uid",userid );
                params.put("initial", "yes");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}




