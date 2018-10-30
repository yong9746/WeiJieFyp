package dos.suc.workshopday3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etPhone;
    private EditText etIC;
    private Button btnRegister;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back button on action bar

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etIC = (EditText) findViewById(R.id.etIC);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        okHttpClient = new OkHttpClient();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcc();
            }
        });


    }

    public void createAcc() {

        String strEmail = etEmail.getText().toString().trim();
        String strPassword = etPassword.getText().toString();
        String strName = etName.getText().toString().trim();
        String strPhone = etPhone.getText().toString().trim();
        String strIC = etIC.getText().toString().trim();

        if(strEmail.equals("") && strPassword.equals("") && strName.equals("") && strPhone.equals("") && strIC.equals("")) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }else {
            RequestBody formbody = new FormBody.Builder()
                    .add("email", strEmail)
                    .add("password", strPassword)
                    .add("name", strName)
                    .add("phone", strPhone)
                    .add("ic", strIC).build();

            Request request = new Request.Builder()
                    .url("http://www.bose16c1.com/lwj/android/createAcc.php")
                    .post(formbody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this, "Connecction Error", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String resStr = response.body().string().toString();

                                JSONObject jsonObject = new JSONObject(resStr);
                                String success = jsonObject.getString("success");

                                if (success.equals("1")) {
                                    Toast.makeText(SignUpActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                } else if (success.equals("0")) {
                                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "hhh", Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); //back button
        }

        return super.onOptionsItemSelected(item);
    }
}
