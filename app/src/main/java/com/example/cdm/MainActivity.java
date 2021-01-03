package com.example.cdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button mButtonLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        mButtonLogout = (Button)findViewById(R.id.logoutButton);
        mAuth=FirebaseAuth.getInstance();
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(MainActivity.this,Login.class);
                SharedPreferences pref = getSharedPreferences("user_details", MODE_PRIVATE);
                if(pref.contains("Email")){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                    LoginManager.getInstance().logOut();
                }
                else{

                    mAuth.signOut();
                }
                startActivity(mintent);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences pref = getSharedPreferences("user_details", MODE_PRIVATE);
        if(currentUser == null && !pref.contains("Email"))
        {
            Intent login = new Intent(MainActivity.this,Login.class);
            startActivity(login);
            finish();
        }
        else
        {
            CheckUserExistence();
        }
        // drivesadapter.startListening();
    }
    private void CheckUserExistence()
    {
        final SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        final String username = preferences.getString("Username",null);

        Toast.makeText(MainActivity.this,username,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        if(userref==null)
        {
            Toast.makeText(MainActivity.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
            Intent Registration = new Intent(MainActivity.this,Registration.class);
            Registration.putExtra("Email",preferences.getString("Email",null));
            Registration.putExtra("Username",preferences.getString("Username",null));
            Registration.putExtra("Name",preferences.getString("Name",null));
            Registration.putExtra("Image",preferences.getString("Image",null));
            startActivity(Registration);
            finish();
        }
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(!dataSnapshot.hasChild(username))
                {
                    Toast.makeText(MainActivity.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
                    Intent Registration = new Intent(MainActivity.this,Registration.class);
                    Registration.putExtra("Email",preferences.getString("Email",null));
                    Registration.putExtra("Username",preferences.getString("Username",null));
                    Registration.putExtra("Name",preferences.getString("Name",null));
                    Registration.putExtra("Image",preferences.getString("Image",null));
                    startActivity(Registration);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Error ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
