package com.jby.hanwei.leave;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jby.hanwei.R;
import com.jby.hanwei.leave.leaveList.LeaveRemainingDialog;
import com.jby.hanwei.other.MySingleton;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.jby.hanwei.leave.leaveList.LeaveListActivity.UPDATE_LIST;
import static com.jby.hanwei.url.UrlManager.Leave;


public class LeaveActivity extends AppCompatActivity implements View.OnClickListener, OnItemSelectedListener {
    private Toolbar toolbar;
    private TextView fromDate, toDate, leaveRemaining;
    private EditText reason;
    private Spinner leaveType;
    private Button apply;

    Calendar mCurrentTime;
    DatePickerDialog datePicker;
    Calendar calendar;
    private String selectDate;
    private String selectedLeaveType = "";
    private boolean isShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        toolbar = findViewById(R.id.toolbar);
        leaveRemaining = findViewById(R.id.leave_remaining);

        fromDate = findViewById(R.id.activity_leave_from_date);
        toDate = findViewById(R.id.activity_leave_to_date);
        reason = findViewById(R.id.activity_leavel_reason);

        apply = findViewById(R.id.activity_apply_leave);
        leaveType = findViewById(R.id.activity_leave_type);
    }

    private void objectSetting() {
        //setup action bar
        leaveRemaining.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Apply Leave");

//        fromDate.setOnClickListener(this);
//        toDate.setOnClickListener(this);
        apply.setOnClickListener(this);
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        leaveType.setOnItemSelectedListener(this);
        leaveRemaining.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setupSpinner();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_leave_from_date:
                if(!isShowing){
                    selectDateDialog(true);
                    isShowing = true;
                }
                break;
            case R.id.activity_leave_to_date:
                if(!isShowing){
                    selectDateDialog(false);
                    isShowing = true;
                }
                break;
            case R.id.activity_apply_leave:
                checkingInput();
                break;
            case R.id.leave_remaining:
                DialogFragment dialogFragment = new LeaveRemainingDialog();
                FragmentManager fm = getSupportFragmentManager();
                dialogFragment.show(fm, "");
                break;
        }
    }

    private void checkingInput(){
        String userReason = reason.getText().toString();
        String selectFromDate = fromDate.getText().toString();
        String selectToDate = toDate.getText().toString();

        Log.d("haha ", "haha fromDate: " + selectFromDate);
        Log.d("haha ", "haha SelectDate: " + selectToDate);
        if(!userReason.equals("") && !fromDate.equals("") && !toDate.equals("") && leaveType.getSelectedItemPosition()!= 0) applyLeave(userReason, selectFromDate, selectToDate);
        else Toast.makeText(this, "Every field above is required!", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0){
            selectedLeaveType = leaveType.getSelectedItem().toString();
            Log.d("haha","haha: " + selectedLeaveType);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setupSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Please Select Leave Type");
        categories.add("Annual");
        categories.add("Casual");
        categories.add("Emergency");
        categories.add("Hospitalization");
        categories.add("Maternity");
        categories.add("Hospitalization");
        categories.add("Paternity");
        categories.add("Sick");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_layout, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        leaveType.setAdapter(dataAdapter);
    }

    private void selectDateDialog(final boolean isFromdate){
        mCurrentTime = Calendar.getInstance();
        int dayOfMonth = mCurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mCurrentTime.get(Calendar.MONTH);
        int year = mCurrentTime.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar = Calendar.getInstance();
                        calendar.set(year,monthOfYear, dayOfMonth);

                        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                        SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY", Locale.getDefault());

                        String strDate = monthFormat.format(calendar.getTime());
                        String day = dayFormat.format(calendar.getTime());
                        String selectYear = yearFormat.format(calendar.getTime());
                        selectDate = day + "/" + strDate + "/" + selectYear;

                        if(isFromdate)
                            fromDate.setText(selectDate);
                        else
                            toDate.setText(selectDate);

                    }
                }, year, month, dayOfMonth);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isShowing = false;
            }
        });
        datePicker.show();
    }

    public void applyLeave(final String userReason, final String fromDate, final String toDate) {
        String domain = SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + Leave, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("haha", "haha: " +response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            Toast.makeText(LeaveActivity.this, "Apply Successful!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            break;
                        default:
                            Toast.makeText(LeaveActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LeaveActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date_from", fromDate);
                params.put("date_to", toDate);
                params.put("type", selectedLeaveType);
                params.put("reason", userReason);
                params.put("insert","1");
                params.put("user_id", SharedPreferenceManager.getUserID(LeaveActivity.this));
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        setResult(UPDATE_LIST);
        super.onBackPressed();
    }

}
