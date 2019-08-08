package com.example.socialnetwork.Reigster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialnetwork.Login.LoginActivity;
import com.example.socialnetwork.Dashboard.DashboardActivity;
import com.example.socialnetwork.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,password,confirmPassword;
    private Button mRegisterBtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Button mainSignupBtn,mainLoginBtn;
    String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitalizeField();

        mainLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterInput();
            }
        });



    }

    private void RegisterInput()
    {
        String mEmail=email.getText().toString().trim();
        mPassword=password.getText().toString().trim();
        String confirmPass=confirmPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches())
        {
            email.setError("Invalid Email !");
            email.setFocusable(true);
        }
        else if (mPassword.length() < 6)
        {
            password.setError("password must at least six character..");
            password.setFocusable(true);

            if (confirmPass.equals(mPassword))
            {
                confirmPassword.setError("confirm password mismatch !");
                confirmPassword.setFocusable(true);
            }
        }

        else
            {
                RegisterUser(mEmail,mPassword);
            }

    }

    private void RegisterUser(String mEmail, final String mPassword)
    {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(mEmail,mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            //Sign In Success, dismiss dialog and start Register Activity
                            progressDialog.dismiss();

                            FirebaseUser user=mAuth.getCurrentUser();

                            String email=user.getEmail();
                            String uid=user.getUid();


                            HashMap<Object,String> map=new HashMap<>();

                            map.put("email",email);
                            map.put("uid",uid);
                            map.put("name","");
                            map.put("phone","");
                            map.put("image","");
                            map.put("password",mPassword);
                            map.put("onlineStatus","online");
                            map.put("univeristy","");
                            map.put("business","");
                            map.put("cover","");

                            FirebaseDatabase database=FirebaseDatabase.getInstance();

                            DatabaseReference reference=database.getReference("Users");

                            reference.child(uid).setValue(map);



                            Toast.makeText(RegisterActivity.this, "Registered..\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else
                              {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Authentication failed !", Toast.LENGTH_SHORT).show();
                              }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();  //go previous Activity
        return super.onSupportNavigateUp();
    }


    private void InitalizeField()

    {
        mAuth=FirebaseAuth.getInstance();
/*

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Create Account");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
*/

        email=findViewById(R.id.email_editText);
        password=findViewById(R.id.password_editText);
        mRegisterBtn=findViewById(R.id.buttonSignUp);
        mainLoginBtn=findViewById(R.id.mainBtnLogin);
        confirmPassword=findViewById(R.id.confirmPass_editText);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

    }

}
