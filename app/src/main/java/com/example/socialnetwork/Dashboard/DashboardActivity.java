package com.example.socialnetwork.Dashboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.socialnetwork.Fragments.HomeFragment;
import com.example.socialnetwork.Fragments.ProfileFragment;
import com.example.socialnetwork.Fragments.UsersFragment;
import com.example.socialnetwork.Login.LoginActivity;
import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        InilizeFields();
        BottomNavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //default
        actionBar.setTitle("Home");
        HomeFragment homeFragment=new HomeFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.content,homeFragment,"");
        fragmentTransaction.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
                {
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_home:

                            actionBar.setTitle("Home");
                            HomeFragment homeFragment=new HomeFragment();
                            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

                            fragmentTransaction.replace(R.id.content,homeFragment,"");
                            fragmentTransaction.commit();

                            return true;

                        case R.id.nav_profile:

                            actionBar.setTitle("Profile");
                            ProfileFragment profileFragment=new ProfileFragment();
                            FragmentTransaction profilefragmentTransaction=getSupportFragmentManager().beginTransaction();

                            profilefragmentTransaction.replace(R.id.content,profileFragment,"");
                            profilefragmentTransaction.commit();
                            return true;

                        case R.id.nav_users:

                            actionBar.setTitle("Users");
                            UsersFragment usersFragment=new UsersFragment();
                            FragmentTransaction usersFragmentTransaction=getSupportFragmentManager().beginTransaction();

                            usersFragmentTransaction.replace(R.id.content,usersFragment,"");
                            usersFragmentTransaction.commit();
                            return true;
                    }

                    return false;
                }
            };

    private void CheckUserStatus()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null)
        {
            //user is signed in, Stay here.
            //mProfileTv.setText(user.getEmail());

        }
        else
            {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();

            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void InilizeFields()
    {
        mAuth=FirebaseAuth.getInstance();

        actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");

        //mProfileTv=findViewById(R.id.profileTv);

    }

    @Override
    protected void onStart() {

        CheckUserStatus();

        super.onStart();
    }

}
