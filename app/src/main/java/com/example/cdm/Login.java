package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    LoginButton mFblogin;
    TextView mTextViewRegister;
    private static CheckBox show_hide_password;
    private FirebaseAuth mAuth;
    private Boolean emailaddresschecker;
    private ProgressDialog loadingBar;
    SharedPreferences pref;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_login);

        mTextUsername = (EditText)findViewById(R.id.email);
        mTextPassword = (EditText)findViewById(R.id.password);
        mFblogin=findViewById(R.id.fblogin);

        mButtonLogin = (Button)findViewById(R.id.LoginButton);
        mAuth=FirebaseAuth.getInstance();
        mTextViewRegister = (TextView)findViewById(R.id.Register);
        callbackManager=CallbackManager.Factory.create();
        mFblogin.setPermissions(Arrays.asList("email","public_profile"));
        mFblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(Login.this,"logged in",Toast.LENGTH_SHORT).show();
//                AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
//                    @Override
//                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                        if(currentAccessToken!=null){
//                            getuserprofile(currentAccessToken);
//                        }
//                    }
//                };
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

       /* show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);
        show_hide_password
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        if (isChecked) {

                            show_hide_password.setText("Hide Password");

                            mTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            mTextPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                        } else {
                            show_hide_password.setText("Show Password");

                            mTextPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mTextPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());

                        }

                    }
                });*/
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        loadingBar=new ProgressDialog(this);
        Intent intent = new Intent(Login.this,MainActivity.class);

        if(pref.contains("Email") ){
            startActivity(intent);
            finish();
        }
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newregisterintent = new Intent(Login.this,Register.class);
                startActivity(newregisterintent);
                finish();
            }
        });
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user)) {
                    Toast.makeText(Login.this, "Please write your Email id...", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(Login.this, "Please write your password...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Login in : ");
                    loadingBar.setMessage("Please Wait ");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    mAuth.signInWithEmailAndPassword(user, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        verifyuser();
                                        loadingBar.dismiss();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(Login.this, "Error Occured " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }
    private  void verifyuser()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        char[] user1 = mTextUsername.getText().toString().toCharArray();
        String username="";
        int i=0;
        while(user1[i]!='@'){
            username+=user1[i];
            i++;
        }
        emailaddresschecker = user.isEmailVerified();
        if(emailaddresschecker)
        {
            Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra("Username",username);
            SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
            editor.putString("Username",username);
            editor.apply();
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(Login.this,"Please Verify Your Email to continue",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Intent mainIntent = new Intent( Login.this,MainActivity.class);

            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken!=null){
                getuserprofile(currentAccessToken);
            }
        }
    };
    public void getuserprofile(AccessToken accessToken){
        GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                FacebookRequestError error = response.getError();

                if(error!=null) Toast.makeText(Login.this,error.getErrorMessage().toString(),Toast.LENGTH_LONG).show();
                else{
                    try {

                        final String url = response.getJSONObject()
                                .getJSONObject("picture")
                                .getJSONObject("data")
                                .getString("url");

                        final String first_name=object
                                .getString("first_name");
                        final String last_name=object
                                .getString("last_name");
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setTitle("Enter your mail id to continue");

                        final Intent i = new Intent(Login.this, Registration.class);
                        final SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();

                            final EditText input = new EditText(Login.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   String emailS = input.getText().toString();
                                    char[] email = emailS
                                            .toCharArray();

                                    String username="";
                                    int j=0;
                                    while(email[j]!='@'){
                                        username+=email[j];
                                        j++;
                                    }
                                    i.putExtra("Username", username);
                                    i.putExtra("Email", emailS);
                                    editor.putString("Email", emailS);
                                    editor.putString("Username",username);

                                    i.putExtra("Image", url);
                                    i.putExtra("Name",first_name+" "+last_name);

                                    editor.putString("Image",url);
                                    editor.putString("Name",first_name+" "+last_name);
                                    editor.apply();

                                    startActivity(i);
                                    finish();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();



                    } catch (JSONException e) {
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        e.printStackTrace();

                    }
                }

            }
        });
        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,picture.type(large)");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
