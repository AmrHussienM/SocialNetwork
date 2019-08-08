package com.example.socialnetwork.Chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.Login.LoginActivity;
import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    CircleImageView profileImg;
    TextView name,status;
    EditText messageEt;
    ImageButton sendBtn;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid,myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InitlizeFields();
        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        Query userQuery=usersDbRef.orderByChild("uid").equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //get Data
                    String nameChat=""+ds.child("name").getValue();
                    String imageChat=""+ds.child("image").getValue();


                    //set Data
                    name.setText(nameChat);

                    try {
                        Picasso.get().load(imageChat).placeholder(R.drawable.profile).into(profileImg);

                    }
                    catch (Exception e){

                        Picasso.get().load(R.drawable.profile).into(profileImg);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=messageEt.getText().toString();
                if (TextUtils.isEmpty(message))
                {
                    Toast.makeText(ChatActivity.this, "empty message...", Toast.LENGTH_SHORT).show();

                }
                else
                    {
                        sendMessages(message);
                        
                    }
            }
        });
    }

    private void sendMessages(String message)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        databaseReference.child("Chats").push().setValue(hashMap);

        //reset EditText after sending message
        messageEt.setText("");
    }


    private void CheckUserStatus()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if (user != null)
        {
            //user is signed in, Stay here.
            //mProfileTv.setText(user.getEmail());
            myUid=user.getUid();

        }
        else
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }
    }

    @Override
    protected void onStart() {
        CheckUserStatus();
        super.onStart();
    }

    private void InitlizeFields()
    {
        toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView=findViewById(R.id.chat_recyclerview);
        profileImg=findViewById(R.id.profile_img);
        name=findViewById(R.id.profile_name);
        status=findViewById(R.id.profile_status);
        messageEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef=firebaseDatabase.getReference("Users");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.action_logout)
        {
            mAuth.signOut();
            CheckUserStatus();

        }
        return super.onOptionsItemSelected(item);
    }
}

