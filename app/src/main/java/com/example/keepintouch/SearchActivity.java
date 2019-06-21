package com.example.keepintouch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {
    private ImageView seach_btn;
    private EditText mSearchField;
    FirebaseUser getuser;
    String mcur_id=null;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");
        mSearchField = (EditText) findViewById(R.id.search_field);

        mResultList = (RecyclerView) findViewById(R.id.list_result);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        getuser=FirebaseAuth.getInstance().getCurrentUser();
        mcur_id=getuser.getUid();



        seach_btn=(ImageView)findViewById(R.id.search_btn);
        seach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);

            }
        });
    }
    private void firebaseUserSearch(String searchText) {


        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<user_model> options=
                new FirebaseRecyclerOptions.Builder<user_model>()
                        .setQuery(firebaseSearchQuery,user_model.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<user_model, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<user_model, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new UsersViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.user_item, viewGroup, false));            }

            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull user_model model) {
                holder.setDetails(getApplicationContext(), model.getName(), model.getStatus(), model.getImage());
                final String user_list_id=getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user_list_id.equals(mcur_id)){
                            Intent intent = new Intent(SearchActivity.this, AccoutSetting.class);
                            intent.putExtra("user_id", user_list_id);

                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                            intent.putExtra("user_id", user_list_id);

                            startActivity(intent);
                        }

                    }
                });

            }


        };
        mResultList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, String userName, String userStatus, String userImage){

            TextView user_name = (TextView) mView.findViewById(R.id.user_item_name);
            TextView user_status = (TextView) mView.findViewById(R.id.user_item_stat);
            ImageView user_image = (ImageView) mView.findViewById(R.id.user_item_img);


            user_name.setText(userName);
            user_status.setText(userStatus);

            Glide.with(ctx).load(userImage).into(user_image);


        }




    }



}
