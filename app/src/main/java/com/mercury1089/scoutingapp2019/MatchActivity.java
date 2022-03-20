package com.mercury1089.scoutingapp2019;

import com.google.android.material.tabs.TabLayout;
import com.mercury1089.scoutingapp2019.utils.GenUtils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.LinkedHashMap;

public class MatchActivity extends AppCompatActivity {

    //variables that store elements of the screen for the output variables
    public TabLayout tabs;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    public LinkedHashMap<String, String> setupHashMap;

    //for QR code generator
    public final static int QRCodeSize = 500;
    Bitmap bitmap;
    String QRValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        //initializers
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.SETUP);
        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.AUTON);
        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.TELEOP);
        HashMapManager.checkNullOrEmpty(HashMapManager.HASH.CLIMB);
    }

    //back button
    @Override
    public void onBackPressed() {
        Dialog dialog = new Dialog(MatchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_confirm_popup);

        Button exitConfirm = dialog.findViewById(R.id.ExitConfirm);
        Button cancelConfirm = dialog.findViewById(R.id.CancelConfirm);

        dialog.show();

        cancelConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exitConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HashMapManager.setDefaultValues(HashMapManager.HASH.SETUP);
                HashMapManager.setDefaultValues(HashMapManager.HASH.AUTON);
                HashMapManager.setDefaultValues(HashMapManager.HASH.TELEOP);
                HashMapManager.setDefaultValues(HashMapManager.HASH.CLIMB);
                Intent intent = new Intent(MatchActivity.this, PregameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //call methods

    //template for implementing a button click for a rectangle for starting position
    /*public void LL1Click (View view) {
        setupHashMap.put("StartingPosition", "LL1");
        startButtonCheck();
        clearButtonCheck();
        makeCirclesInvisible();
        LL1Circle.setVisibility(View.VISIBLE);
    }*/

    //QR Generation
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
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
                        GenUtils.getAColor(MatchActivity.this, R.color.design_default_color_primary_dark) : GenUtils.getAColor(MatchActivity.this, R.color.bootstrap_dropdown_divider);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}