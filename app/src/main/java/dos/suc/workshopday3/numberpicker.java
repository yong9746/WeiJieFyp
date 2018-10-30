package dos.suc.workshopday3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;

public class numberpicker extends AppCompatActivity {

    NumberPicker noPicker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberpicker);

        noPicker = (NumberPicker)findViewById(R.id.pickNumber);
        noPicker.setMaxValue(1000);
        noPicker.setMinValue(0);
        noPicker.setWrapSelectorWheel(false);
    }
}
