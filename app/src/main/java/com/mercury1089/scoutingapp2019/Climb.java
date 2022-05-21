package com.mercury1089.scoutingapp2019;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.util.LinkedHashMap;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mercury1089.scoutingapp2019.utils.GenUtils;
import com.mercury1089.scoutingapp2019.utils.QRStringBuilder;

public class Climb extends Fragment {
    //HashMaps for sending QR data between screens
    private LinkedHashMap<String, String> setupHashMap;
    private LinkedHashMap<String, String> climbHashMap;

    //Buttons
    private Button generateQRButton;

    //Switches
    private Switch climbedSwitch;

    //TextViews
    private TextView endgameID;
    private TextView endgameDirections;
    private TextView climbedID;
    private TextView climbRungDirections;

    //Containers
    private TabLayout rungTabs;

    //other variables
    private ProgressDialog progressDialog;
    private Dialog loading_alert;
    public final static int QRCodeSize = 500;

    public static Climb newInstance() {
        Climb fragment = new Climb();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private MatchActivity context;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = (MatchActivity) getActivity();
        return inflater.inflate(R.layout.fragment_climb, container, false);
    }

    public void onStart(){
        super.onStart();

        //linking variables to XML elements on the screen
        endgameID = getView().findViewById(R.id.IDEndgame);
        endgameDirections = getView().findViewById(R.id.IDEndgameDirections);

        climbedID = getView().findViewById(R.id.IDClimbed);
        climbedSwitch = getView().findViewById(R.id.ClimbedSwitch);
        climbRungDirections = getView().findViewById(R.id.IDClimbRungDirections);

        generateQRButton = getView().findViewById(R.id.GenerateQRButton);
        rungTabs = getView().findViewById(R.id.rungTabs);
        //Removes tab indicator because climb switch starts out as null
        rungTabs.setSelectedTabIndicator(null);

        //set listeners for buttons
        climbedSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                climbHashMap.put("Climbed", isChecked ? "1" : "0");
                //Default option for rung is LOW
                if (isChecked) {
                    //Sets tab indicator to built-in default
                    rungTabs.setSelectedTabIndicator(R.drawable.mtrl_tabs_default_indicator);
                    rungTabs.getTabAt(0).select();
                    climbHashMap.put("Rung", "L");
                } else {
                    //Removes tab indicator
                    rungTabs.setSelectedTabIndicator(null);

                }
                updateXMLObjects();
            }
        });

        rungTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String text = (String) tab.getText();
                if (text.equals("LOW"))
                    climbHashMap.put("Rung", "L");
                else if (text.equals("MID"))
                    climbHashMap.put("Rung", "M");
                else if (text.equals("HIGH"))
                    climbHashMap.put("Rung", "H");
                else if (text.equals("TRAVERSAL"))
                    climbHashMap.put("Rung", "T");
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { climbHashMap.put("Rung", "0"); }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.generate_qrcode_confirm_popup);

                Button generateQRButton = dialog.findViewById(R.id.GenerateQRButton);
                Button cancelConfirm = dialog.findViewById(R.id.CancelConfirm);

                dialog.show();

                generateQRButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loading_alert = new Dialog(context);
                        loading_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        loading_alert.setContentView(R.layout.loading_screen);
                        loading_alert.setCancelable(false);
                        loading_alert.show();

                        HashMapManager.putSetupHashMap(setupHashMap);
                        HashMapManager.putClimbHashMap(climbHashMap);

                        Climb.QRRunnable qrRunnable = new Climb.QRRunnable();
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
            }
        });
    }

    private void rungTabsEnabledState(boolean enable) {
        if (!enable)
            climbHashMap.put("Rung", "0");
        LinearLayout tabStrip = ((LinearLayout)rungTabs.getChildAt(0));
        tabStrip.setEnabled(enable);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setEnabled(enable);
            tabStrip.getChildAt(i).setClickable(enable);
        }
    }

    private void climbButtonsEnabledState(boolean enable) {
        climbRungDirections.setEnabled(enable);
        //Always want the climbed switch and "climb" text next to switch to be enabled unless fell over/died is checked
        rungTabsEnabledState(enable);
    }

    private void climbedSwitchEnabledState(boolean enable) {
        //"Climbing/Traversing" title
        endgameID.setEnabled(enable);
        //Directions below title
        endgameDirections.setEnabled(enable);
        //Switch
        climbedSwitch.setEnabled(enable);
        climbedID.setEnabled(enable);
    }

    private void updateXMLObjects() {
        if (setupHashMap.get("FellOver").equals("1")) {
            climbButtonsEnabledState(false);
            climbedSwitch.setChecked(false);
            climbHashMap.put("Rung", "0");
            climbedSwitchEnabledState(false);
        } else if (setupHashMap.get("FellOver").equals("0")) {
            climbButtonsEnabledState(true);
            climbedSwitchEnabledState(true);
        }

        if (climbHashMap.get("Climbed").equals("0")) {
            climbHashMap.put("Rung", "0");
            climbedSwitch.setChecked(false);
            climbButtonsEnabledState(false);
        } else if (climbHashMap.get("Climbed").equals("1")) {
            climbedSwitch.setChecked(true);
            climbButtonsEnabledState(true);
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming visible, then...
            if (isVisibleToUser) {
                setupHashMap = HashMapManager.getSetupHashMap();
                climbHashMap = HashMapManager.getClimbHashMap();
                updateXMLObjects();
                //set all objects in the fragment to their values from the HashMaps
            } else {
                HashMapManager.putSetupHashMap(setupHashMap);
                HashMapManager.putClimbHashMap(climbHashMap);
            }
        }
    }

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
                        GenUtils.getAColor(context, R.color.black) : GenUtils.getAColor(context, R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    class QRRunnable implements Runnable {
        @Override
        public void run() {
            HashMapManager.checkNullOrEmpty(HashMapManager.HASH.AUTON);
            HashMapManager.checkNullOrEmpty(HashMapManager.HASH.TELEOP);
            HashMapManager.checkNullOrEmpty(HashMapManager.HASH.CLIMB);

            QRStringBuilder.buildQRString();

            try {
                Bitmap bitmap = TextToImageEncode(QRStringBuilder.getQRString());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog = new Dialog(context);
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
                                Dialog confirmDialog = new Dialog(context);
                                confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                confirmDialog.setContentView(R.layout.setup_next_match_confirm_popup);

                                Button setupNextMatchButton = confirmDialog.findViewById(R.id.SetupNextMatchButton);
                                Button cancelConfirm = confirmDialog.findViewById(R.id.CancelConfirm);

                                confirmDialog.show();

                                setupNextMatchButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        QRStringBuilder.clearQRString(context);
                                        HashMapManager.setupNextMatch();
                                        Intent intent = new Intent(context, PregameActivity.class);
                                        startActivity(intent);
                                        context.finish();
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
