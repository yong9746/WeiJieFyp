package dos.suc.workshopday3;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

public class HousesFragment extends Fragment {
    public String number = "1";
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_houses_fragment, container, false);
        setHasOptionsMenu(true);
        return view;
    }
}
