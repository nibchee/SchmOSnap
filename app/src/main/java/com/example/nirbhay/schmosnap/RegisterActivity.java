package com.example.nirbhay.schmosnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity
{
    private EditText UserEmail,UserPassword,UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        UserEmail=(EditText)findViewById(R.id.register_email);
        UserPassword=(EditText)findViewById(R.id.register_password);
        UserConfirmPassword=(EditText)findViewById(R.id.register_confirm_password);
        CreateAccountButton=(Button)findViewById(R.id.register_create_account);
        loadingBar=new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();

            }
        });
    }

    private void CreateNewAccount()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        String confirmPassword= UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please Write Your Email....",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Write Your Password....",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this,"Please Write Your Confirm Password....",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this,"Password does not match with confirm password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait,while we creating your new account.....");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

           mAuth.createUserWithEmailAndPassword(email, password)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task)
                       {
                           if(task.isSuccessful())
                           {
                               SendUserToSetUpActivity();
                               Toast.makeText(RegisterActivity.this,"You are authenticated sucessfully....",Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                           }
                           else
                           {
                               String message=task.getException().getMessage();
                               Toast.makeText(RegisterActivity.this,"Error Occured "+ message,Toast.LENGTH_SHORT).show();
                              loadingBar.dismiss();
                           }

                       }
                   });
        }
    }

    private void SendUserToSetUpActivity()
    {
        Intent setUpIntent=new Intent(RegisterActivity.this,SetupActivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUpIntent);
        finish();
    }
}