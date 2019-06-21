package com.example.keepintouch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeStatus extends AppCompatActivity {

    FirebaseUser getuser;
    DatabaseReference myRef ;

    private Button btnsave;
    private EditText txtchst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        txtchst=(EditText)findViewById(R.id.txtchst);
        btnsave=(Button)findViewById(R.id.btnsave);

        getuser= FirebaseAuth.getInstance().getCurrentUser();
        final String user_id=getuser.getUid();
        myRef= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newst=txtchst.getText().toString();
                if (newst.isEmpty()){
                    txtchst.setError("field is empty");
                }
                else{  myRef.child("status").setValue(newst).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Operation is failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                    finish();
                }

            }
        });
    }
}
