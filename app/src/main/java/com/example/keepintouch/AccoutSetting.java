package com.example.keepintouch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccoutSetting extends AppCompatActivity {

    private CircleImageView imgpic;
    private Button btnchangepic,btnchgstt;
    private TextView txtprofile,txtstat;

    FirebaseUser getuser;
    FirebaseAuth mfirebaseAuth;
    DatabaseReference myRef ;
    StorageReference mstorageReference;


    final static int pic_code=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accout_setting);
        mfirebaseAuth=FirebaseAuth.getInstance();
        mstorageReference= FirebaseStorage.getInstance().getReference();
        getuser=mfirebaseAuth.getCurrentUser();
       final String user_id=getuser.getUid();



        imgpic=(CircleImageView)findViewById(R.id.imgpic);
        btnchangepic=(Button)findViewById(R.id.btnchangepic);
        btnchgstt=(Button)findViewById(R.id.btnchgstt);
        txtprofile=(TextView)findViewById(R.id.txtprofile);
        txtstat=(TextView)findViewById(R.id.txtstat);

        getuser=FirebaseAuth.getInstance().getCurrentUser();
        myRef=FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                txtprofile.setText(name);
                txtstat.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.profile).into(imgpic);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        btnchgstt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(AccoutSetting.this,ChangeStatus.class);
                startActivity(i);
            }
        });
        btnchangepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galarypic=new Intent();
                galarypic.setType("image/*");
                galarypic.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galarypic,"select image"),pic_code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==pic_code&&resultCode==RESULT_OK)
        {
            getuser=FirebaseAuth.getInstance().getCurrentUser();
            final ProgressDialog progressDialog=new ProgressDialog(AccoutSetting.this);
            progressDialog.setTitle("Loading ......");
            progressDialog.setMessage("Please wait until image uploaded");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            String user_id=getuser.getUid();
            Uri imguri=data.getData();
            final StorageReference filepath=mstorageReference.child("profile").child(user_id);
            filepath.putFile(imguri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                progressDialog.cancel();
                                myRef.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"successful",Toast.LENGTH_LONG);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG);

                                        }
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                    else {
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG);

                    }
                }
            });

        }
        else if(resultCode==RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"You didn't select picture",Toast.LENGTH_LONG).show();
        }

    }
}
