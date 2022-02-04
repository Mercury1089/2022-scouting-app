package com.mercury1089.scoutingapp2019;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class HashMapManager{
    private static LinkedHashMap<String, String> settingsHashMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> setupHashMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> autonHashMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> teleopHashMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> climbHashMap = new LinkedHashMap<>();

    /**
     *
     * Enum for reference to each HashMap
     *
     */
    public enum HASH{
        SETTINGS, SETUP, AUTON, TELEOP, CLIMB, QRCODES
    }

    /**
    *
    * Used to access the setup HashMap from an activity
    *
     */
    public HashMapManager(){
        // Nothing to see here
    }

    /**
     *
     * Used to get the setttingsHashMap
     * Call when an activity starts and assign to global variable
     *
     */
    public static LinkedHashMap<String, String> getSettingsHashMap(){
        return settingsHashMap;
    }

    /**
    *
    * Used to get the setupHashMap
    * Call when an activity starts and assign to global variable
    *
     */
    public static LinkedHashMap<String, String> getSetupHashMap(){
        return setupHashMap;
    }

    /**
     *
     * Used to get the autonHashMap
     * Call when an activity starts and assign to global variable
     *
     */
    public static LinkedHashMap<String, String> getAutonHashMap(){
        return autonHashMap;
    }

    /**
     *
     * Used to get the teleopHashMap
     * Call when an activity starts and assign to global variable
     *
     */
    public static LinkedHashMap<String, String> getTeleopHashMap(){
        return teleopHashMap;
    }

    /**
     *
     * Used to get the climbHashMap
     * Call when an activity starts and assign to global variable
     *
     */
    public static LinkedHashMap<String, String> getClimbHashMap(){
        return climbHashMap;
    }

    /**
     *
     * Used to set the app wide settingsHashMap
     * Call before leaving an activity to update the app wide settingsHashMap
     *
     */
    public static void putSettingsHashMap(LinkedHashMap<String, String> settingsData){
        if(settingsData == null)
            return;
        settingsHashMap = settingsData;
    }

    /**
    *
    * Used to set the app wide setupHashMap
    * Call before leaving an activity to update the app wide setupHashMap
    *
     */
    public static void putSetupHashMap(LinkedHashMap<String, String> setupData){
        if(setupData == null) {
            return;
        }
        setupHashMap = setupData;
    }

    /**
     *
     * Used to set the app wide autonHashMap
     * Call before leaving an activity to update the app wide autonHashMap
     *
     */
    public static void putAutonHashMap(LinkedHashMap<String, String> autonData){
        if(autonData == null)
            return;
        autonHashMap = autonData;
    }

    /**
     *
     * Used to set the app wide teleopHashMap
     * Call before leaving an activity to update the app wide teleopHashMap
     *
     */
    public static void putTeleopHashMap(LinkedHashMap<String, String> teleopData){
        if(teleopData == null)
            return;
        teleopHashMap = teleopData;
    }

    /**
     *
     * Used to set the app wide climbHashMap
     * Call before leaving an activity to update the app wide climbHashMap
     *
     */
    public static void putClimbHashMap(LinkedHashMap<String, String> climbData){
        if(climbData == null)
            return;
        climbHashMap = climbData;
    }

    /**
     *
     */
    public static void appendQRList(String qrString, Context context){
        String[] qrList = setupQRList(context);
        String[] newList = new String[qrList.length + 1];
        Log.d("QRStuff", "" + qrList.length + " " + newList.length);
        for(int i = 0; i < qrList.length; i++)
            newList[i] = qrList[i];
        newList[newList.length - 1] = qrString;
        outputQRList(newList, context);
    }

    /**
     *
     */
    public static String[] setupQRList(Context context){
        String filename = "QRData";
        String[] qrList = new String[0];
        try {
            FileInputStream fs = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fs, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            int count = 0;
            while(line != null){
                Log.d("QRStuff3", "" + count);
                String[] tempList = qrList;
                qrList = new String[tempList.length + 1];
                for(int i = 0; i < tempList.length; i++)
                    qrList[i] = tempList[i];
                qrList[qrList.length-1] = line;
                line = reader.readLine();
                count ++;
            }
        } catch(Exception e){
            File file = new File(context.getFilesDir(), filename);
            try {
                file.createNewFile();
            }catch (Exception e1) {}
        }
        return qrList;
    }

    /**
     *
     */
    public static void outputQRList(String[] qrList, Context context){
        String filename = "QRData";
        try {
            File file = new File(context.getFilesDir(), filename);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fs = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs));
            for(String qrString : qrList){
                Log.d("QRStuff2", "" + qrList.length);
                bw.write(qrString);
                bw.newLine();
            }
            bw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] pullSettingsPassword(Context context){
        String filename = "SettingsPassword";
        String password, usePassword;
        try {
            FileInputStream fs = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fs, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            password = reader.readLine();
            usePassword = reader.readLine();
            if(password == null || usePassword == null){
                return new String[] {"", "N"};
            }
            return new String[] {password, usePassword};
        } catch(Exception e){
            File file = new File(context.getFilesDir(), filename);
            try {
                file.createNewFile();
            }catch (Exception e1) {}
            return null;
        }
    }

    public static void saveSettingsPassword(String[] passwordStuff, Context context){
        String filename = "SettingsPassword";
        try {
            String password = passwordStuff[0];
            String usePassword = passwordStuff[1];
            File file = new File(context.getFilesDir(), filename);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fs = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs));
            bw.write(password);
            bw.newLine();
            bw.write(usePassword);
            bw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *
    * Used to reset all the setupHashMap values to their default values
    * Fill in default values to prevent null pointer exceptions
    *
     */
    public static void setDefaultValues(HASH map){
        switch(map) {
            case SETTINGS:
                settingsHashMap.put("HashMapName", "Settings");
                settingsHashMap.put("NothingToSeeHere", "0");
                settingsHashMap.put("Slack", "0");
                settingsHashMap.put("DefaultPassword", "abc");
            case SETUP:
                setupHashMap.put("HashMapName", "Setup");
                setupHashMap.put("ScouterName", "");
                setupHashMap.put("MatchNumber", "");
                setupHashMap.put("TeamNumber", "");
                setupHashMap.put("NoShow", "0");
                setupHashMap.put("AlliancePartner1", "");
                setupHashMap.put("AlliancePartner2", "");
                setupHashMap.put("AllianceColor", "");
                setupHashMap.put("StartingPosition", "");
                setupHashMap.put("FellOver", "0");
                break;
            case AUTON:
                //include all the items that will be in the autonHashMap
                autonHashMap.put("HashMapName", "Auton");
                autonHashMap.put("NumberPickedUp", "0");
                autonHashMap.put("NumberDropped",  "0");
                autonHashMap.put("OuterPortScored", "0");
                autonHashMap.put("InnerPortScored", "0");
                autonHashMap.put("LowerPortScored", "0");
                autonHashMap.put("UpperPortMissed", "0");
                autonHashMap.put("LowerPortMissed", "0");
                autonHashMap.put("CrossedInitiationLine", "0");
                break;
            case TELEOP:
                //include all the items that will be in the teleopHashMap
                teleopHashMap.put("HashMapName", "Teleop");
                teleopHashMap.put("NumberPickedUp", "0");
                teleopHashMap.put("NumberDropped",  "0");
                teleopHashMap.put("OuterPortScored", "0");
                teleopHashMap.put("InnerPortScored", "0");
                teleopHashMap.put("LowerPortScored", "0");
                teleopHashMap.put("UpperPortMissed", "0");
                teleopHashMap.put("LowerPortMissed", "0");
                teleopHashMap.put("StageTwo", "0");
                teleopHashMap.put("StageThree", "0");
                break;
            case CLIMB:
                //include all the items that will be in the climbHashMap
                climbHashMap.put("HashMapName", "Climb");
                climbHashMap.put("CLP", "");
                break;
        }
    }

    /**
    *
    * Checks if the setupHashMap is empty or null
    * if it is null, it instantiates it and calls setDefaultValues()
    * if it is empty, it calls setDefaultValues()
    *
     */

    public static boolean checkNullOrEmpty(HASH map){
        switch(map){
            case SETTINGS:
                if(settingsHashMap == null)
                    settingsHashMap = new LinkedHashMap<>();
                if(settingsHashMap.isEmpty()) {
                    setDefaultValues(HASH.SETTINGS);
                    return true;
                }
                break;
            case SETUP:
                if(setupHashMap == null)
                    setupHashMap = new LinkedHashMap<>();
                if(setupHashMap.isEmpty()) {
                    setDefaultValues(HASH.SETUP);
                    return true;
                }
                break;
            case AUTON:
                if(autonHashMap == null)
                    autonHashMap = new LinkedHashMap<>();
                if(autonHashMap.isEmpty()) {
                    setDefaultValues(HASH.AUTON);
                    return true;
                }
                break;
            case TELEOP:
                if(teleopHashMap == null)
                    teleopHashMap = new LinkedHashMap<>();
                if(teleopHashMap.isEmpty()) {
                    setDefaultValues(HASH.TELEOP);
                    return true;
                }
                break;
            case CLIMB:
                if(climbHashMap == null)
                    climbHashMap = new LinkedHashMap<>();
                if(climbHashMap.isEmpty()) {
                    setDefaultValues(HASH.CLIMB);
                    return true;
                }
        }
        return false;
    }

    /**
     *
     * resets all of the values of all of the HashMaps except for scouterName
     * It also increments the match number
     *
     */

    public static void setupNextMatch(){
        String scouterName = setupHashMap.get("ScouterName");
        String matchNumber = setupHashMap.get("MatchNumber");
        String allianceColor = setupHashMap.get("AllianceColor");
        setDefaultValues(HASH.SETUP);
        setDefaultValues(HASH.AUTON);
        setDefaultValues(HASH.TELEOP);
        setDefaultValues(HASH.CLIMB);
        setupHashMap.put("ScouterName", scouterName);
        try {
            setupHashMap.put("MatchNumber", Integer.toString((Integer.parseInt(matchNumber) + 1)));
        } catch(NumberFormatException e){
            setupHashMap.put("MatchNumber", "0");
        }
        setupHashMap.put("AllianceColor", allianceColor);
    }
}
