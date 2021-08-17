package com.example.smarthouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {
    public static final String PREFERENCES = "com.example.smarthouse.MyPrefs";
    public static final String USER_UID = "com.example.smarthouse.UserUid";

    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;

    private EditText txtEmail;
    private EditText txtPassword;
    private TextView txtPasswordHint;
    private Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Initializing SharedPreferences
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        // Initializing Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initializing Variables 
        txtEmail = findViewById(R.id.a1_edit_email);
        txtPassword = findViewById(R.id.a1_edit_password);
        txtPasswordHint = findViewById(R.id.a1_txt_password);
        btnSignin = findViewById(R.id.a1_btn_signin);

        // Initializing TabLayout 
        TabLayout tabLayout = findViewById(R.id.a1_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.equals(tabLayout.getTabAt(0))) {
                    btnSignin.setText(R.string.login);
                    txtPasswordHint.setVisibility(View.GONE);
                    btnSignin.setOnClickListener(v -> doLogin());
                } 
                else {
                    btnSignin.setText(R.string.signup);
                    txtPasswordHint.setVisibility(View.VISIBLE);
                    btnSignin.setOnClickListener(v -> doSignup());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Initializing Layout
        btnSignin.setText(R.string.login);
        txtPasswordHint.setVisibility(View.GONE);
        btnSignin.setOnClickListener(v -> doLogin());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            updateUI(currentUser);
        }
    }

    // Login
    public void doLogin() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } 
            else {
                // If sign in fails, display a message to the user.
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Signup
    public void doSignup() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(password.length() >= 8) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    createUser(user);
                    updateUI(user);
                } 
                else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        } 
        else {
            Toast.makeText(getApplicationContext(), "Password is too short.", Toast.LENGTH_SHORT).show();
        }
    }

    // Auxiliary to Login
    private void updateUI(FirebaseUser user) {
        if(user != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_UID, user.getUid());
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
         } 
         else {
            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Auxiliary function to create fields in Realtime Database
    private void createUser(FirebaseUser user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if(user != null) {
            String uid = user.getUid();

            mDatabase.child(uid).child("Brightness").setValue(0);
            mDatabase.child(uid).child("Values").child("Temperature").setValue(0);
            mDatabase.child(uid).child("LightSwitches").child("Room1").setValue(false);
            mDatabase.child(uid).child("LightSwitches").child("Room2").setValue(false);
            mDatabase.child(uid).child("LightSwitches").child("Room3").setValue(false);
            mDatabase.child(uid).child("LightSwitches").child("Room4").setValue(false);
            mDatabase.child(uid).child("LightSwitches").child("Room5").setValue(false);
            mDatabase.child(uid).child("AutomaticLights").child("SwitchAutomaticLights").setValue(true);
            mDatabase.child(uid).child("AutomaticLights").child("SwitchTime").setValue(true);
            mDatabase.child(uid).child("AutomaticLights").child("HoursOn").setValue(20);
            mDatabase.child(uid).child("AutomaticLights").child("MinutesOn").setValue(30);
            mDatabase.child(uid).child("AutomaticLights").child("HoursOff").setValue(20);
            mDatabase.child(uid).child("AutomaticLights").child("MinutesOff").setValue(30);
            mDatabase.child(uid).child("AutomaticLights").child("SwitchLight").setValue(true);
            mDatabase.child(uid).child("AutomaticLights").child("Light").setValue(30);
        } 
        else {
            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to SignOut
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}