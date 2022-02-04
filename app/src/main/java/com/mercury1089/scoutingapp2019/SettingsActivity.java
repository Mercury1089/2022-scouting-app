package com.mercury1089.scoutingapp2019;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury1089.scoutingapp2019.utils.ListAdapter;
import java.util.LinkedHashMap;

public class SettingsActivity extends AppCompatActivity {

    private LinkedHashMap settingsHashMap;
    private String[] qrList;
    private ListView qrCodeSelector;
    private ListAdapter listAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //assigning variables to their equivalent screen elements
        Button localStorageResetButton = findViewById(R.id.LocalStorageResetButton);
        Button backButton = findViewById(R.id.BackButton);
        Button clearQRCache = findViewById(R.id.ClearQRCodesButton);
        Button passwordSettingsButton = findViewById(R.id.PasswordSettingsButton);
        ImageButton muteButton = findViewById(R.id.MuteButton);

        qrCodeSelector = findViewById(R.id.QRCodeListView);

        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.SETTINGS);
        settingsHashMap = HashMapManager.getSettingsHashMap();
        qrList = HashMapManager.setupQRList(getApplicationContext());

        listAdapter = new ListAdapter(this, qrList);
        addQRCodes();

        if(settingsHashMap.get("NothingToSeeHere").equals("1")) {
            muteButton.setVisibility(View.VISIBLE);
            muteButton.setEnabled(true);
        } else {
            muteButton.setVisibility(View.INVISIBLE);
            muteButton.setEnabled(false);
        }

        try {
            String requiredPassword = HashMapManager.pullSettingsPassword(SettingsActivity.this)[1];
            passwordSettingsButton.setSelected(requiredPassword.equals("Y"));
        } catch (Exception e) {
            passwordSettingsButton.setSelected(false);
        }

        if(qrList.length > 0)
            clearQRCache.setEnabled(true);
        else
            clearQRCache.setEnabled(false);

        localStorageResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMapManager.setDefaultValues(HashMapManager.HASH.SETTINGS);
                HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                HashMapManager.setDefaultValues(HashMapManager.HASH.AUTON);
                HashMapManager.setDefaultValues(HashMapManager.HASH.TELEOP);
                HashMapManager.setDefaultValues(HashMapManager.HASH.CLIMB);
                Toast.makeText(SettingsActivity.this, "All variables successfully reset.", Toast.LENGTH_SHORT).show();
            }
        });

        passwordSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = SettingsActivity.this;
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.enter_password_popup);

                TextView passwordField = dialog.findViewById(R.id.PasswordField);
                Switch requirePasswordSwitch = dialog.findViewById(R.id.SettingsPasswordSwitch);
                Button doneButton = dialog.findViewById(R.id.DoneButton);
                Button cancelButton = dialog.findViewById(R.id.CancelButton);

                String[] passwordData = HashMapManager.pullSettingsPassword(context);
                try {
                    passwordField.setText(passwordData[0]);
                    requirePasswordSwitch.setChecked(passwordData[1].equals("Y"));
                } catch (Exception e) {}

                passwordField.setHint(settingsHashMap.get("DefaultPassword").toString());

                dialog.show();

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = passwordField.getText().toString(), requiredPassword = requirePasswordSwitch.isChecked() ? "Y" : "N";
                        HashMapManager.saveSettingsPassword(new String[]{password, requiredPassword}, context);
                        dialog.dismiss();
                        HashMapManager.saveSettingsPassword(new String[]{password, requiredPassword}, context);
                        passwordSettingsButton.setSelected(requiredPassword.equals("Y"));
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PregameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                settingsHashMap.put("NothingToSeeHere", "0");
                Toast.makeText(SettingsActivity.this, "Muted", Toast.LENGTH_SHORT).show();
                muteButton.setVisibility(View.INVISIBLE);
                muteButton.setEnabled(false);
            }
        });

        clearQRCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.clear_qr_cache_confirm);

                Button clearConfirm = dialog.findViewById(R.id.ClearConfirm);
                Button cancelConfirm = dialog.findViewById(R.id.CancelConfirm);

                dialog.show();

                clearConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMapManager.outputQRList(new String[0], SettingsActivity.this);
                        qrList = HashMapManager.setupQRList(SettingsActivity.this);
                        listAdapter = new ListAdapter(SettingsActivity.this, qrList);
                        qrCodeSelector.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();
                        clearQRCache.setEnabled(false);
                        dialog.dismiss();
                    }
                });

                cancelConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void addQRCodes(){
        qrCodeSelector.setAdapter(listAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HashMapManager.putSettingsHashMap(settingsHashMap);
    }
}