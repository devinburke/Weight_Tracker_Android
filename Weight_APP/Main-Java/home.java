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

public class home extends Activity implements View.OnClickListener {

    private static final String TAG = WeightActivity.class.getSimpleName();
    private TextView name;
    private TextView currentweight;
    private TextView goalweight;
    private TextView startweight;
    private Button logout;
    private Button newweight;
    private Button graphbutton;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        name = (TextView) findViewById(R.id.name);
        currentweight = (TextView) findViewById(R.id.currentweight);
        goalweight = (TextView) findViewById(R.id.goalweight);
        startweight = (TextView) findViewById(R.id.startweight);
        logout = (Button) findViewById(R.id.btnLogout);
        newweight = (Button) findViewById(R.id.addweight);
        graphbutton = (Button) findViewById(R.id.graphbutton);

        graphbutton.setOnClickListener(this);
        logout.setOnClickListener(this);
        newweight.setOnClickListener(this);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();
        String uid = user.get("uid");
        String username = user.get("name");
        String default_weights = "";

        name.setText(username);
        currentweight.setText(default_weights);
        goalweight.setText(default_weights);
        getWeight();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogout:
                logoutUser();
                break;
            case R.id.addweight:

                Intent intent = new Intent (getApplicationContext(), WeightNewActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.graphbutton:
                Intent graph = new Intent (getApplicationContext(), graph.class);
                startActivity(graph);
                finish();
                break;
        }
    }
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(home.this, LoginActivity.class);
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

    public void getWeight(){
        String tag_string_reg = "reg_weight";
        pDialog.setMessage("Retreiving Weights...");
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
                                JSONObject user = jObj.getJSONObject("user");
                                String currentweight_json = user.getString("weight");
                                String goalweight_json = user.getString("weightgoal");
                                String startweight_json = user.getString("startweight");
                                currentweight.setText(currentweight_json);
                                goalweight.setText(goalweight_json);
                                startweight.setText(startweight_json);
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
                params.put("tag", "home");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);
    }
}
