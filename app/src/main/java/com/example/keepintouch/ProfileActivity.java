package com.example.keepintouch;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public String current_stat="not_friends";
    DatabaseReference myRef ;
    DatabaseReference friend_request ;
    DatabaseReference friends ;
    DatabaseReference notifications ;

    FirebaseUser mcurrent;
    private CircleImageView imgpro;
    private TextView txtproname,txtprostat;
    private Button btnsendrequest,btnreject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id=getIntent().getStringExtra("user_id");
        myRef= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friend_request= FirebaseDatabase.getInstance().getReference().child("friend_req");
        friends= FirebaseDatabase.getInstance().getReference().child("friends");
        notifications= FirebaseDatabase.getInstance().getReference().child("notifications");
        mcurrent= FirebaseAuth.getInstance().getCurrentUser();

        imgpro=(CircleImageView)findViewById(R.id.imgpro);
        txtproname=(TextView)findViewById(R.id.txtproname);
        txtprostat=(TextView)findViewById(R.id.txtprostat);
        btnsendrequest=(Button)findViewById(R.id.btnsendrequest);
        btnreject=(Button)findViewById(R.id.btnreject);
        btnreject.setVisibility(View.INVISIBLE);



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                txtproname.setText(name);
                txtprostat.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.profile).into(imgpro);

                friend_request.child(mcurrent.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_stat=dataSnapshot.child(user_id).child("req_stat").getValue().toString();
                            if (req_stat.equals("received")){
                                current_stat="req_received";
                                btnsendrequest.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                                btnreject.setBackgroundColor(getResources().getColor(R.color.browser_actions_bg_grey));
                                btnsendrequest.setText("Accept request");
                                btnreject.setVisibility(View.VISIBLE);
                            }
                            else if (req_stat.equals("sent")){
                                current_stat = "req_sent";
                                btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_linkColor));
                                btnsendrequest.setText("Cancel sending request");
                                btnreject.setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            friends.child(mcurrent.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_bgPhone));
                                        btnsendrequest.setText("Remove friend");
                                        current_stat="friends";
                                        btnreject.setVisibility(View.INVISIBLE);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });





        btnsendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_stat.equals("not_friends")) {
                    friend_request.child(mcurrent.getUid()).child(user_id).child("req_stat").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friend_request.child(user_id).child(mcurrent.getUid()).child("req_stat").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_linkColor));
                                        btnsendrequest.setText("Cancel");
                                        current_stat = "req_sent";
                                        HashMap<String,String>notf_data=new HashMap<>();
                                        notf_data.put("from",mcurrent.getUid());
                                        notf_data.put("type","request");
                                        notifications.child(user_id).push().setValue(notf_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "failed to send request", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                    if (current_stat.equals("req_sent")){
                    friend_request.child(mcurrent.getUid()).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friend_request.child(user_id).child(mcurrent.getUid()).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_bgPhone));
                                        btnsendrequest.setText("Send friend request");
                                        current_stat="not_friends";
                                        btnreject.setVisibility(View.INVISIBLE);
                                    }
                                });

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"failed to delete request",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }if (current_stat.equals("req_received")){
                        final String curdate= DateFormat.getDateTimeInstance().format(new Date());
                        friends.child(mcurrent.getUid()).child(user_id).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friends.child(user_id).child(mcurrent.getUid()).child("date").setValue(curdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        friend_request.child(mcurrent.getUid()).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    friend_request.child(user_id).child(mcurrent.getUid()).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_bgPhone));
                                                            btnsendrequest.setText("Remove friend");
                                                            current_stat="friends";
                                                            btnreject.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(),"You are friends now",Toast.LENGTH_LONG).show();

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
                if (current_stat.equals("friends")){
                    AlertDialog.Builder remove_friend=new AlertDialog.Builder(ProfileActivity.this);
                    remove_friend.setTitle("Removing Friend");
                    remove_friend.setMessage("Confirm to remove your friend !!");
                    remove_friend.setNegativeButton("Dismiss",null);
                    remove_friend.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            friends.child(mcurrent.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        friends.child(user_id).child(mcurrent.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_bgPhone));
                                                btnsendrequest.setText("Send friend request");
                                                current_stat="not_friends";
                                                btnreject.setVisibility(View.INVISIBLE);

                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"failed to remove friend",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                    remove_friend.create();
                    remove_friend.show();
                }
                            else {
                                Toast.makeText(getApplicationContext(),"failed to delete request",Toast.LENGTH_LONG).show();
                            }
                        }




        });
        btnreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder reject_req=new AlertDialog.Builder(ProfileActivity.this);
                reject_req.setTitle("Reject request");
                reject_req.setMessage("Confirm to reject friend request !!");
                reject_req.setNegativeButton("Dismiss",null);
                reject_req.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        friend_request.child(mcurrent.getUid()).child(user_id).child("req_stat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    friend_request.child(user_id).child(mcurrent.getUid()).child("req_stat").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            btnsendrequest.setBackgroundColor(getResources().getColor(R.color.fui_bgPhone));
                                            btnsendrequest.setText("Send friend request");
                                            current_stat="not_friends";
                                            btnreject.setVisibility(View.INVISIBLE);

                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"error in rejecting request",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                reject_req.create();
                reject_req.show();


            }
        });
    }
}
