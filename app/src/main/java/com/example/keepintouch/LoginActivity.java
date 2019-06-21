package com.example.keepintouch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth mAuth;
    private EditText txt_mail,txt_pass;
    private Button login,btnnew;
    private CheckBox chk_rem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_pass=(EditText)findViewById(R.id.txtpass);
        txt_mail=(EditText)findViewById(R.id.txt_mail);
        login=(Button)findViewById(R.id.btnlog);
        btnnew=(Button)findViewById(R.id.btnnew);
        chk_rem=(CheckBox)findViewById(R.id.chk_rem);

        mAuth=FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m=txt_mail.getText().toString();
                String p=txt_pass.getText().toString();
            if (txt_mail.getText().toString().isEmpty()){
                txt_mail.setError("E-mail is empty");

            } if (txt_pass.getText().toString().isEmpty()){
                    txt_pass.setError("Password is empty");

            }
            else {
                login_method(m,p);            }


            }
        });
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()

                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setTheme(R.style.AppTheme_NoActionBar)
                                .setLogo(R.drawable.logo)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                                .build(),
                        RC_SIGN_IN);

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder exit=new AlertDialog.Builder(LoginActivity.this);
            exit.setTitle("EXIT !");
            exit.setMessage("Are want to close app");
            exit.setNegativeButton("Close app", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            }).setPositiveButton("Dismiss",null);

            exit.show();
            exit.create();
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            if (resultCode==RESULT_OK){
                final FirebaseUser user = mAuth.getCurrentUser();
                final String user_id=user.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                            if (dataSnapshot.child(user_id).exists()) {
                                FirebaseDatabase userdatabase = FirebaseDatabase.getInstance();
                              DatabaseReference userref = userdatabase.getReference("users");
//                                userref.child(user_id).child("name").setValue(user.getDisplayName());
//                                userref.child(user_id).child("image").setValue("no image");
//                                userref.child(user_id).child("status").setValue("Hey ther, I'm using KIT application");
                                HashMap<String,String>userdetails=new HashMap<>();
                                userdetails.put("name",user.getDisplayName());
                                userdetails.put("image","no image");
                                userdetails.put("status","Hey ther, I'm using KIT application");
                                userref.child(user_id).setValue(userdetails);
                                if(chk_rem.isChecked())
                                {
                                    getSharedPreferences("remember",MODE_PRIVATE)
                                            .edit()
                                            .putString("Username",user.getDisplayName())
                                            .putString("id",user.getUid())
                                            .apply();

                                }



                                                        }
                                else {
                                if(chk_rem.isChecked())
                                {
                                    getSharedPreferences("remember",MODE_PRIVATE)
                                            .edit()
                                            .putString("Username",user.getDisplayName())
                                            .putString("id",user.getUid())
                                            .apply();

                                }

                            }
                        }



                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
                Intent i=new Intent(LoginActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            finish();
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"fail to sign in",Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }
    void login_method(final String m, final String p){
        mAuth.signInWithEmailAndPassword(m, p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(chk_rem.isChecked())
                            {
                                getSharedPreferences("remember",MODE_PRIVATE)
                                        .edit()
                                        .putString("Username",m)
                                        .putString("id",user.getUid())

                                        .apply();
                            }

                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
