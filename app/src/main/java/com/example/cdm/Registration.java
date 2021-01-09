package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private EditText mTextPhno;
    private EditText mname;
    private TextView mUsername;
    private EditText memail;
    private Button mButtonRegister;
    String currentuserid;
    private FirebaseAuth mAuth;
    private String email,image,username,name;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private Context context;
    private DatabaseReference userref,chapterref,tokenref;
    String Chapter;
    public String TOPIC_TO_SUBSCRIBE;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;
    final static int Gallery_Pick = 1;
    private CircularImageView profile;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_registration);
        mTextPhno = findViewById(R.id.uphno);
        mname = findViewById(R.id.uname);
        mUsername = findViewById(R.id.uuid);
        profile = (CircularImageView) findViewById(R.id.profilepic);

        mButtonRegister = findViewById(R.id.ureg);
        memail = findViewById(R.id.uemail);
        SharedPreferences pref = getSharedPreferences("user_details", MODE_PRIVATE);
        final String chapter=pref.getString("Chapter",null);
        Toast.makeText(Registration.this, image, Toast.LENGTH_SHORT).show();
        chapterref = FirebaseDatabase.getInstance().getReference().child("User").child(chapter);

        if (extras != null) {
            image = extras.getString("Image");
            Toast.makeText(Registration.this, image, Toast.LENGTH_SHORT).show();
            name=extras.getString("Name");
            username=extras.getString("Username");
            Log.v("Abc",username);
            email=extras.getString("Email");
            CheckUserExistence(username);
            memail.setText(email);
            mUsername.setText(username);
            mname.setText(name);
            if(image!=null){
                final HashMap hashMap = new HashMap();

                hashMap.put("Image",image);
                chapterref.child(username).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Registration.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();

                    }
                });
            }


        }
        mAuth = FirebaseAuth.getInstance();
       // currentuserid = mAuth.getCurrentUser().getUid();
        email = memail.getText().toString();

        tokenref = FirebaseDatabase.getInstance().getReference().child("Tokens");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mname.getText().toString();
                final String user = mUsername.getText().toString();
                String phone=mTextPhno.getText().toString();
                String email = memail.getText().toString();
                int  x =0;
                //phone = "+" + codePicker.getSelectedCountryCode() + phone;
                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(Registration.this, "Please write your Name ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(user))
                {
                    Toast.makeText(Registration.this, "Please write your Userid...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(Registration.this, "Please confirm your Phoneno....", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    getlocation();
                    loadingbar.setTitle("Saving User data");
                    loadingbar.setMessage("Please Wait ");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);
                    final HashMap usermap = new HashMap();


                    usermap.put("Name",name);
                    usermap.put("Username",user);
                    usermap.put("Phoneno",phone);
                    usermap.put("Email",email);
                    usermap.put("drives",String.valueOf(x));
                    usermap.put("Smiles",String.valueOf(x));
                    chapterref.child(username).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Registration.this, "Registration Completed Sucessfully", Toast.LENGTH_SHORT).show();
                                getlocation();
                                // mAuth.signOut();

                                Intent movetomain = new Intent(Registration.this,MainActivity.class);
                                startActivity(movetomain);
                                finish();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Registration.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });


                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
      chapterref.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("Profile"))
                    {
                        String image = dataSnapshot.child("Profile").getValue().toString();
                        Toast.makeText(Registration.this,image,Toast.LENGTH_SHORT).show();
                        Picasso.get().load(image).into(profile);
                    }
                    else
                    {
                        Toast.makeText(Registration.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                        profile.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

                     CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we updating your profile image...");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                Toast.makeText(this,username,Toast.LENGTH_SHORT).show();
                final StorageReference filePath = UserProfileImageRef.child(username + ".jpg");
                Toast.makeText(this,filePath.getPath(),Toast.LENGTH_SHORT).show();
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap hashmap = new HashMap();
                                    hashmap.put("Profile Img",String.valueOf(uri));
                                    chapterref.child(username).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Registration.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();

                                            }
                                            else
                                            {

                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }
    private void getlocation(){
        if(ActivityCompat.checkSelfPermission(Registration.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(Registration.this, "Permission granted", Toast.LENGTH_SHORT).show();
            getuserlocation();
        }
        else{
            ActivityCompat.requestPermissions(Registration.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            getlocation();
            //Toast.makeText(Registration.this, "Click get location button again", Toast.LENGTH_SHORT).show();
        }
    }
    private void getuserlocation() {
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location!=null){
                    try {
                        loadingbar.setTitle("Location");
                        loadingbar.setMessage("Please wait, while we updating your location...");
                        loadingbar.show();
                        loadingbar.setCanceledOnTouchOutside(true);
                        HashMap hashMap=new HashMap();
                        Geocoder geocoder=new Geocoder(Registration.this, Locale.getDefault());
                        List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        hashMap.put("Latitude",addresses.get(0).getLatitude());
                        hashMap.put("Longitude",addresses.get(0).getLongitude());
                        hashMap.put("Address",addresses.get(0).getAddressLine(0));
                        hashMap.put("Chapter",addresses.get(0).getLocality());
                        Chapter=addresses.get(0).getLocality();
                        chapterref.child(username).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(Registration.this, "Location Saved Successfully...", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }
        });
    }
    private void CheckUserExistence(final String image)
    {

        Toast.makeText(Registration.this,image,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        if(chapterref.child(username)==null)
        {
            Toast.makeText(Registration.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
        }
       else{
            chapterref.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.hasChild(image))
                    {
                        Toast.makeText(Registration.this,"Together we can!",Toast.LENGTH_SHORT).show();
                        Intent toMain = new Intent(Registration.this,MainActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Enter Your Data",Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Registration.this,"Error ",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
