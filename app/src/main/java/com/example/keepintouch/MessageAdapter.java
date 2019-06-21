package com.example.keepintouch;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth madaptAuth;
    String mcur_user;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        madaptAuth=FirebaseAuth.getInstance();
        mcur_user=madaptAuth.getCurrentUser().getUid();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_message ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText,timeSent;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeSent = (TextView) view.findViewById(R.id.time_text_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();
        Long time_send=c.getTime();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(image).placeholder(R.drawable.profile).into(viewHolder.profileImage);
                viewHolder.displayName.setText(name);
              /*  String getTime=dataSnapshot.child("time").getValue().toString();
                Long server_time=Long.parseLong(getTime);
                String time_sent=getDate(server_time);
//                viewHolder.timeSent.setText(time_sent);*/
//                if (from_user.equals(mcur_user)){
//                    viewHolder.messageText.setBackgroundResource(R.drawable.my_message);
//                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        } else  {
            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            //    Picasso.get().load(c.getMessage())
            //         .placeholder(R.drawable.profile).into(viewHolder.messageImage);
           Glide.with(viewHolder.messageImage.getContext()).load(c.getMessage()).into(viewHolder.messageImage);



        }

                String time_sent=getDate(time_send);
                viewHolder.timeSent.setText(time_sent);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private String getDate(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);
return dateFormatted;



    }





}
