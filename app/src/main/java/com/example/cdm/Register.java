package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Register extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    LoginButton mFblogin;
    Button mButtonRegister;
    private Boolean emailaddresschecker;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    String currentuserid;
    private EditText mEmailText;
    ProgressDialog loadingbar;
    ProgressBar progressBar;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean vp =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fAuth = FirebaseAuth.getInstance();
        mTextPassword = (EditText) findViewById(R.id.rpass);
        mTextCnfPassword = (EditText) findViewById(R.id.rcpass);
        mEmailText=findViewById(R.id.remail);
        mButtonRegister = (Button) findViewById(R.id.cirRegisterButton);
        mFblogin=findViewById(R.id.fblogin);
        mAuth = FirebaseAuth.getInstance();

        loadingbar=new ProgressDialog(this);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pwd = mTextPassword.getText().toString().trim();
                String email = mEmailText.getText().toString().trim();
                String cnfpwd = mTextCnfPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Register.this, "Please write your email id ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(pwd))
                {
                    Toast.makeText(Register.this, "Please write your password...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(cnfpwd))
                {
                    Toast.makeText(Register.this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
                }
                else if(!pwd.equals(cnfpwd))
                {
                    Toast.makeText(Register.this, "your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Creating New Account");
                    loadingbar.setMessage("Please Wait ");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);
                    mAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "your are authentication sucessfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        SendEmailVerifiaction();
                                    }
                                    else
                                    {
                                        String message =task.getException().getMessage();
                                        Toast.makeText(Register.this, "Error Occured"+message, Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });

                }

            }
        });
        callbackManager= CallbackManager.Factory.create();
        mFblogin.setReadPermissions(Arrays.asList("email","public_profile"));
        mFblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private  void SendEmailVerifiaction()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        mAuth.signOut();
                        Toast.makeText(Register.this,"Your Registration is Sucessfull Please Verify Your Email id...",Toast.LENGTH_SHORT).show();
                        Intent loginintent = new Intent(Register.this,Login.class);
                        startActivity(loginintent);
                        finish();
                    }
                    else
                    {
                        String  meaasge = task.getException().getMessage().toString();
                        Toast.makeText(Register.this,"Error !"+meaasge,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
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

                if(error!=null) Toast.makeText(Register.this,error.getErrorMessage().toString(),Toast.LENGTH_LONG).show();
                else{
                    try {
                        String first_name=object
                                .getString("first_name");
                        String last_name=object
                                .getString("last_name");
                        String emailS=object
                                .getString("email");
                        char[] email = emailS
                                .toCharArray();
                        String id=object.getString("id");
                        String urltoimage="https://graph.facebook.com/"+id+"/picture?type=large";
                        Toast.makeText(Register.this,first_name+urltoimage,Toast.LENGTH_LONG).show();
                        String username="";
                        int j=0;
                        while(email[j]!='@'){
                            username+=email[j];
                            j++;
                        }
                        Intent i = new Intent(Register.this, Registration.class);
                        i.putExtra("Image", urltoimage);
                        i.putExtra("Username", username);
                        i.putExtra("Email",emailS);
                        i.putExtra("Name",first_name+" "+last_name);
                        SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
                        editor.putString("Email",emailS);
                        editor.putString("Username",username);
                        editor.putString("Image",urltoimage);
                        editor.putString("Name",first_name+" "+last_name);
                        editor.apply();
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {

                        e.printStackTrace();

                    }
                }

            }
        });
        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
