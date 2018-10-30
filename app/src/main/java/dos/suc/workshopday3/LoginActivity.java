package dos.suc.workshopday3;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dos.suc.workshopday3.preference.SavePreferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class    LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;
    OkHttpClient okHttpClient;
    SavePreferences savePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        okHttpClient = new OkHttpClient();
        savePreferences = new SavePreferences(LoginActivity.this);



        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }
    public void userLogin(){

        String strEmail = etEmail.getText().toString().trim();
        String strPass = etPassword.getText().toString().trim();
        if(strEmail.equals("") && strPass.equals("")){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }else{
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", strEmail)
                    .add("password", strPass)
                    .build();
            Request request = new Request.Builder()
                    .url("http://www.bose16c1.com/lwj/android/userSignin.php")
                    .post(requestBody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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

                                    String id = jsonObject.getString("id");

                                    savePreferences.saveUserID(id);

                                    startActivity(new Intent(LoginActivity.this, UserDashBoard.class));
                                } else {

                                    Toast.makeText(LoginActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
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
}

