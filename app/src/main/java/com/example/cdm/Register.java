package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

public class Register extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    private Boolean emailaddresschecker;

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
                                        //SendEmailVerifiaction();
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

    }
}
