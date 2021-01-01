package com.example.cdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                mAuth.signOut();
                startActivity(mintent);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            Intent login = new Intent(MainActivity.this,Login.class);
            startActivity(login);
        }
        else
        {
            CheckUserExistence();
        }
        // drivesadapter.startListening();
    }
    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        if(userref==null)
        {
            Toast.makeText(MainActivity.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
            Intent Registration = new Intent(MainActivity.this,Registration.class);
            startActivity(Registration);
        }
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(current_user_id))
                {
                     Toast.makeText(MainActivity.this,"Welcome to Robbinhood Army",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
                    Intent Registration = new Intent(MainActivity.this,Registration.class);
                    startActivity(Registration);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Error ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
