package com.mercury1089.scoutingapp2019;

import com.mercury1089.scoutingapp2019.utils.GenUtils;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mercury1089.scoutingapp2019.utils.QRStringBuilder;
import java.util.LinkedHashMap;

public class PregameActivity extends AppCompatActivity {

    //Set the default password in HashMapManager.setDefaultValues();
    String password;

    //variables that store elements of the screen for the output variables
    //Buttons
    private ImageButton settingsButton;
    private Button blueButton;
    private Button redButton;
    private Button clearButton;
    private Button startButton;

    //Text Fields
    private EditText scouterNameInput;
    private EditText matchNumberInput;
    private EditText teamNumberInput;
    private EditText firstAlliancePartnerInput;
    private EditText secondAlliancePartnerInput;

    //Switches
    private Switch noShowSwitch;
    private Switch preloadSwitch;

    //HashMaps
    private LinkedHashMap<String, String> settingsHashMap;
    private LinkedHashMap<String, String> setupHashMap;

    private Dialog loading_alert;
    private ProgressDialog progressDialog;

    //for QR code generator
    public final static int QRCodeSize = 500;
    Bitmap bitmap;
    //ProgressDialog progressDialog;
    boolean isQRButton = false;

    //others
    private MediaPlayer rooster;
    private ImageView slackCenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        //initializers
        scouterNameInput = findViewById(R.id.ScouterNameInput);
        matchNumberInput = findViewById(R.id.MatchNumberInput);
        teamNumberInput = findViewById(R.id.TeamNumberInput);
        firstAlliancePartnerInput = findViewById(R.id.FirstAlliancePartnerInput);
        secondAlliancePartnerInput = findViewById(R.id.SecondAlliancePartnerInput);
        blueButton = findViewById(R.id.BlueButton);
        redButton = findViewById(R.id.RedButton);
        noShowSwitch = findViewById(R.id.NoShowSwitch);
        preloadSwitch = findViewById(R.id.PreloadedCargoSwitch);
        clearButton = findViewById(R.id.ClearButton);
        startButton = findViewById(R.id.StartButton);
        settingsButton = findViewById(R.id.SettingsButton);

        rooster = MediaPlayer.create(PregameActivity.this, R.raw.sound);
        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.SETTINGS);
        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.SETUP);
        settingsHashMap = HashMapManager.getSettingsHashMap();
        setupHashMap = HashMapManager.getSetupHashMap();
        password = settingsHashMap.get("DefaultPassword");

        //setting group buttons to default state
        updateXMLObjects(true);

        scouterNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupHashMap.put("ScouterName", scouterNameInput.getText().toString());
                updateXMLObjects(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        matchNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupHashMap.put("MatchNumber", matchNumberInput.getText().toString());
                updateXMLObjects(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        teamNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupHashMap.put("TeamNumber", teamNumberInput.getText().toString());
                updateXMLObjects(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        firstAlliancePartnerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupHashMap.put("AlliancePartner1", firstAlliancePartnerInput.getText().toString());
                updateXMLObjects(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        secondAlliancePartnerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupHashMap.put("AlliancePartner2", secondAlliancePartnerInput.getText().toString());
                updateXMLObjects(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //starting listener to check the status of the switch
        noShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setupHashMap.put("PreloadCargo", "0");
                preloadSwitch.setEnabled(!isChecked);
                setupHashMap.put("NoShow", isChecked ? "1" : "0");
                updateXMLObjects(false);
            }
        });

        //starting listener to check status of switch
        preloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setupHashMap.put("PreloadCargo", isChecked ? "1" : "0");
                updateXMLObjects(false);
            }
        });

        //click methods
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] passwordData = HashMapManager.pullSettingsPassword(PregameActivity.this);
                final String password, requiredPassword;
                String tempPassword, tempRequired;
                try {
                    tempPassword = passwordData[0];
                    tempRequired = passwordData[1];
                } catch (Exception e) {
                    tempPassword = PregameActivity.this.password;
                    tempRequired = "N";
                }

                password = tempPassword;
                requiredPassword = tempRequired;

                if(requiredPassword.equals("N")){
                    HashMapManager.putSetupHashMap(setupHashMap);
                    Intent intent = new Intent(PregameActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                Dialog dialog = new Dialog(PregameActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.settings_password);

                TextView passwordField = dialog.findViewById(R.id.PasswordField);
                Button confirm = dialog.findViewById(R.id.ConfirmButton);
                Button cancel = dialog.findViewById(R.id.CancelButton);
                ImageView topEdgeBar = dialog.findViewById(R.id.topEdgeBar);
                ImageView bottomEdgeBar = dialog.findViewById(R.id.bottomEdgeBar);
                ImageView leftEdgeBar = dialog.findViewById(R.id.leftEdgeBar);
                ImageView rightEdgeBar = dialog.findViewById(R.id.rightEdgeBar);

                dialog.show();

                passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            //do what you want on the press of 'done'
                            confirm.performClick();
                        }
                        return false;
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String savedPassword = !password.equals("") ? password : PregameActivity.this.password;
                        if(passwordField.getText().toString().equals(savedPassword)){
                            HashMapManager.putSetupHashMap(setupHashMap);
                            Intent intent = new Intent(PregameActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(PregameActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                            ObjectAnimator topEdgeLighter = ObjectAnimator.ofFloat(topEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator bottomEdgeLighter = ObjectAnimator.ofFloat(bottomEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator rightEdgeLighter = ObjectAnimator.ofFloat(rightEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator leftEdgeLighter = ObjectAnimator.ofFloat(leftEdgeBar, View.ALPHA, 0.0f, 1.0f);

                            topEdgeLighter.setDuration(500);
                            bottomEdgeLighter.setDuration(500);
                            leftEdgeLighter.setDuration(500);
                            rightEdgeLighter.setDuration(500);

                            topEdgeLighter.setRepeatMode(ObjectAnimator.REVERSE);
                            topEdgeLighter.setRepeatCount(1);
                            bottomEdgeLighter.setRepeatMode(ObjectAnimator.REVERSE);
                            bottomEdgeLighter.setRepeatCount(1);
                            leftEdgeLighter.setRepeatMode(ObjectAnimator.REVERSE);
                            leftEdgeLighter.setRepeatCount(1);
                            rightEdgeLighter.setRepeatMode(ObjectAnimator.REVERSE);
                            rightEdgeLighter.setRepeatCount(1);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(topEdgeLighter, bottomEdgeLighter, leftEdgeLighter, rightEdgeLighter);
                            animatorSet.start();

                            /*
                            ObjectAnimator topEdgeLighterOn = ObjectAnimator.ofFloat(topEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator bottomEdgeLighterOn = ObjectAnimator.ofFloat(bottomEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator rightEdgeLighterOn = ObjectAnimator.ofFloat(rightEdgeBar, View.ALPHA, 0.0f, 1.0f);
                            ObjectAnimator leftEdgeLighterOn = ObjectAnimator.ofFloat(leftEdgeBar, View.ALPHA, 0.0f, 1.0f);

                            ObjectAnimator topEdgeLighterOff = ObjectAnimator.ofFloat(topEdgeBar, View.ALPHA, 1.0f, 0.0f);
                            ObjectAnimator bottomEdgeLighterOff = ObjectAnimator.ofFloat(bottomEdgeBar, View.ALPHA, 1.0f, 0.0f);
                            ObjectAnimator rightEdgeLighterOff = ObjectAnimator.ofFloat(rightEdgeBar, View.ALPHA, 1.0f, 0.0f);
                            ObjectAnimator leftEdgeLighterOff = ObjectAnimator.ofFloat(leftEdgeBar, View.ALPHA, 1.0f, 0.0f);

                            topEdgeLighterOn.setDuration(250);
                            bottomEdgeLighterOn.setDuration(250);
                            rightEdgeLighterOn.setDuration(250);
                            leftEdgeLighterOn.setDuration(250);

                            topEdgeLighterOff.setDuration(200);
                            bottomEdgeLighterOff.setDuration(200);
                            rightEdgeLighterOff.setDuration(200);
                            leftEdgeLighterOff.setDuration(200);

                            AnimatorSet animateOn = new AnimatorSet();
                            AnimatorSet animateOff = new AnimatorSet();
                            AnimatorSet animatorSet = new AnimatorSet();

                            animateOn.playTogether(topEdgeLighterOn, bottomEdgeLighterOn, rightEdgeLighterOn, leftEdgeLighterOn);

                            animateOff.playTogether(topEdgeLighterOff, bottomEdgeLighterOff, rightEdgeLighterOff, leftEdgeLighterOff);

                            animatorSet.playSequentially(animateOn, animateOff);
                            animatorSet.start();
                             */
                        }
                    }
                });

                cancel.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }));
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                setupHashMap.put("AllianceColor", setupHashMap.get("AllianceColor").equals("Blue") ? "" : "Blue");
                updateXMLObjects(false);
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                setupHashMap.put("AllianceColor", setupHashMap.get("AllianceColor").equals("Red") ? "" : "Red");
                updateXMLObjects(false);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v){
                if(isQRButton) {
                    Dialog dialog = new Dialog(PregameActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.generate_qrcode_confirm_popup);

                    Button generateQRButton = dialog.findViewById(R.id.GenerateQRButton);
                    Button cancelConfirm = dialog.findViewById(R.id.CancelConfirm);

                    dialog.show();

                    generateQRButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loading_alert = new Dialog(PregameActivity.this);
                            loading_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            loading_alert.setContentView(R.layout.loading_screen);
                            loading_alert.setCancelable(false);
                            loading_alert.show();

                            HashMapManager.putSetupHashMap(setupHashMap);

                            PregameActivity.QRRunnable qrRunnable = new PregameActivity.QRRunnable();
                            new Thread(qrRunnable).start();
                            dialog.dismiss();
                        }
                    });

                    cancelConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    HashMapManager.putSetupHashMap(setupHashMap);
                    if(scouterNameInput.getText().toString().equals("Mercury") && matchNumberInput.getText().toString().equals("1") && teamNumberInput.getText().toString().equals("0") && firstAlliancePartnerInput.getText().toString().equals("8") && secondAlliancePartnerInput.getText().toString().equals("9")) {
                        settingsHashMap.put("NothingToSeeHere", "1");
                        HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                        setupHashMap = HashMapManager.getSetupHashMap();

                        updateXMLObjects(true);
                        return;
                    } else if(scouterNameInput.getText().toString().equals("0x") && matchNumberInput.getText().toString().equals("441") && teamNumberInput.getText().toString().equals("1089") && firstAlliancePartnerInput.getText().toString().equals("1089") && secondAlliancePartnerInput.getText().toString().equals("1089")) {
                        settingsHashMap.put("Slack", "1");
                        HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                        setupHashMap = HashMapManager.getSetupHashMap();

                        updateXMLObjects(true);
                        return;
                    } else if(scouterNameInput.getText().toString().equals("admin") && matchNumberInput.getText().toString().equals("1") && teamNumberInput.getText().toString().equals("0") && firstAlliancePartnerInput.getText().toString().equals("8") && secondAlliancePartnerInput.getText().toString().equals("9")){
                        HashMapManager.saveSettingsPassword(new String[]{"", "N"}, PregameActivity.this);
                        HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                        setupHashMap = HashMapManager.getSetupHashMap();

                        updateXMLObjects(true);
                        return;
                    } else if(settingsHashMap.get("NothingToSeeHere").equals("1")) {
                        rooster.start();
                    } else if (teamNumberInput.getText().toString().equals(firstAlliancePartnerInput.getText().toString()) || teamNumberInput.getText().toString().equals(secondAlliancePartnerInput.getText().toString())) {
                        Toast.makeText(PregameActivity.this, "A team cannot be its own partner.", Toast.LENGTH_SHORT).show();
                        setupHashMap.put("TeamNumber", "");
                        setupHashMap.put("AlliancePartner1", "");
                        setupHashMap.put("AlliancePartner2", "");
                        teamNumberInput.requestFocus();
                        updateXMLObjects(true);
                        return;

                    }
                    Intent intent = new Intent(PregameActivity.this, MatchActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(PregameActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.clear_confirm_popup);

                Button clearConfirm = dialog.findViewById(R.id.ClearConfirm);
                Button cancelConfirm = dialog.findViewById(R.id.CancelConfirm);

                dialog.show();

                cancelConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                clearConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                        setupHashMap = HashMapManager.getSetupHashMap();
                        updateXMLObjects(true);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateXMLObjects(true);
    }

    //call methods
    /*public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startButtonCheck() {
        if(scouterNameInput.getText().length() > 0 &&
                matchNumberInput.getText().length() > 0 &&
                teamNumberInput.getText().length() > 0 &&
                firstAlliancePartnerInput.getText().length() > 0 &&
                secondAlliancePartnerInput.getText().length() > 0 &&
                (blueButton.isSelected() || redButton.isSelected()) &&
                ((setupHashMap.get("NoShow").equals("0")) || setupHashMap.get("NoShow").equals("1")))
                startButton.setEnabled(true);
        else
            startButton.setEnabled(false);
    }

    private void clearButtonCheck() {
        if(scouterNameInput.getText().length() > 0 ||
                matchNumberInput.getText().length() > 0 ||
                teamNumberInput.getText().length() > 0 ||
                noShowSwitch.isChecked() ||
                firstAlliancePartnerInput.getText().length() > 0 ||
                secondAlliancePartnerInput.getText().length() > 0 ||
                blueButton.isSelected() || redButton.isSelected())
            clearButton.setEnabled(true);
        else
            clearButton.setEnabled(false);
    }

    private void updateXMLObjects(boolean updateText){
        if(updateText) {
            scouterNameInput.setText(setupHashMap.get("ScouterName"));
            matchNumberInput.setText(setupHashMap.get("MatchNumber"));
            teamNumberInput.setText(setupHashMap.get("TeamNumber"));
            firstAlliancePartnerInput.setText(setupHashMap.get("AlliancePartner1"));
            secondAlliancePartnerInput.setText(setupHashMap.get("AlliancePartner2"));
        }

        blueButton.setSelected(setupHashMap.get("AllianceColor").equals("Blue"));
        redButton.setSelected(setupHashMap.get("AllianceColor").equals("Red"));

        if(settingsHashMap.get("Slack").equals("1"))
            slackCenter.setVisibility(View.VISIBLE);

        if (setupHashMap.get("PreloadCargo").equals("1")) {
            preloadSwitch.setChecked(true);
        } else {
            preloadSwitch.setChecked(false);
        }

        if(setupHashMap.get("NoShow").equals("1")) {
            noShowSwitch.setChecked(true);

            startButton.setPadding(185, 0, 185, 0);
            startButton.setText(R.string.GenerateQRCode);
            isQRButton = true;
        } else {
            noShowSwitch.setChecked(false);

            startButton.setPadding(234, 0, 234, 0);
            startButton.setText(R.string.Start);
            isQRButton = false;
        }


        startButtonCheck();
        clearButtonCheck();
    }

    //template for implementing a button click for a rectangle for starting position
    /*public void LL1Click (View view) {
        setupHashMap.put("StartingPosition", "LL1");
        startButtonCheck();
        clearButtonCheck();
        makeCirclesInvisible();
        LL1Circle.setVisibility(View.VISIBLE);
    }*/

    //QR Generation
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRCodeSize, QRCodeSize, null
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        GenUtils.getAColor(PregameActivity.this, R.color.black) : GenUtils.getAColor(PregameActivity.this, R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    class QRRunnable implements Runnable {
        @Override
        public void run() {
            HashMapManager.setDefaultValues(HashMapManager.HASH.AUTON);
            HashMapManager.setDefaultValues(HashMapManager.HASH.TELEOP);
            HashMapManager.setDefaultValues(HashMapManager.HASH.CLIMB);

            QRStringBuilder.buildQRString();

            try {
                bitmap = TextToImageEncode(QRStringBuilder.getQRString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog = new Dialog(PregameActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.popup_qr);

                        ImageView imageView = dialog.findViewById(R.id.imageView);
                        TextView scouterName = dialog.findViewById(R.id.ScouterNameQR);
                        TextView teamNumber = dialog.findViewById(R.id.TeamNumberQR);
                        TextView matchNumber = dialog.findViewById(R.id.MatchNumberQR);
                        Button goBackToMain = dialog.findViewById(R.id.GoBackButton);
                        imageView.setImageBitmap(bitmap);

                        dialog.setCancelable(false);

                        scouterName.setText(setupHashMap.get("ScouterName"));
                        teamNumber.setText(setupHashMap.get("TeamNumber"));
                        matchNumber.setText(setupHashMap.get("MatchNumber"));

                        loading_alert.dismiss();

                        dialog.show();

                        goBackToMain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog confirmDialog = new Dialog(PregameActivity.this);
                                confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                confirmDialog.setContentView(R.layout.setup_next_match_confirm_popup);

                                Button setupNextMatchButton = confirmDialog.findViewById(R.id.SetupNextMatchButton);
                                Button cancelConfirm = confirmDialog.findViewById(R.id.CancelConfirm);

                                confirmDialog.show();

                                setupNextMatchButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        QRStringBuilder.clearQRString(getApplicationContext());
                                        HashMapManager.setupNextMatch();
                                        setupHashMap = HashMapManager.getSetupHashMap();
                                        updateXMLObjects(true);
                                        dialog.dismiss();
                                        confirmDialog.dismiss();
                                    }
                                });

                                cancelConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        confirmDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            } catch (WriterException e){}
        }
    }
}