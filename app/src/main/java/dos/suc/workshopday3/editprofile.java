package dos.suc.workshopday3;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dos.suc.workshopday3.preference.LoadPreferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class editprofile extends AppCompatActivity {
    private LoadPreferences loadPreferences;
    private OkHttpClient okHttpClient;
    private Handler mHandler;
    EditText et_email, et_name, et_phone, et_ic;
    Button btn_save;
    String id, email, name, phone, ic;
    String uptEmail, uptName, uptPhone, uptIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadPreferences = new LoadPreferences(this);
        mHandler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient();
        et_email = (EditText) findViewById(R.id.et_email);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_ic = (EditText) findViewById(R.id.et_ic);
        btn_save = (Button) findViewById(R.id.btn_save);
        displayOriginalData();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    public void displayOriginalData() {
        final ProgressDialog progressDialog = new ProgressDialog(editprofile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        id = loadPreferences.getUserID();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();
        Request request = new Request.Builder()
                .url("http://www.bose16c1.com/lwj/android/viewprofile.php")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                Toast.makeText(editprofile.this, "Fail to select data from API", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String resStr = response.body().string().toString();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(resStr);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        email = jsonObject.getString("email");
                        name = jsonObject.getString("name");
                        phone = jsonObject.getString("phone");
                        ic = jsonObject.getString("ic");

                        progressDialog.dismiss();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                et_email.setText(email);
                                et_name.setText(name);
                                et_phone.setText(phone);
                                et_ic.setText(ic);

                            }


                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(editprofile.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateData() {
        final ProgressDialog progressDialog = new ProgressDialog(editprofile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        id = loadPreferences.getUserID();
        uptEmail = et_email.getText().toString();
        uptName = et_name.getText().toString();
        uptPhone = et_phone.getText().toString();
        uptIC = et_ic.getText().toString();
        if (uptEmail.equals("") || uptName.equals("") || uptPhone.equals("") || uptIC.equals("")) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill in all field", Toast.LENGTH_SHORT).show();
        } else {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id", id)
                    .add("email", uptEmail)
                    .add("name", uptName)
                    .add("phone", uptPhone)
                    .add("ic", uptIC)
                    .build();
            Request request = new Request.Builder()
                    .url("http://www.bose16c1.com/lwj/android/updateprofile.php")
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    progressDialog.dismiss();
                    Toast.makeText(editprofile.this, "Fail to select data from API", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    String resStr = response.body().string().toString();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resStr);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            progressDialog.dismiss();

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(editprofile.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                                }


                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(editprofile.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }
}