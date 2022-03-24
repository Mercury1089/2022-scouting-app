package com.mercury1089.scoutingapp2019.utils;

import android.content.Context;

import com.mercury1089.scoutingapp2019.HashMapManager;

import java.util.LinkedHashMap;

public class QRStringBuilder {

    private static StringBuilder QRString = new StringBuilder();

    public static void buildQRString(){
        LinkedHashMap setup = HashMapManager.getSetupHashMap();
        LinkedHashMap auton = HashMapManager.getAutonHashMap();
        LinkedHashMap teleop = HashMapManager.getTeleopHashMap();
        LinkedHashMap climb = HashMapManager.getClimbHashMap();

        // Setup Data
        QRString.append(setup.get("ScouterName")).append(",");
        QRString.append(setup.get("TeamNumber")).append(",");
        QRString.append(setup.get("MatchNumber")).append(",");
        QRString.append(setup.get("AlliancePartner1")).append(",");
        QRString.append(setup.get("AlliancePartner2")).append(",");
        QRString.append(setup.get("AllianceColor")).append(",");
        QRString.append(setup.get("PreloadCargo").equals("1") ? "Y" : "N").append(",");
        QRString.append(setup.get("NoShow").equals("1") ? "Y" : "N").append(",");
        QRString.append(setup.get("FellOver").equals("1") ? "Y" : "N").append(",");

        QRString.append(auton.get("Taxi").equals("1") ? "Y" : "N").append(",");
        QRString.append(climb.get("Rung")).append(",");


        // Event Data
        // Auton
        QRString.append("Auton").append(",");
        QRString.append(auton.get("ScoredUpper")).append(",");
        QRString.append(auton.get("ScoredLower")).append(",");
        QRString.append(auton.get("MissedUpper")).append(",");
        QRString.append(auton.get("MissedLower")).append(",");
        // Teleop
        QRString.append("Teleop").append(",");
        QRString.append(teleop.get("ScoredUpper")).append(",");
        QRString.append(teleop.get("ScoredLower")).append(",");
        QRString.append(teleop.get("MissedUpper")).append(",");
        QRString.append(teleop.get("MissedLower")).append(",");
    }

    public static String getQRString(){
        return QRString.toString();
    }

    public static void clearQRString(Context context) {
        HashMapManager.appendQRList(QRString.toString(), context);
        QRString = new StringBuilder();
    }
}
