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

        //Setup Data
        QRString.append(setup.get("ScouterName")).append(",");
        QRString.append(setup.get("TeamNumber")).append(",");
        QRString.append(setup.get("MatchNumber")).append(",");
        QRString.append(setup.get("AlliancePartner1")).append(",");
        QRString.append(setup.get("AlliancePartner2")).append(",");
        QRString.append(setup.get("AllianceColor")).append(",");
        QRString.append(setup.get("NoShow").equals("1") ? "Y" : "N").append(",");
        String startingPos = (String)setup.get("StartingPosition");
        if(startingPos != null && startingPos.length() >= 2)
            QRString.append(startingPos.substring(1)).append(",");
        else
            QRString.append(startingPos).append(",");
        QRString.append(auton.get("CrossedInitiationLine").equals("1") ? "Y" : "N").append(",");
        QRString.append(teleop.get("StageTwo").equals("1") ? "Y" : "N").append(",");
        QRString.append(teleop.get("StageThree").equals("1") ? "Y" : "N").append(",");
        QRString.append(climb.get("CLP")).append(",");
        QRString.append(setup.get("FellOver").equals("1") ? "Y" : "N").append(",");

        //Event Data
        //Auton
        QRString.append("Auton").append(",");
        QRString.append(auton.get("InnerPortScored")).append(",");
        QRString.append(auton.get("OuterPortScored")).append(",");
        QRString.append(auton.get("LowerPortScored")).append(",");
        QRString.append(auton.get("UpperPortMissed")).append(",");
        QRString.append(auton.get("LowerPortMissed")).append(",");
        QRString.append(auton.get("NumberDropped")).append(",");
        //Teleop
        QRString.append("Teleop").append(",");
        QRString.append(teleop.get("InnerPortScored")).append(",");
        QRString.append(teleop.get("OuterPortScored")).append(",");
        QRString.append(teleop.get("LowerPortScored")).append(",");
        QRString.append(teleop.get("UpperPortMissed")).append(",");
        QRString.append(teleop.get("LowerPortMissed")).append(",");
        QRString.append(teleop.get("NumberDropped")).append(",");
    }

    public static String getQRString(){
        return QRString.toString();
    }

    public static void clearQRString(Context context) {
        HashMapManager.appendQRList(QRString.toString(), context);
        QRString = new StringBuilder();
    }
}
