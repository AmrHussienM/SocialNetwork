package com.example.socialnetwork.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.Dashboard.DashboardActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Reigster.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin,passwordLogin;
    private Button loginBtn,mainSignupBtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private TextView forgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitlizeFields();

        mainSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginInput();
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showRecoverPassword();

            }
        });
    }


    private void LoginInput()
    {
        String email=emailLogin.getText().toString().trim();
        String password=passwordLogin.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailLogin.setError("Invalid Email !");
            emailLogin.setFocusable(true);

        }
        else
            {
                LoginUser(email,password);
            }

    }

    private void LoginUser(String email, String password)
    {
        progressDialog.setMessage("Sign In...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            FirebaseUser user=mAuth.getCurrentUser();

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void InitlizeFields()
    {
/*

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Login");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
*/

        emailLogin=findViewById(R.id.email_login_editText);
        passwordLogin=findViewById(R.id.password_login_editText);
        loginBtn=findViewById(R.id.buttonLogin);
        mainSignupBtn=findViewById(R.id.MainbtnSignup);
        forgotPassword=findViewById(R.id.forgotPasswordTxt);

        progressDialog=new ProgressDialog(this);
        //progressDialog.setMessage("Sign In...");

        mAuth=FirebaseAuth.getInstance();

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void showRecoverPassword()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover password");

        ConstraintLayout constraintLayout=new ConstraintLayout(this);

        final EditText emailEt=new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);

        constraintLayout.addView(emailEt);
        constraintLayout.setPadding(10,10,10,10);

        builder.setView(constraintLayout);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String email=emailEt.getText().toString().trim();
                beginRecoveryPass(email);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

            }
        });

        builder.create().show();





    }

    private void beginRecoveryPass(String email)
    {

        progressDialog.setMessage("Sending Email...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) 
            {
                progressDialog.dismiss();
                if (task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Email sent..", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        Toast.makeText(LoginActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                    }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
