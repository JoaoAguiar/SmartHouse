package com.example.smarthouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AutomaticLightsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String uid;

    private DatabaseReference mDatabase;

    private SeekBar seekBar;

    private boolean swcAL;
    private boolean swcT;
    private boolean swcL;

    private int hourOn = 0;
    private int minOn = 0;
    private int hourOff = 0;
    private int minOff = 0;
    private int light = 0;

    private SwitchMaterial swcAutoLights;
    private SwitchMaterial swcTime;
    private SwitchMaterial swcLight;

    private LinearLayout llAutoLights;
    private LinearLayout llTime;
    private TextView tvTimeOn;
    private TextView tvTimeOff;
    private LinearLayout llLight;
    private TextView tvLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_lights);

        // Initializing SharedPreferences
        sharedPreferences = getSharedPreferences(SigninActivity.PREFERENCES, MODE_PRIVATE);
        uid = sharedPreferences.getString(SigninActivity.USER_UID, null);

        // Initializing Database
        mDatabase = FirebaseDatabase.getInstance().getReference(uid + "/AutomaticLights");
        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swcAL = dataSnapshot.child("SwitchAutomaticLights").getValue(Boolean.class);
                swcT = dataSnapshot.child("SwitchTime").getValue(Boolean.class);
                swcL = dataSnapshot.child("SwitchLight").getValue(Boolean.class);

                hourOn = dataSnapshot.child("HoursOn").getValue(Integer.class);
                minOn = dataSnapshot.child("MinutesOn").getValue(Integer.class);
                hourOff = dataSnapshot.child("HoursOff").getValue(Integer.class);
                minOff = dataSnapshot.child("MinutesOff").getValue(Integer.class);
                light = dataSnapshot.child("Light").getValue(Integer.class);

                swcAutoLights.setChecked(swcAL);
                swcTime.setChecked(swcT);
                swcLight.setChecked(swcL);

                setTime(hourOn, minOn, tvTimeOn);
                setTime(hourOff, minOff, tvTimeOff);
                tvLight.setText(light + "%");
                seekBar.setProgress(light);

                if(swcAL) {
                    llAutoLights.setVisibility(View.VISIBLE);
                    if(swcT && swcL) {
                        llTime.setVisibility(View.VISIBLE);
                        llLight.setVisibility(View.VISIBLE);
                    } 
                    else if(swcT) {
                        llTime.setVisibility(View.VISIBLE);
                        llLight.setVisibility(View.GONE);
                    } 
                    else {
                        llTime.setVisibility(View.GONE);
                        llLight.setVisibility(View.VISIBLE);
                    }
                } 
                else {
                    llAutoLights.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("AutomaticLights", "Failed to read value.", error.toException());
            }
        });

        // Initializing Variables
        swcAutoLights = findViewById(R.id.a4_swc_autolights);
        swcTime = findViewById(R.id.a4_swc_time);
        swcLight = findViewById(R.id.a4_swc_light);

        llAutoLights = findViewById(R.id.a4_ll_autolights);
        llTime = findViewById(R.id.a4_ll_time);
        tvTimeOn = findViewById(R.id.a4_text_time_on);
        tvTimeOff = findViewById(R.id.a4_text_time_off);
        llLight = findViewById(R.id.a4_ll_light);
        tvLight = findViewById(R.id.a4_text_light);

        // Initializing Toolbar
        MaterialToolbar topAppBar = findViewById(R.id.a4_topAppBar);
        topAppBar.setNavigationOnClickListener (v -> finish());

        // Initializing SeekBar
        seekBar = findViewById(R.id.a4_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLight.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabase.child("Light").setValue(seekBar.getProgress());
            }
        });
    }

    // Function for formatting time to text 
    private void setTime(int h, int m, TextView t) {
        if(h < 10) {
            if(m < 10) {
                t.setText("0" + h + ":0" + m);
            } 
            else {
                t.setText("0" + h + ":" + m);
            }
        } 
        else if(m < 10) {
            t.setText(h + ":0" + m);
        } 
        else {
            t.setText(h + ":" + m);
        }
    }

    // Function for turning on and off each switch
    public void setVisibility(View view) {
        SwitchMaterial swc = findViewById(view.getId());

        if(swcAutoLights.equals(swc)) {
            if(swc.isChecked()) {
                if(!swcTime.isChecked() && !swcLight.isChecked()) {
                    mDatabase.child("SwitchTime").setValue(true);
                    mDatabase.child("SwitchLight").setValue(true);
                }

                mDatabase.child("SwitchAutomaticLights").setValue(true);
            } 
            else {
                mDatabase.child("SwitchAutomaticLights").setValue(false);
            }
        } 
        else if(swcTime.equals(swc)) {
            if(swc.isChecked()) {
                mDatabase.child("SwitchTime").setValue(true);
            } 
            else {
                mDatabase.child("SwitchTime").setValue(false);
            }
        } 
        else if(swcLight.equals(swc)) {
            if(swc.isChecked()) {
                mDatabase.child("SwitchLight").setValue(true);
            } 
            else {
                mDatabase.child("SwitchLight").setValue(false);
            }
        }

        if(!swcTime.isChecked() && !swcLight.isChecked() && swcAL) {
            mDatabase.child("SwitchAutomaticLights").setValue(false);
        }
    }

    // Function for setting up the TimePickerOn
    public void showMaterialTimePickerOn(View view) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hourOn)
            .setMinute(minOn)
            .setTitleText("Select time:")
            .build();

        picker.show(getSupportFragmentManager(), "timePickerOn");
        picker.addOnPositiveButtonClickListener(v -> {
            hourOn = picker.getHour();
            minOn = picker.getMinute();

            setTime(hourOn, minOn, tvTimeOn);

            mDatabase.child("HoursOn").setValue(hourOn);
            mDatabase.child("MinutesOn").setValue(minOn);
        });
    }

    // Function for setting up the TimePickerOff
    public void showMaterialTimePickerOff(View view) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hourOff)
            .setMinute(minOff)
            .setTitleText("Select time:")
            .build();

        picker.show(getSupportFragmentManager(), "timePickerOff");
        picker.addOnPositiveButtonClickListener(v -> {
            hourOff = picker.getHour();
            minOff = picker.getMinute();

            setTime(hourOff, minOff, tvTimeOff);

            mDatabase.child("HoursOff").setValue(hourOff);
            mDatabase.child("MinutesOff").setValue(minOff);
        });
    }
}