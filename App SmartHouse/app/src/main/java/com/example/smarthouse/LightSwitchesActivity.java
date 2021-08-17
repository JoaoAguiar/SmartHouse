package com.example.smarthouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LightSwitchesActivity extends AppCompatActivity {
    private SharedPreferences shared_preferences;

    private String uid;

    private DatabaseReference database;

    private SwitchMaterial switch_room1;
    private SwitchMaterial switch_room2;
    private SwitchMaterial switch_room3;
    private SwitchMaterial switch_room4;
    private SwitchMaterial switch_room5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_switches);

        // Initializing SharedPreferences
        shared_preferences = getSharedPreferences(SigninActivity.PREFERENCES, MODE_PRIVATE);
        uid = shared_preferences.getString(SigninActivity.USER_UID, null);

        // Initializing Database
        database = FirebaseDatabase.getInstance().getReference(uid + "/LightSwitches");

        database.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean r1 = dataSnapshot.child("Room1").getValue(Boolean.class);
                boolean r2 = dataSnapshot.child("Room2").getValue(Boolean.class);
                boolean r3 = dataSnapshot.child("Room3").getValue(Boolean.class);
                boolean r4 = dataSnapshot.child("Room4").getValue(Boolean.class);
                boolean r5 = dataSnapshot.child("Room5").getValue(Boolean.class);

                switch_room1 = findViewById(R.id.a3_switch_room1);
                switch_room2 = findViewById(R.id.a3_switch_room2);
                switch_room3 = findViewById(R.id.a3_switch_room3);
                switch_room4 = findViewById(R.id.a3_switch_room4);
                switch_room5 = findViewById(R.id.a3_switch_room5);

                switch_room1.setChecked(r1);
                switch_room2.setChecked(r2);
                switch_room3.setChecked(r3);
                switch_room4.setChecked(r4);
                switch_room5.setChecked(r5);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("LightSwitches", "Failed to read value.", error.toException());
            }
        });

        // Initializing Toolbar
        MaterialToolbar topAppBar = findViewById(R.id.a3_topAppBar);

        topAppBar.setNavigationOnClickListener (v -> finish());
    }

    // Function for setting light switches values on firebase
    public void setValues(View view) {
        SwitchMaterial swc = findViewById(view.getId());

        if(switch_room1.equals(swc)) {
            if (swc.isChecked()) {
                database.child("Room1").setValue(true);
            } 
            else {
                database.child("Room1").setValue(false);
            }
        } 
        else if(switch_room2.equals(swc)) {
            if(swc.isChecked()) {
                database.child("Room2").setValue(true);
            } 
            else {
                database.child("Room2").setValue(false);
            }
        } 
        else if(switch_room3.equals(swc)) {
            if(swc.isChecked()) {
                database.child("Room3").setValue(true);
            } 
            else {
                database.child("Room3").setValue(false);
            }
        } 
        else if(switch_room4.equals(swc)) {
            if(swc.isChecked()) {
                database.child("Room4").setValue(true);
            } 
            else {
                database.child("Room4").setValue(false);
            }
        } 
        else if (switch_room5.equals(swc)) {
            if(swc.isChecked()) {
                database.child("Room5").setValue(true);
            } 
            else {
                database.child("Room5").setValue(false);
            }
        }
    }
}