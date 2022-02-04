package com.mercury1089.scoutingapp2019;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mercury1089.scoutingapp2019.utils.GenUtils;
import java.util.LinkedHashMap;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Teleop extends Fragment {
    //HashMaps for sending QR data between screens
    private LinkedHashMap<String, String> setupHashMap;
    private LinkedHashMap<String, String> teleopHashMap;

    //RadioButtons
    private ImageButton pickedUpIncrementButton;
    private ImageButton pickedUpDecrementButton;
    private ImageButton droppedIncrementButton;
    private ImageButton droppedDecrementButton;
    private Button scoredButton;
    private Button missedButton;
    private Button nextButton;

    //Switches
    private Switch stageTwoButton;
    private Switch stageThreeButton;
    private Switch fellOverSwitch;

    //TextViews
    private TextView possessionID;
    private TextView possessionDescription;
    private TextView pickedUpID;
    private TextView pickedUpCounter;
    private TextView droppedID;
    private TextView droppedCounter;

    private TextView scoringID;
    private TextView scoringDescription;
    private TextView scoredCounter;
    private TextView missedCounter;

    private TextView controlPanelID;
    private TextView controlPanelDescription;
    private TextView stageTwoID;
    private TextView stageThreeID;

    private TextView miscID;
    private TextView miscDescription;

    private TextView fellOverID;

    //other variables
    private ConstraintLayout constraintLayout;
    private int totalScored;
    private int totalMissed;
    private boolean scoredReTap;
    private boolean missedReTap;

    public static Teleop newInstance() {
        Teleop fragment = new Teleop();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private MatchActivity context;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = (MatchActivity) getActivity();
        return inflater.inflate(R.layout.fragment_teleop, container, false);
    }

    public void onStart(){
        super.onStart();

        //linking variables to XML elements on the screen
        possessionID = getView().findViewById(R.id.IDPossession);
        possessionDescription = getView().findViewById(R.id.IDPossessionDirections);
        pickedUpID = getView().findViewById(R.id.IDPickedUp);
        pickedUpIncrementButton = getView().findViewById(R.id.PickedUpButton);
        pickedUpDecrementButton = getView().findViewById(R.id.NotPickedUpButton);
        pickedUpCounter = getView().findViewById(R.id.PickedUpCounter);

        droppedID = getView().findViewById(R.id.IDDropped);
        droppedIncrementButton = getView().findViewById(R.id.DroppedButton);
        droppedDecrementButton = getView().findViewById(R.id.NotDroppedButton);
        droppedCounter = getView().findViewById(R.id.DroppedCounter);

        scoringID = getView().findViewById(R.id.IDScoring);
        scoringDescription = getView().findViewById(R.id.IDScoringDirections);
        scoredButton = getView().findViewById(R.id.ScoredButton);
        scoredCounter = getView().findViewById(R.id.ScoredCounter);
        missedButton = getView().findViewById(R.id.MissedButton);
        missedCounter = getView().findViewById(R.id.MissedCounter);

        controlPanelID = getView().findViewById(R.id.IDControlPanel);
        controlPanelDescription = getView().findViewById(R.id.IDControlPanelDirections);
        stageTwoButton = getView().findViewById(R.id.Stage2Switch);
        stageTwoID = getView().findViewById(R.id.IDStage2);
        stageThreeButton = getView().findViewById(R.id.Stage3Switch);
        stageThreeID = getView().findViewById(R.id.IDStage3);

        miscID = getView().findViewById(R.id.IDMisc);
        miscDescription = getView().findViewById(R.id.IDMiscDirections);
        fellOverSwitch = getView().findViewById(R.id.FellOverSwitch);
        fellOverID = getView().findViewById(R.id.IDFellOver);

        nextButton = getView().findViewById(R.id.NextClimbButton);

        //set listeners for buttons
        pickedUpIncrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                int currentCount = Integer.parseInt((String)pickedUpCounter.getText());
                currentCount++;
                teleopHashMap.put("NumberPickedUp", String.valueOf(currentCount));
                updateXMLObjects();
            }
        });

        pickedUpDecrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                int currentCount = Integer.parseInt((String)pickedUpCounter.getText());
                if(currentCount > 0)
                    currentCount--;
                teleopHashMap.put("NumberPickedUp", String.valueOf(currentCount));
                updateXMLObjects();
            }
        });

        droppedIncrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                int currentCount = Integer.parseInt((String)droppedCounter.getText());
                currentCount++;
                teleopHashMap.put("NumberDropped", String.valueOf(currentCount));
                updateXMLObjects();
            }
        });

        droppedDecrementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                int currentCount = Integer.parseInt((String)droppedCounter.getText());
                if(currentCount > 0)
                    currentCount--;
                teleopHashMap.put("NumberDropped", String.valueOf(currentCount));
                updateXMLObjects();
            }
        });

        scoredButton.setOnClickListener(new View.OnClickListener() {
            // Buttons
            private Button doneButton;
            private Button cancelButton;
            private ImageButton outerIncrement;
            private ImageButton outerDecrement;
            private ImageButton innerIncrement;
            private ImageButton innerDecrement;
            private ImageButton lowerIncrement;
            private ImageButton lowerDecrement;

            // TextViews
            private TextView outerScore;
            private TextView innerScore;
            private TextView lowerScore;

            // On Cancel Variables
            private String oldOuterScore;
            private String oldInnerScore;
            private String oldLowerScore;

            public void onClick(View view){
                if(scoredReTap){
                    scoredReTap = false;
                    return;
                }

                possessionButtonsEnabledState(false);
                controlPanelButtonsEnabledState(false);
                miscButtonsEnabledState(false);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_scored_down, null);

                int width = (int)getResources().getDimension(R.dimen.scoring_popup_width);
                int height = (int)getResources().getDimension(R.dimen.scoring_popup_height);

                PopupWindow popupWindow = new PopupWindow(popupView, width, height);

                // required*
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                // *required

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        scoredButton.setSelected(false);
                        possessionButtonsEnabledState(true);
                        controlPanelButtonsEnabledState(true);
                        miscButtonsEnabledState(true);
                        updateObjects();
                        updateXMLObjects();
                    }
                });

                popupWindow.showAsDropDown(scoredButton);
                scoredButton.setSelected(true);

                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(0 <= event.getX() && event.getX() <= scoredButton.getWidth() &&
                                -scoredButton.getHeight() <= event.getY() && event.getY() <= 0) {
                            scoredReTap = true;
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                // Buttons
                doneButton = popupView.findViewById(R.id.DoneButton);
                cancelButton = popupView.findViewById(R.id.CancelButton);
                outerIncrement = popupView.findViewById(R.id.OuterIncrement);
                outerDecrement = popupView.findViewById(R.id.OuterDecrement);
                innerIncrement = popupView.findViewById(R.id.InnerIncrement);
                innerDecrement = popupView.findViewById(R.id.InnerDecrement);
                lowerIncrement = popupView.findViewById(R.id.LowerIncrement);
                lowerDecrement = popupView.findViewById(R.id.LowerDecrement);

                // Counter TextBoxes
                outerScore = popupView.findViewById(R.id.OuterScore);
                innerScore = popupView.findViewById(R.id.InnerScore);
                lowerScore = popupView.findViewById(R.id.LowerScore);

                oldOuterScore = teleopHashMap.get("OuterPortScored");
                oldInnerScore = teleopHashMap.get("InnerPortScored");
                oldLowerScore = teleopHashMap.get("LowerPortScored");

                updateObjects();

                outerIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("OuterPortScored", Integer.toString(Integer.parseInt((String)outerScore.getText()) + 1));
                        updateObjects();
                    }
                });

                outerDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int outerPortNum = Integer.parseInt((String)outerScore.getText());
                        teleopHashMap.put("OuterPortScored", outerPortNum > 0 ? Integer.toString(outerPortNum - 1) : Integer.toString(outerPortNum));
                        updateObjects();
                    }
                });

                innerIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("InnerPortScored", Integer.toString(Integer.parseInt((String)innerScore.getText()) + 1));
                        updateObjects();
                    }
                });

                innerDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int innerPortNum = Integer.parseInt((String)innerScore.getText());
                        teleopHashMap.put("InnerPortScored", innerPortNum > 0 ? Integer.toString(innerPortNum - 1) : Integer.toString(innerPortNum));
                        updateObjects();
                    }
                });

                lowerIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("LowerPortScored", Integer.toString(Integer.parseInt((String)lowerScore.getText()) + 1));
                        updateObjects();
                    }
                });

                lowerDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int lowerPortNum = Integer.parseInt((String)lowerScore.getText());
                        teleopHashMap.put("LowerPortScored", lowerPortNum > 0 ? Integer.toString(lowerPortNum - 1) : Integer.toString(lowerPortNum));
                        updateObjects();
                    }
                });

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("OuterPortScored", oldOuterScore);
                        teleopHashMap.put("InnerPortScored", oldInnerScore);
                        teleopHashMap.put("LowerPortScored", oldLowerScore);
                        popupWindow.dismiss();
                    }
                });
            }

            private void updateObjects(){
                String outerPortNum = teleopHashMap.get("OuterPortScored");
                String innerPortNum = teleopHashMap.get("InnerPortScored");
                String lowerPortNum = teleopHashMap.get("LowerPortScored");

                outerScore.setText(GenUtils.padLeftZeros(outerPortNum, 3));
                innerScore.setText(GenUtils.padLeftZeros(innerPortNum, 3));
                lowerScore.setText(GenUtils.padLeftZeros(lowerPortNum, 3));

                if(Integer.parseInt(outerPortNum) <= 0)
                    outerDecrement.setEnabled(false);
                else
                    outerDecrement.setEnabled(true);

                if(Integer.parseInt(innerPortNum) <= 0)
                    innerDecrement.setEnabled(false);
                else
                    innerDecrement.setEnabled(true);

                if(Integer.parseInt(lowerPortNum) <= 0)
                    lowerDecrement.setEnabled(false);
                else
                    lowerDecrement.setEnabled(true);

                totalScored = Integer.parseInt(outerPortNum) + Integer.parseInt(innerPortNum) + Integer.parseInt(lowerPortNum);
                scoredCounter.setText(GenUtils.padLeftZeros(Integer.toString(totalScored), 3));
            }
        });

        missedButton.setOnClickListener(new View.OnClickListener() {
            // Buttons
            private Button doneButton;
            private Button cancelButton;
            private ImageButton higherIncrement;
            private ImageButton higherDecrement;
            private ImageButton lowerDecrement;
            private ImageButton lowerIncrement;

            // TextViews
            private TextView higherScore;
            private TextView lowerScore;

            // On Cancel Variables
            private String oldHigherScore;
            private String oldLowerScore;

            public void onClick(View view){
                if(missedReTap){
                    missedReTap = false;
                    return;
                }

                possessionButtonsEnabledState(false);
                controlPanelButtonsEnabledState(false);
                miscButtonsEnabledState(false);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_missed_down, null);

                int width = (int)getResources().getDimension(R.dimen.missed_popup_width);
                int height = (int)getResources().getDimension(R.dimen.missed_popup_height);

                PopupWindow popupWindow = new PopupWindow(popupView, width, height);

                // required*
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                // *required

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        missedButton.setSelected(false);
                        possessionButtonsEnabledState(true);
                        controlPanelButtonsEnabledState(false);
                        miscButtonsEnabledState(true);
                        updateObjects();
                        updateXMLObjects();
                    }
                });

                popupWindow.showAsDropDown(missedButton);
                missedButton.setSelected(true);

                popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(0 <= event.getX() && event.getX() <= missedButton.getWidth() &&
                                -missedButton.getHeight() <= event.getY() && event.getY() <= 0) {
                            missedReTap = true;
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                // Buttons
                doneButton = popupView.findViewById(R.id.DoneButton);
                cancelButton = popupView.findViewById(R.id.CancelButton);
                higherIncrement = popupView.findViewById(R.id.UpperIncrement);
                higherDecrement = popupView.findViewById(R.id.UpperDecrement);
                lowerIncrement = popupView.findViewById(R.id.LowerIncrement);
                lowerDecrement = popupView.findViewById(R.id.LowerDecrement);

                // Counter TextBoxes
                higherScore = popupView.findViewById(R.id.UpperScore);
                lowerScore = popupView.findViewById(R.id.LowerScore);

                oldHigherScore = teleopHashMap.get("UpperPortMissed");
                oldLowerScore = teleopHashMap.get("LowerPortMissed");

                updateObjects();

                higherIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("UpperPortMissed", Integer.toString(Integer.parseInt((String)higherScore.getText()) + 1));
                        updateObjects();
                    }
                });

                higherDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int higherPortNum = Integer.parseInt((String)higherScore.getText());
                        teleopHashMap.put("UpperPortMissed", higherPortNum > 0 ? Integer.toString(higherPortNum - 1) : Integer.toString(higherPortNum));
                        updateObjects();
                    }
                });

                lowerIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("LowerPortMissed", Integer.toString(Integer.parseInt((String)lowerScore.getText()) + 1));
                        updateObjects();
                    }
                });

                lowerDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int lowerPortNum = Integer.parseInt((String)lowerScore.getText());
                        teleopHashMap.put("LowerPortMissed", lowerPortNum > 0 ? Integer.toString(lowerPortNum - 1) : Integer.toString(lowerPortNum));
                        updateObjects();
                    }
                });

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teleopHashMap.put("UpperPortMissed", oldHigherScore);
                        teleopHashMap.put("LowerPortMissed", oldLowerScore);
                        popupWindow.dismiss();
                    }
                });
            }

            private void updateObjects(){
                String higherPortNum = teleopHashMap.get("UpperPortMissed");
                String lowerPortNum = teleopHashMap.get("LowerPortMissed");

                higherScore.setText(GenUtils.padLeftZeros(higherPortNum, 3));
                lowerScore.setText(GenUtils.padLeftZeros(lowerPortNum, 3));

                if(Integer.parseInt(higherPortNum) <= 0)
                    higherDecrement.setEnabled(false);
                else
                    higherDecrement.setEnabled(true);

                if(Integer.parseInt(lowerPortNum) <= 0)
                    lowerDecrement.setEnabled(false);
                else
                    lowerDecrement.setEnabled(true);

                totalMissed = Integer.parseInt(higherPortNum) + Integer.parseInt(lowerPortNum);
                missedCounter.setText(GenUtils.padLeftZeros(Integer.toString(totalMissed), 3));
            }
        });

        stageTwoButton.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                teleopHashMap.put("StageTwo", isChecked ? "1" : "0");
                updateXMLObjects();
            }
        });

        stageThreeButton.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                teleopHashMap.put("StageThree", isChecked ? "1" : "0");
                updateXMLObjects();
            }
        });

        fellOverSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupHashMap.put("FellOver", isChecked ? "1" : "0");
                updateXMLObjects();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.tabs.getTabAt(2).select();
            }
        });
    }

    private void possessionButtonsEnabledState(boolean enable){
        possessionID.setEnabled(enable);
        possessionDescription.setEnabled(enable);

        pickedUpID.setEnabled(enable);
        pickedUpIncrementButton.setEnabled(enable);
        pickedUpDecrementButton.setEnabled(enable);
        pickedUpCounter.setEnabled(enable);

        droppedID.setEnabled(enable);
        droppedIncrementButton.setEnabled(enable);
        droppedDecrementButton.setEnabled(enable);
        droppedCounter.setEnabled(enable);
    }

    private void scoringButtonsEnabledState(boolean enable){
        scoringID.setEnabled(enable);
        scoringDescription.setEnabled(enable);

        scoredButton.setEnabled(enable);
        scoredCounter.setEnabled(enable);

        missedButton.setEnabled(enable);
        missedCounter.setEnabled(enable);
    }

    private void controlPanelButtonsEnabledState(boolean enable){
        controlPanelID.setEnabled(enable);
        controlPanelDescription.setEnabled(enable);

        stageTwoButton.setEnabled(enable);
        stageTwoID.setEnabled(enable);

        stageThreeButton.setEnabled(enable);
        stageThreeID.setEnabled(enable);
    }

    private void miscButtonsEnabledState(boolean enable){
        miscID.setEnabled(enable);
        miscDescription.setEnabled(enable);
        fellOverSwitch.setEnabled(enable);
        fellOverID.setEnabled(enable);
        nextButton.setEnabled(enable);
    }

    private void allButtonsEnabledState(boolean enable){
        possessionButtonsEnabledState(enable);
        scoringButtonsEnabledState(enable);
        controlPanelButtonsEnabledState(enable);
    }

    private void updateXMLObjects(){
        scoredCounter.setText(GenUtils.padLeftZeros(Integer.toString(totalScored), 3));
        missedCounter.setText(GenUtils.padLeftZeros(Integer.toString(totalMissed), 3));
        pickedUpCounter.setText(GenUtils.padLeftZeros(teleopHashMap.get("NumberPickedUp"), 3));
        droppedCounter.setText(GenUtils.padLeftZeros(teleopHashMap.get("NumberDropped"), 3));
        stageTwoButton.setChecked(teleopHashMap.get("StageTwo").equals("1"));
        stageThreeButton.setChecked(teleopHashMap.get("StageThree").equals("1"));

        if(setupHashMap.get("FellOver").equals("1")) {
            fellOverSwitch.setChecked(true);
            allButtonsEnabledState(false);
        } else {
            fellOverSwitch.setChecked(false);
            allButtonsEnabledState(true);
            if(Integer.parseInt((String)pickedUpCounter.getText()) == 0)
                pickedUpDecrementButton.setEnabled(false);
            else
                pickedUpDecrementButton.setEnabled(true);
            if(Integer.parseInt((String)droppedCounter.getText()) == 0)
                droppedDecrementButton.setEnabled(false);
            else
                droppedDecrementButton.setEnabled(true);
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
                teleopHashMap = HashMapManager.getTeleopHashMap();
                updateXMLObjects();
                //set all objects in the fragment to their values from the HashMaps
            } else {
                HashMapManager.putSetupHashMap(setupHashMap);
                HashMapManager.putTeleopHashMap(teleopHashMap);
            }
        }
    }
}
