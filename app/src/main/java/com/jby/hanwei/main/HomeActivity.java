package com.jby.hanwei.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.jby.hanwei.AttendanceActivity;
import com.jby.hanwei.LoginActivity;
import com.jby.hanwei.R;
import com.jby.hanwei.attendanceHistory.AttendanceHistoryActivity;
import com.jby.hanwei.leave.LeaveActivity;
import com.jby.hanwei.leave.leaveList.LeaveListActivity;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    CardView homeActivityAttendance, homeActivityAttendanceHistory, homeActivityAttendanceLogOut;
    CardView homeActivityAttendanceApplyLeave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        toolbar = findViewById(R.id.toolbar);

        homeActivityAttendance = findViewById(R.id.activity_home_take_attendance);
        homeActivityAttendanceHistory = findViewById(R.id.activity_home_attendance_history);
        homeActivityAttendanceLogOut = findViewById(R.id.activity_home_attendance_log_out);
        homeActivityAttendanceApplyLeave = findViewById(R.id.activity_home_attendance_apply_list);
    }

    private void objectSetting() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hi," + SharedPreferenceManager.getUserName(this));
        homeActivityAttendance.setOnClickListener(this);
        homeActivityAttendanceHistory.setOnClickListener(this);
        homeActivityAttendanceLogOut.setOnClickListener(this);
        homeActivityAttendanceApplyLeave.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        clickEffect(view);
        switch (view.getId()){
            case R.id.activity_home_take_attendance:
                startActivity(new Intent(this, AttendanceActivity.class));
                break;
            case R.id.activity_home_attendance_history:
                startActivity(new Intent(this, AttendanceHistoryActivity.class));
                break;
            case R.id.activity_home_attendance_log_out:
                alertMessage();
                break;
            case R.id.activity_home_attendance_apply_list:
                startActivity(new Intent(this, LeaveListActivity.class));
                break;
        }
    }

    public void alertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure that you want to sign out?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferenceManager.setUserName(HomeActivity.this, "default");
                        SharedPreferenceManager.setUserID(HomeActivity.this, "default");
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
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

    public void clickEffect(View view){
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }
}
