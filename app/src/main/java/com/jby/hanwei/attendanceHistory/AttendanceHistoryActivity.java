package com.jby.hanwei.attendanceHistory;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jby.hanwei.R;
import com.jby.hanwei.other.MySingleton;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.jby.hanwei.url.UrlManager.AttendancePath;

public class AttendanceHistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private Spinner monthSpinner, yearSpinner;
    private ExpandableListView attendanceHistoryActivityListView;
    private ProgressBar attendanceHistoryActivityProgressBar;
    private RelativeLayout attendanceHistoryActivityNotFound;

    private AttendanceHistoryExpandableAdapter attendanceHistoryExpandableAdapter;
    private ArrayList<AttendanceHistoryGroupObject> attendanceHistoryGroupObjectArrayList;
    private Handler handler;
    //sorting
    private String selectedMonth, selectedYear;
    private boolean firstLoad = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        toolbar = findViewById(R.id.toolbar);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);

        attendanceHistoryActivityListView = findViewById(R.id.activity_attendance_history_list_view);
        attendanceHistoryActivityProgressBar = findViewById(R.id.activity_attendance_history_progress_bar);
        attendanceHistoryActivityNotFound = findViewById(R.id.activity_attendance_history_not_found);

        attendanceHistoryGroupObjectArrayList = new ArrayList<>();
        attendanceHistoryExpandableAdapter = new AttendanceHistoryExpandableAdapter(this, attendanceHistoryGroupObjectArrayList);
        handler = new Handler();
    }

    private void objectSetting() {
        monthSpinner.setVisibility(View.VISIBLE);
        yearSpinner.setVisibility(View.VISIBLE);
        monthSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);

        attendanceHistoryActivityListView.setAdapter(attendanceHistoryExpandableAdapter);
        //setup action bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Attendance");

        setUpMonthSpinner();
        setUpYearSpinner();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAttendanceHistory();
            }
        },200);
    }

    public void getAttendanceHistory() {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + AttendancePath, new Response.Listener<String>() {
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
                                 attendanceHistoryGroupObjectArrayList.add(new AttendanceHistoryGroupObject(
                                         jsonArray.getJSONObject(i).getString("staff_id"),
                                         jsonArray.getJSONObject(i).getString("date"),
                                         jsonArray.getJSONObject(i).getString("status"),
                                         jsonArray.getJSONObject(i).getString("holiday_name"),
                                         setUpChildItem(jsonArray.getJSONObject(i))
                                 ));
                            }
                            break;
                        case "4":
                            Toast.makeText(AttendanceHistoryActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                    }
                    setUpView();
                    attendanceHistoryExpandableAdapter.notifyDataSetChanged();
                    firstLoad = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AttendanceHistoryActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("getAttendance", "1");
                params.put("user_id", SharedPreferenceManager.getUserID(AttendanceHistoryActivity.this));
                params.put("month", selectedMonth);
                params.put("year", selectedYear);
                Log.d("haha", "haha: " +selectedYear);
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void setUpView() {
        if (attendanceHistoryGroupObjectArrayList.size() > 0){
            attendanceHistoryActivityListView.setVisibility(View.VISIBLE);
            attendanceHistoryActivityNotFound.setVisibility(View.GONE);
        }
        else{
            attendanceHistoryActivityListView.setVisibility(View.GONE);
            attendanceHistoryActivityNotFound.setVisibility(View.VISIBLE);
        }
        attendanceHistoryActivityProgressBar.setVisibility(View.INVISIBLE);
    }

    public ArrayList setUpChildItem(JSONObject jsonObject){
        ArrayList<AttendanceHistoryChildObject> attendanceHistoryChildObjectArrayList = new ArrayList<>();
        try {
            attendanceHistoryChildObjectArrayList.add(new AttendanceHistoryChildObject(
                    jsonObject.getString("weekday"),
                    jsonObject.getString("time_in"),
                    jsonObject.getString("time_out"),
                    jsonObject.getString("work_hr")
                    ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attendanceHistoryChildObjectArrayList;
    }
/*---------------------------------------------------------------------spinner setting------------------------------------------------------------*/
    private void setUpMonthSpinner() {
        List<String> list = new ArrayList<String>();
        for(int i = 1; i <= 12; i++){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(dataAdapter);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        monthSpinner.setSelection(month);
        this.selectedMonth = monthSpinner.getItemAtPosition(month).toString();
    }

    private void setUpYearSpinner(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        List<String> list = new ArrayList<String>();
        int previousYear = year - 10;
        for(int i = previousYear; i < year + 10; i++){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(dataAdapter);
        //set default as current year
        yearSpinner.setSelection(list.size()-10);
        selectedYear = String.valueOf(year);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(!firstLoad)
        switch(adapterView.getId()){
            case R.id.month_spinner:
                attendanceHistoryActivityProgressBar.setVisibility(View.VISIBLE);
                selectedMonth = adapterView.getSelectedItem().toString();
                attendanceHistoryGroupObjectArrayList.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAttendanceHistory();
                    }
                },200);
                break;
            case R.id.year_spinner:
                attendanceHistoryActivityProgressBar.setVisibility(View.VISIBLE);
                selectedYear = adapterView.getSelectedItem().toString();
                attendanceHistoryGroupObjectArrayList.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAttendanceHistory();
                    }
                },200);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
