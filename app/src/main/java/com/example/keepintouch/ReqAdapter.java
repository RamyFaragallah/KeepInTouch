package com.example.keepintouch;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReqAdapter extends ArrayAdapter<Reqmodel> {
    Context context;
    ArrayList<Reqmodel> mreqmodels;

    public ReqAdapter(Context context, ArrayList<Reqmodel> reqmodels) {
        super(context, R.layout.req_item);
        this.context = context;
        this.mreqmodels = reqmodels;
    }

    public class Holder {

        TextView req_name;

        CircleImageView req_prof;
        Button req_approve,req_reject;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Reqmodel data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        Holder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {


            viewHolder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.req_item, parent, false);

            viewHolder.req_name = (TextView) convertView.findViewById(R.id.req_name_layout);
            viewHolder.req_approve = (Button) convertView.findViewById(R.id.req_approve_btn);
            viewHolder.req_reject = (Button) convertView.findViewById(R.id.req_Reject_btn);
            viewHolder.req_prof = (CircleImageView) convertView.findViewById(R.id.req_profile_layout);



            convertView.setTag(viewHolder);

        } else {
            viewHolder = (Holder) convertView.getTag();
        }


        Picasso.get().load(data.getImgUrl()).placeholder(R.drawable.profile).into(viewHolder.req_prof);

        viewHolder.req_name.setText(data.getName());


        // Return the completed view to render on screen
        return convertView;
    }
}
