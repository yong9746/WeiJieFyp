package com.jby.hanwei;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jby.hanwei.main.HomeActivity;
import com.jby.hanwei.other.MySingleton;
import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jby.hanwei.url.UrlManager.loginPath;
//lol/ new thing from benson
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username, password;
    private Button loginButton, ipAddress;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //session != null
        if(!SharedPreferenceManager.getUserID(this).equals("default")){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
        username = findViewById(R.id.activity_login_username);
        password = findViewById(R.id.activity_login_password);
        loginButton = findViewById(R.id.activity_login_login_button);
        ipAddress = findViewById(R.id.activity_login_ip_address);
        handler = new Handler();
    }


    private void objectSetting() {
        loginButton.setOnClickListener(this);
        ipAddress.setOnClickListener(this);
        checkingIpAddress();

        if(SharedPreferenceManager.getIpAddress(this).equals("show")) ipAddress.setVisibility(View.VISIBLE);
        else ipAddress.setVisibility(View.GONE);

        loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ipAddress.getVisibility() == View.GONE) {
                    SharedPreferenceManager.setIpDialog(LoginActivity.this, "show");
                    ipAddress.setVisibility(View.VISIBLE);
                }
                else{
                    SharedPreferenceManager.setIpDialog(LoginActivity.this, "hide");
                    ipAddress.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_login_login_button:
                checkingInput();
                break;
            case R.id.activity_login_ip_address:
                openIPAddressDialog();
                break;
        }
    }

    private void checkingInput() {
        final String username_input = username.getText().toString().trim();
        final String password_input = password.getText().toString().trim();

        if(!username_input.equals("") && !password_input.equals(""))
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    login(username_input, password_input);
                }
            },200);
        else
            Toast.makeText(this, "EveryField above is required!", Toast.LENGTH_SHORT).show();
    }

    public void login(final String username, final String password){
        String domain =  SharedPreferenceManager.getIpAddress(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+domain + loginPath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    switch (status){
                        case "1":
                            SharedPreferenceManager.setUserID(LoginActivity.this, jsonObject.getString("id"));
                            SharedPreferenceManager.setUserName(LoginActivity.this, jsonObject.getString("name"));
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            break;
                        case "2":
                            Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                            break;
                            default:
                                Toast.makeText(LoginActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Unable Connect to Api!", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

/*-----------------------------------------------------------ip address purpose--------------------------------------------------------------------*/
    private void checkingIpAddress() {
        if (SharedPreferenceManager.getIpAddress(this).equals("default"))
            openIPAddressDialog();
    }

    private void openIPAddressDialog(){
        DialogFragment dialogFragment = new IpAddressDialog();
        FragmentManager fm = getSupportFragmentManager();
        dialogFragment.show(fm, "");
    }
}
