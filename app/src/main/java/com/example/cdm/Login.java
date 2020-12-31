package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    private static CheckBox show_hide_password;
    private FirebaseAuth mAuth;
    private Boolean emailaddresschecker;
    private ProgressDialog loadingBar;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTextUsername = (EditText)findViewById(R.id.email);
        mTextPassword = (EditText)findViewById(R.id.password);
        mButtonLogin = (Button)findViewById(R.id.LoginButton);
        mAuth=FirebaseAuth.getInstance();
        mTextViewRegister = (TextView)findViewById(R.id.Register);
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
        if(pref.contains("username") && pref.contains("password")){
            startActivity(intent);
        }
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newregisterintent = new Intent(Login.this,Register.class);
                startActivity(newregisterintent);
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

    }
    private  void verifyuser()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        String user1 = mTextUsername.getText().toString();
        emailaddresschecker = user.isEmailVerified();
        if(emailaddresschecker)
        {
            Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra("Email",user1);
            startActivity(intent);
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
        }
    }
}
