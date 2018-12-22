package com.jby.hanwei.leave.leaveList;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jby.hanwei.url.UrlManager.Leave;

public class LeaveRemainingDialog extends DialogFragment {
    View rootView;
    public TextView close;
    private TextView annual, casual, emergency, hospital, maternity, paternity, sick;


    public LeaveRemainingDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.leave_remaining_dialog, container);
        objectInitialize();
        objectSetting();

        return rootView;
    }

    private void objectInitialize() {
        annual = rootView.findViewById(R.id.annual);
        casual = rootView.findViewById(R.id.casual);
        emergency = rootView.findViewById(R.id.emergency);
        maternity = rootView.findViewById(R.id.maternity);
        paternity = rootView.findViewById(R.id.paternity);
        sick = rootView.findViewById(R.id.sick);
        hospital = rootView.findViewById(R.id.hospitalization);

        close = rootView.findViewById(R.id.close);

    }

    private void objectSetting() {
        getAllLeave();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(d.getWindow()).setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setWindowAnimations(R.style.dialog_up_down);
        }
    }

    public void getAllLeave() {
        String domain = SharedPreferenceManager.getIpAddress(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + domain + Leave, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("haha", "haha: " +response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "1":
                            JSONObject jsonArray = jsonObject.getJSONObject("value");
                            Log.d("haha", "haha: " + jsonArray);
                            annual.setText("Annual Leave: " + jsonArray.getString("annual_l"));
                            casual.setText("Casual Leave: " +jsonArray.getString("casual_l"));
                            emergency.setText("Emergency Leave: " +jsonArray.getString("emergency_l"));
                            hospital.setText("Hospital Leave: " +jsonArray.getString("hospital_l"));
                            maternity.setText("Maternity Leave: " +jsonArray.getString("maternity_l"));
                            paternity.setText("Paternity Leave: " +jsonArray.getString("paternity_l"));
                            sick.setText("Sick Leave: " +jsonArray.getString("sick_l"));
                            break;
                        default:
                            Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leaveDays","1");
                params.put("user_id", SharedPreferenceManager.getUserID(getActivity()));
                return params;
            }
        };
        MySingleton.getmInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}