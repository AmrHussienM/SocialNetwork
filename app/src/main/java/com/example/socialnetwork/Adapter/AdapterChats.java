package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialnetwork.Model.Chat;
import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<Chat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChats(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,viewGroup,false);
            return new ViewHolder(view);
        }
        else
            {
                View view=LayoutInflater.from(context).inflate(R.layout.row_chat_left,viewGroup,false);
                return new ViewHolder(view);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //get Data
        String message=chatList.get(i).getMessage();
        String timeStamp=chatList.get(i).getTimestamp();

        //convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        //set Data
        viewHolder.messageTv.setText(message);
        viewHolder.timeTv.setText(dateTime);

        try {
            Picasso.get().load(imageUrl).into(viewHolder.imageChat);
        }
        catch (Exception e)
        {

        }

        //set Seen/delivered status of messages
        if (i==chatList.size() - 1)
        {
            if (chatList.get(i).isSeen())
            {
                viewHolder.seenTv.setText("Seen");
            }
            else
                {
                    viewHolder.seenTv.setText("Delivered");
                }
        }
        else
            {
                viewHolder.seenTv.setVisibility(View.GONE);

            }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }

        else
            {
                return MSG_TYPE_LEFT;
            }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageChat;
        TextView messageTv,timeTv,seenTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageChat=itemView.findViewById(R.id.image_chat);
            messageTv=itemView.findViewById(R.id.message_chat);
            timeTv=itemView.findViewById(R.id.time);
            seenTv=itemView.findViewById(R.id.isSeen);
        }
    }
}
