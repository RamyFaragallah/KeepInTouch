package com.example.keepintouch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference muser=null;
 private FirebaseAuth.AuthStateListener mauthStateListener;


    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfirebaseAuth=FirebaseAuth.getInstance();
        muser= FirebaseDatabase.getInstance().getReference().child("users");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.to_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
         mauthStateListener=new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                 if (firebaseUser==null){
                     startActivity(new Intent(MainActivity.this,LoginActivity.class));

                 }

             }
         };
    }

   


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            AuthUI.getInstance().signOut(getApplicationContext());
            getSharedPreferences("remember",MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit();

            return true;
        }
        if (id==R.id.acc_setting){
            Intent intent=new Intent(MainActivity.this,AccoutSetting.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
           switch (position){
               case 0:
                   tabhome tab1=new tabhome();
                   return tab1;
               case 1:
                   tabcontact tab2=new tabcontact();
                   return tab2;
               case 2:
                   tabrequsts tab3=new tabrequsts();
                   return tab3;
               default:
                   return null;
           }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Home";
                case 1:
                    return "Friends";
                case 2:
                    return "Requests";
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mfirebaseAuth.addAuthStateListener(mauthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mfirebaseAuth.removeAuthStateListener(mauthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            if (resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"signed in",Toast.LENGTH_LONG).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"fail to sign in",Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder exit=new AlertDialog.Builder(MainActivity.this);
            exit.setTitle("EXIT !");
            exit.setMessage("Are want to close the app");
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
    protected void onStart() {
        super.onStart();
         user=mfirebaseAuth.getCurrentUser().getUid();
         String id=muser.child(user).getKey();

        if (id==null){
            HashMap<String,String> userdetails=new HashMap<>();
            userdetails.put("name",mfirebaseAuth.getCurrentUser().getDisplayName());
            userdetails.put("image","no image");
            userdetails.put("status","Hey ther, I'm using KIT application");
            userdetails.put("online", String.valueOf(ServerValue.TIMESTAMP));
            muser.child(user).setValue(userdetails);
        }
        else {
            muser.child(user).child("online").setValue("online");
        }
    } @Override
    protected void onStop() {
        super.onStop();
        muser.child(user).child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        muser.child(user).child("online").setValue(ServerValue.TIMESTAMP);

    }

}
