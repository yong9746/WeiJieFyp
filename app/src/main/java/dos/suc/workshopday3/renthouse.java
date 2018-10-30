package dos.suc.workshopday3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class renthouse extends AppCompatActivity {
    private OkHttpClient okHttpClient;
    private EditText et_people, et_bedroom, et_bed;
    private Spinner spinner_kind, spinner_type;
    private String kind[] = {"- Select Your Kind -", "Kind1", "Kind2", "Kind3", "Kind4", "Kind5"};
    private String type[] = {"- Slect Your Type -", "Kind1", "Kind2", "Kind3", "Kind4", "Kind5"};
    private String strKind, strType, strPeople, strBedroom, strBed, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renthouse);
        okHttpClient = new OkHttpClient();
        et_people  = (EditText) findViewById(R.id.et_people);
        et_bedroom  = (EditText) findViewById(R.id.et_bedroom);
        et_bed  = (EditText) findViewById(R.id.et_bed);
        spinner_kind = (Spinner) findViewById(R.id.spinner_kind);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(renthouse.this, R.layout.activity_renthouse, kind);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(renthouse.this, R.layout.activity_renthouse, type);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.activity_renthouse);
        spinner_kind.setAdapter(spinnerArrayAdapter1);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.activity_renthouse);
        spinner_type.setAdapter(spinnerArrayAdapter2);
        strPeople = et_people.getText().toString();
        strBed = et_bed.getText().toString();
        strBedroom = et_bedroom.getText().toString();
        strKind = spinner_kind.getSelectedItem().toString();
        strType = spinner_type.getSelectedItem().toString();
        if(!strPeople.equals("") && !strBed.equals("") && !strBedroom.equals("") && !strKind.equals("") && !strType.equals("") ){
            Toast.makeText(this, "Please fill in all information", Toast.LENGTH_SHORT).show();
        }else{
            insertData();
        }
    }


    private void insertData() {
        RequestBody requestBody = new FormBody.Builder()
                .add("people", strPeople)
                .add("bed", strBed)
                .add("bedroom", strBedroom)
                .add("kind", strKind)
                .add("type", strType)
                .build();
        Request request = new Request.Builder()
                .url("http://www.bose16c1.com/lwj/android/renthouse.php")
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            String s = response.body().string().replace("\t", "");
            JSONObject jsonObject = new JSONObject(s);
            status = jsonObject.getString("status");
            if (status.equals("1")) {
                Toast.makeText(this, "Successfull", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Correction Error", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}