package com.jby.hanwei.leave.leaveList;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jby.hanwei.R;
import com.jby.hanwei.leave.LeaveActivity;
import com.jby.hanwei.other.MySingleton;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jby.hanwei.url.UrlManager.Leave;

public class LeaveListActivity extends AppCompatActivity implements OnItemClickListener {
    private Toolbar toolbar;
    private TextView toolbarApplyLeave;
    private ListView listView;
    private RelativeLayout notFound;
    private LeaveListActivityAdapter leaveListActivityAdapter;
    private ArrayList<LeaveListObject> leaveListObjectArrayList;
    private ProgressBar progressBar;
    private Handler handler;
    public static final int UPDATE_LIST = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_list);
        objectInitialize();
        objectSetting();
    }
    
    private void objectInitialize() {
        toolbar = findViewById(R.id.toolbar);
        toolbarApplyLeave = findViewById(R.id.apply_list);
        listView = findViewById(R.id.activity_leave_list_list_view);
        progressBar = findViewById(R.id.activity_leave_list_progress_bar);
        notFound = findViewById(R.id.activity_leave_list_not_found);
        
        leaveListObjectArrayList = new ArrayList<>();
        leaveListActivityAdapter = new LeaveListActivityAdapter(this, leaveListObjectArrayList);
        handler = new Handler();
    }

    private void objectSetting() {
        toolbarApplyLeave.setVisibility(View.VISIBLE);
        //setup action bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Leave History");

        listView.setAdapter(leaveListActivityAdapter);
        listView.setOnItemClickListener(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchAllLeave();
            }
        },200);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        toolbarApplyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LeaveListActivity.this, LeaveActivity.class), UPDATE_LIST);
            }
        });
    }

    public void fetchAllLeave() {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + Leave, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("haha", "haha: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            JSONArray jsonArray = jsonObject.getJSONArray("value");
                            for(int i = 0 ; i < jsonArray.length(); i++){
                                leaveListObjectArrayList.add(new LeaveListObject(
                                        jsonArray.getJSONObject(i).getString("leave_id"),
                                        jsonArray.getJSONObject(i).getString("status"),
                                        jsonArray.getJSONObject(i).getString("date_from"),
                                        jsonArray.getJSONObject(i).getString("date_to"),
                                        jsonArray.getJSONObject(i).getString("type"),
                                        jsonArray.getJSONObject(i).getString("reason"),
                                        jsonArray.getJSONObject(i).getString("days")
                                ));
                            }
                            break;
                        case "4":
                            Toast.makeText(LeaveListActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                    setUpView();
                    leaveListActivityAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LeaveListActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("getLeave", "1");
                params.put("user_id", SharedPreferenceManager.getUserID(LeaveListActivity.this));
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void setUpView() {
        if (leaveListObjectArrayList.size() > 0){
            listView.setVisibility(View.VISIBLE);
            notFound.setVisibility(View.GONE);
        }
        else{
            listView.setVisibility(View.GONE);
            notFound.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(LeaveListActivity.this, EditLeaveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("reason", leaveListObjectArrayList.get(i).getReason());
        bundle.putString("from_date", leaveListObjectArrayList.get(i).getFromDate());
        bundle.putString("to_date", leaveListObjectArrayList.get(i).getToDate());
        bundle.putString("leave_id", leaveListObjectArrayList.get(i).getId());
        bundle.putString("type", leaveListObjectArrayList.get(i).getType());
        bundle.putString("status", leaveListObjectArrayList.get(i).getStatus());
        intent.putExtras(bundle);
        startActivityForResult(intent, UPDATE_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == UPDATE_LIST){
            leaveListObjectArrayList.clear();
            fetchAllLeave();
        }
    }
}
