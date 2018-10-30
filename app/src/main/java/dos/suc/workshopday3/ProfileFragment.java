package dos.suc.workshopday3;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfileFragment extends Fragment implements View.OnClickListener
{
    public String number = "1";
    private View view;
    private Button btn_profile, btn_editProfile, btn_rent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile_fragment, container, false);
        btn_profile = view.findViewById(R.id.btn_profile);
        btn_editProfile = view.findViewById(R.id.btn_editProfile);
        btn_rent = view.findViewById(R.id.btn_rent);

        btn_profile.setOnClickListener(this);
        btn_editProfile.setOnClickListener(this);
        btn_rent.setOnClickListener(this);
        return view;



    }
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_profile:
                Intent i = new Intent(getActivity(),viewprofile.class);
                startActivity(i);
                break;

            case R.id.btn_editProfile:
                Intent o = new Intent(getActivity(),editprofile.class);
                startActivity(o);
                break;

            case R.id.btn_rent:
                Intent p = new Intent(getActivity(),numberpicker.class);
                startActivity(p);
                break;

        }
    }

}
