package dos.suc.workshopday3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import dos.suc.workshopday3.preference.LoadPreferences;

public class Dashboard extends AppCompatActivity {

    LoadPreferences loadPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        loadPreferences = new LoadPreferences(Dashboard.this);

        String id = loadPreferences.getUserID();
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
