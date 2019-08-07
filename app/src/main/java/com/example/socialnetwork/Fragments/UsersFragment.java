package com.example.socialnetwork.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialnetwork.Adapter.AdapterUsers;
import com.example.socialnetwork.Dashboard.DashboardActivity;
import com.example.socialnetwork.Login.LoginActivity;
import com.example.socialnetwork.Model.User;
import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    AdapterUsers adapter;
    List<User> userList;

    FirebaseAuth mAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView=view.findViewById(R.id.user_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();

        getAllUsers();

        return view;
    }

    private void getAllUsers()
    {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    User user=ds.getValue(User.class);

                    if (!user.getUid().equals(firebaseUser.getUid()))
                    {
                        userList.add(user);
                    }

                    adapter=new AdapterUsers(getActivity(),userList);

                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void searchUsers(final String query)
    {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    User user=ds.getValue(User.class);

                    if (!user.getUid().equals(firebaseUser.getUid()))
                    {

                        if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                                user.getEmail().toLowerCase().contains(query.toLowerCase()))
                        {
                            userList.add(user);

                        }

                    }

                    adapter=new AdapterUsers(getActivity(),userList);

                    adapter.notifyDataSetChanged();

                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


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
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main,menu);


        //Search View

        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                if (!TextUtils.isEmpty(s.trim()))
                {
                    searchUsers(s);

                }
                else
                    {
                        getAllUsers();
                    }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {

                if (!TextUtils.isEmpty(s.trim()))
                {
                    searchUsers(s);

                }
                else
                {
                    getAllUsers();
                }


                return false;
            }
        });


         super.onCreateOptionsMenu(menu,inflater);
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
