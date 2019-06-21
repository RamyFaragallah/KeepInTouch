package com.example.keepintouch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class tabrequsts extends Fragment {
    String mcur_user;
//    String req_user;
    private RecyclerView recyclerView;
//    private Query mreqref;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference reqroot;
    private DatabaseReference friends;
    private DatabaseReference friend_request;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.requests, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reqRlist);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mcur_user = mfirebaseAuth.getCurrentUser().getUid();
        reqroot = FirebaseDatabase.getInstance().getReference().child("friend_req").child(mcur_user);
        friend_request = FirebaseDatabase.getInstance().getReference().child("friend_req");
        friends = FirebaseDatabase.getInstance().getReference().child("friends");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return rootView;


    }
//
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Reqmodel> options =
                new FirebaseRecyclerOptions.Builder<Reqmodel>()
                        .setQuery(reqroot, Reqmodel.class)
                        .setLifecycleOwner(this)
                        .build();
        mFirebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Reqmodel,ReqHolder>(options) {
            @NonNull
            @Override
            public ReqHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new tabrequsts.ReqHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.req_item, viewGroup, false));
            }
//
            @Override
            protected void onBindViewHolder(@NonNull final ReqHolder holder, int position, @NonNull Reqmodel model) {
             final String   req_user = getRef(position).getKey();
                final DatabaseReference get_type_ref=getRef(position).child("req_stat").getRef();
//
                    get_type_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String req_type=dataSnapshot.getValue().toString();
                                if (req_type.equals("received")){
                                    mUsersDatabase.child(req_user).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String userName = dataSnapshot.child("name").getValue().toString();
                                            String userThumb = dataSnapshot.child("image").getValue().toString();


                                            holder.setName(userName);
                                            holder.setUserImage(userThumb);
                                            holder.approve(mcur_user,req_user);
                                            holder.reject(mcur_user,req_user);



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError dataError) {
                                            Toast.makeText(getContext(),"Error at "+ dataError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else if (req_type.equals("sent")){
                                    mUsersDatabase.child(req_user).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String userName = dataSnapshot.child("name").getValue().toString();
                                            String userThumb = dataSnapshot.child("image").getValue().toString();

//
                                            holder.setName(userName);
                                            holder.setUserImage(userThumb);
                                            holder.remove_req(mcur_user,req_user);
//
//
//
//
                                        }
//
                                        @Override
                                        public void onCancelled(DatabaseError dataError) {
                                            Toast.makeText(getContext(), "Error in "+dataError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                            else { Toast.makeText(getContext(),"List is empty",Toast.LENGTH_SHORT).show();}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(),"Error in "+ databaseError.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

            }

        };
        recyclerView.setAdapter(mFirebaseRecyclerAdapter);

    }

    private class ReqHolder  extends RecyclerView.ViewHolder{
        View mView;
        public ReqHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(final String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_name_layout);
            userNameView.setText(name);

        }
        public void setUserImage( final String thumb_image){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_profile_layout);
            Picasso.get().load(thumb_image).placeholder(R.drawable.profile).into(userImageView);

        }
        public void approve(final String mcurrent, final String user_id){
            Button req_approve_btn=(Button)mView.findViewById(R.id.req_approve_btn);
            req_approve_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String curdate= DateFormat.getDateTimeInstance().format(new Date());
                    friends.child(mcurrent).child(user_id).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friends.child(user_id).child(mcurrent).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                });

                                            }

                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            });
        }
        public void reject(final String mcurrent, final String user_id){
            Button req_Reject_btn=(Button)mView.findViewById(R.id.req_Reject_btn);
            req_Reject_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {



                                    }
                                });

                            }
                            else {
                                Toast.makeText(getContext(),"failed to reject request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
        public void remove_req(final String mcurrent, final String user_id){
            Button req_Reject_btn=(Button)mView.findViewById(R.id.req_Reject_btn);
            Button req_approve_btn=(Button)mView.findViewById(R.id.req_approve_btn);
            req_Reject_btn.setVisibility(View.INVISIBLE);
            req_approve_btn.setText("Cancel");
            req_approve_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friend_request.child(mcurrent).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friend_request.child(user_id).child(mcurrent).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {



                                    }
                                });

                            }
                            else {
                                Toast.makeText(getContext(),"failed to delete request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });

        }

    }
}
