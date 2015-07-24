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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WeightNewActivity extends Activity implements View.OnClickListener  {
    private static final String TAG = WeightActivity.class.getSimpleName();
    private TextView currentweight;
    private Button back;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weightnew);


        next = (Button) findViewById(R.id.bnext);
        back = (Button) findViewById(R.id.back);
        currentweight = (TextView) findViewById(R.id.newweight);

        back.setOnClickListener(this);
        next.setOnClickListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogout:
                logoutUser();
                break;
            case R.id.back:
                Intent intent = new Intent(WeightNewActivity.this, home.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bnext:
                String addweight = currentweight.getText().toString();
                if (addweight.length()>3){
                    Toast.makeText(getApplication(), "Weights Cannot Exceed 999 Pounds", Toast.LENGTH_LONG).show();
                }else {
                    newweight(addweight);
                }
                break;
        }}
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(WeightNewActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void newweight(final String addweight){
        String tag_string_reg = "reg_weight";
        pDialog.setMessage("Storing Weight...");
        showDialog();
        StringRequest strReg = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Weight Data Response: " + response.toString());
                        hideDialog();
                        try{
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if(!error){
                                Intent intent = new Intent (getApplicationContext(), home.class);
                                startActivity(intent);
                                finish();

                            } else {
                                String errormsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errormsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Logging error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }})  {
            @Override
            protected Map<String, String> getParams() {
                //Posting uid params to retrieve weight data from weight table
                Map<String, String> params = new HashMap<String, String>();
                HashMap<String, String> user = db.getUserDetails();
                String userid = user.get("uid");
                params.put("uid",userid );
                params.put("tag", "newweight");
                params.put("newweight", addweight);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);
    }

}
