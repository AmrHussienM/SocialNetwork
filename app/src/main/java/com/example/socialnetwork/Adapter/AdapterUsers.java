package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.Model.User;
import com.example.socialnetwork.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> {

    Context context;
    List<User> userList;

    public AdapterUsers(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_users,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //get Data

        String userImage=userList.get(i).getImage();
        final String userName=userList.get(i).getName();
        String userBusiness=userList.get(i).getBusiness();

        //set Data
        viewHolder.name.setText(userName);
        viewHolder.business.setText(userBusiness);

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.profile).into(viewHolder.circleImageView);
        }
        catch (Exception e){}

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+userName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name,business;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.image_user);
            name=itemView.findViewById(R.id.name_user);
            business=itemView.findViewById(R.id.business_user);
        }
    }
}
