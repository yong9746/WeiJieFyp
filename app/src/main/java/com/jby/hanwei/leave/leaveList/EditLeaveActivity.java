package com.jby.hanwei.leave.leaveList;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import static com.jby.hanwei.url.UrlManager.loginPath;

public class EditLeaveActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private EditText reason;
    private Spinner leaveType;
    private Button edit;
    private TextView delete, fromDate, toDate;

    Calendar mCurrentTime;
    DatePickerDialog datePicker;
    Calendar calendar;
    private String selectDate;
    private String selectedLeaveType = "";
    private String id;
    List<String> categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_leave);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        toolbar = findViewById(R.id.toolbar);
        fromDate = findViewById(R.id.activity_edit_leave_from_date);
        toDate = findViewById(R.id.activity_edit_leave_to_date);
        reason = findViewById(R.id.activity_leavel_reason);
        delete = findViewById(R.id.delete_list);

        edit = findViewById(R.id.activity_apply_edit);
        leaveType = findViewById(R.id.activity_edit_leave_type);
    }

    private void objectSetting() {
        //setup action bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Apply Leave");

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        leaveType.setOnItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setupSpinner();
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            fromDate.setText(bundle.getString("from_date"));
            toDate.setText(bundle.getString("to_date"));
            reason.setText(bundle.getString("reason"));
            id = bundle.getString("leave_id");
            for(int i = 0; i<categories.size(); i++){
                if(categories.get(i).equals(bundle.getString("type"))){
                    leaveType.setSelection(i);
                    break;
                }
            }
            String status = bundle.getString("status");
            if(status != null){
                if(status.equals("Pending")){
                    delete.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_edit_leave_from_date:
                selectDateDialog(true);
                break;
            case R.id.activity_edit_leave_to_date:
                selectDateDialog(false);
                break;
            case R.id.activity_apply_edit:
                checkingInput();
                break;
            case R.id.delete_list:
                alertDialog("Delete", "Are you sure that you want to delete this leave?");
                break;
        }
    }

    private void checkingInput(){
        String userReason = reason.getText().toString();
        String selectFromDate = fromDate.getText().toString();
        String selectToDate = toDate.getText().toString();
        if(!userReason.equals("") && !fromDate.equals("") && !toDate.equals("") && leaveType.getSelectedItemPosition()!= 0) editLeave(id, userReason, selectFromDate, selectToDate);
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
        categories = new ArrayList<>();
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
        datePicker.show();
    }

    public void editLeave(final String id, final String userReason, final String fromDate, final String toDate) {
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
                            Toast.makeText(EditLeaveActivity.this, "Apply Successful!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            break;
                        default:
                            Toast.makeText(EditLeaveActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditLeaveActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date_from", fromDate);
                params.put("date_to", toDate);
                params.put("type", selectedLeaveType);
                params.put("reason", userReason);
                params.put("leave_id", id);
                params.put("edit", "1");
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    public void deleteLeave() {
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
                            Toast.makeText(EditLeaveActivity.this, "Delete Successful!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            break;
                        default:
                            Toast.makeText(EditLeaveActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditLeaveActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("delete", "1");
                params.put("leave_id", id);
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    public void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteLeave();
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onBackPressed() {
        setResult(UPDATE_LIST);
        super.onBackPressed();
    }
}
