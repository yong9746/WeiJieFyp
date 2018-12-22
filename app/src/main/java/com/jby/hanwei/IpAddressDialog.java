package com.jby.hanwei;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jby.hanwei.sharePreference.SharedPreferenceManager;

import java.util.Objects;


public class IpAddressDialog extends DialogFragment {
    View rootView;
    private Button ipAddressDialogConfirmButton;
    private EditText ipAddressDialogIpAddress;
    public IpAddressDialog() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ip_address_dialog, container);
        objectInitialize();
        objectSetting();

        return rootView;
    }

    private void objectInitialize() {
       ipAddressDialogConfirmButton = rootView.findViewById(R.id.ip_address_dialog_confirm_button);
        ipAddressDialogIpAddress = rootView.findViewById(R.id.ip_address_dialog_ip_address);

    }

    private void objectSetting() {
        ipAddressDialogConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAdress = ipAddressDialogIpAddress.getText().toString().trim();
                if(!ipAdress.equals("")){
                    SharedPreferenceManager.setIpAddress(getActivity(), ipAdress);
                    dismiss();
                }
                else Toast.makeText(getActivity(), "Please Enter your IP Address!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(d.getWindow()).setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}