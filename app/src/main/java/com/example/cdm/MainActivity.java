package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button mButtonLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile,profile;
    private TextView txtName, txtWebsite,textView;
    private  Toolbar toolbar;
    private FloatingActionButton fab;
    private Handler mHandler;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.opennavigationdrawer,
                R.string.closennavigationdrawer
        );
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        textView=header.findViewById(R.id.name);
        profile=header.findViewById(R.id.img_profile);

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
            //CheckUserExistence();
        }
        // drivesadapter.startListening();
    }
    private void CheckUserExistence()
    {
        final SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String temp = preferences.getString("Username",null);
        final  String username=temp.replace('.','1');
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
