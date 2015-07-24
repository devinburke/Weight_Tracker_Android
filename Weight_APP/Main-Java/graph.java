package com.stocktwt.newapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.stocktwt.newapp.app.AppConfig;
import com.stocktwt.newapp.app.AppController;
import com.stocktwt.newapp.helper.SQLiteHandler;
import com.stocktwt.newapp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william on 7/22/2015.
 */
public class graph extends Activity implements View.OnClickListener {
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private GraphView graph1;
    private LineGraphSeries<DataPoint> mSeries1;
    private Button back;
    private VerticalTextView weightlabel;
    private static final String TAG = WeightActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weightgraph);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        back = (Button) findViewById(R.id.back);
        weightlabel = (VerticalTextView) findViewById(R.id.weightlabel);

        back.setOnClickListener(this);
        showgraph();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:

                Intent intent = new Intent (getApplicationContext(), home.class);
                startActivity(intent);
                finish();

                break;
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void showgraph(){
        String tag_string_reg = "reg_weight";
        pDialog.setMessage("Retreiving Graph Data...");
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

                            graph1 = (GraphView) findViewById(R.id.graph);
                                mSeries1 = new LineGraphSeries<DataPoint>(new DataPoint[]{

                                });

                            Integer count = jObj.getInt("row");

                                for (Integer i=1; i<count+1; i++){
                                    String day = i.toString();
                                    Integer weight = jObj.getInt(day);
                                    mSeries1.appendData(new DataPoint(i,weight), true, 120);

                                }
                                mSeries1.setThickness(8);
                                mSeries1.setDrawDataPoints(true);
                                mSeries1.setDataPointsRadius(5);
                                mSeries1.setTitle("weight");
                                mSeries1.setBackgroundColor(Color.BLACK);
                                graph1.addSeries(mSeries1);

                            }
                            else {
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
                params.put("tag", "graph");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReg, tag_string_reg);
    }












    }


