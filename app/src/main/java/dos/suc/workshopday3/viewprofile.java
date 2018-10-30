package dos.suc.workshopday3;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import dos.suc.workshopday3.preference.LoadPreferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class viewprofile extends AppCompatActivity {
    private LoadPreferences loadPreferences;
    private OkHttpClient okHttpClient;
    private Handler mHandler;
    String id, email, name, phone, ic;
    TextView tv_email, tv_name, tv_phone, tv_ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_viewprofile);
        loadPreferences = new LoadPreferences(this);
        mHandler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient();
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_ic = (TextView) findViewById(R.id.tv_ic);


        displayUser();
    }


    public void displayUser() {
        final ProgressDialog progressDialog = new ProgressDialog(viewprofile.this);
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
                Toast.makeText(viewprofile.this, "Fail to select data from API", Toast.LENGTH_SHORT).show();
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

                                tv_email.setText(email);
                                tv_name.setText(name);
                                tv_phone.setText(phone);
                                tv_ic.setText(ic);

                            }


                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(viewprofile.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
